package nl.wdudokvanheel.neat.lunar.simulation;

import nl.wdudokvanheel.neat.lunar.game.level.RandomizedSingleMountainLevel;
import nl.wdudokvanheel.neat.lunar.game.logic.CollisionDetection;
import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neat.lunar.game.ui.ClipBoard;
import nl.wdudokvanheel.neat.lunar.game.ui.LunarWindow;
import nl.wdudokvanheel.neat.lunar.neural.InputHandler;
import nl.wdudokvanheel.neat.lunar.neural.NeatLander;
import nl.wdudokvanheel.neat.lunar.neural.NetworkInfoPanel;
import nl.wdudokvanheel.neural.neat.genome.Genome;
import nl.wdudokvanheel.neural.neat.genome.HiddenNeuronGene;
import nl.wdudokvanheel.neural.neat.genome.InputNeuronGene;
import nl.wdudokvanheel.neural.neat.genome.OutputNeuronGene;
import nl.wdudokvanheel.neural.neat.service.GenomeBuilder;
import nl.wdudokvanheel.neural.neat.service.InnovationService;
import nl.wdudokvanheel.neural.neat.service.SerializationService;
import nl.wdudokvanheel.neural.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class SingleGenomeBenchmark extends AbstractLunarSimulation {
    private Logger logger = LoggerFactory.getLogger(SingleGenomeBenchmark.class);

    private LunarWindow lunarWindow;
    private NetworkInfoPanel infoPanel;

    private int wins = 0;
    private int fails = 0;

    public static void main(String[] args) {
        new SingleGenomeBenchmark();
    }

    public SingleGenomeBenchmark() {
        logger.debug("Starting Lunar Lander NEAT evolution");

        lunarWindow = new LunarWindow();
        infoPanel = new NetworkInfoPanel();
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.add(infoPanel);
        lunarWindow.add(east, BorderLayout.EAST);
        lunarWindow.pack();
        lunarWindow.addKeyListener(new InputHandler(this));

        SerializationService serializationService = new SerializationService();

        // Check copy+paste buffer for champion valid genome
        String clipboard = ClipBoard.getClipboardText();
        if (clipboard != null && serializationService.deserialize(clipboard) != null) {
            logger.info("Found valid genome on clipboard");
            initialGenome = clipboard;
        } else {
            System.exit(-1);
        }

        lunarWindow.setTitle("Lunar Lander NEAT");
        restartSimulation = false;

        Genome champion = serializationService.deserialize(this.initialGenome);

        logger.info("Starting with a champion");
        infoPanel.setLander(new NeatLander(champion));
        infoPanel.repaint();

        // Run game
        for (int i = 0; i < 100_000; i++) {
            LunarGame game = startNewGame(champion);
            Lander lander = game.landers.getFirst();

            if (lander.alive && lander.reachedGoal) {
                wins++;
                logger.info("Win  (#" + wins + ")");
            } else {
                fails++;
                logger.info("Fail (#" + fails + ")");
            }

            // Update title with stats
            int total = wins + fails;
            double winRate = total > 0 ? (wins * 100.0 / total) : 0.0;
            lunarWindow.setTitle(
                    String.format("Wins: %d  Fails: %d  Win rate: %.2f%%", wins, fails, winRate)
            );

            if(restartSimulation) {
                wins = 0;
                fails = 0;
                lunarWindow.setTitle("Lunar Lander NEAT");
                champion = serializationService.deserialize(this.initialGenome);
                restartSimulation = false;
            }
        }
    }


    private void destroyNoFuel(Lander lander) {
        if (!lander.alive || lander.reachedGoal)
            return;
        if (lander.fuel <= 0) {
            lander.alive = false;
        }
    }

    private LunarGame startNewGame(Genome genome) {
        LunarGame game = new LunarGame(new RandomizedSingleMountainLevel());
        lunarWindow.setGame(game);
        NeatLander lander = new NeatLander(genome);
        game.addLander(lander);

        double lastUpdate = System.currentTimeMillis();
        lunarWindow.repaint();

        while (game.running && game.frame < 60 * 120) {
            for (int i = 0; i < speed; i++) {
                logicUpdate(game);
            }

            //Only render and sleep when necessary
            if (renderGraphics && !skipCurrentGame) {
                //Render game
                lunarWindow.repaint();

                try {
                    Thread.sleep((long) Math.max(0, (1000 / (60) - (System.currentTimeMillis() - lastUpdate))));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lastUpdate = System.currentTimeMillis();
            }
        }
        skipCurrentGame = false;
        lunarWindow.setTitle("Lunar Touchdown");
        return game;
    }

    private void logicUpdate(LunarGame game) {
        //Run network on each lander to set input/output
        for (Lander lander : game.landers) {
            if (lander.alive && !lander.reachedGoal) {
                updateNeuralNetwork(game, (NeatLander) lander);
            }
        }

        // Kill landers out of fuel
        destroyNoFuel(game.landers.getFirst());

        //Update game
        game.update();
    }

    private void updateNeuralNetwork(LunarGame game, NeatLander lander) {
        Vector2d center = lander.position.add(LunarGame.LANDER_WIDTH / 2, LunarGame.LANDER_HEIGHT / 2);

        Network network = lander.network;
        network.resetNeuronValues();

        double input[] = new double[13];
        int count = 0;

        Vector2d[] directions = LunarGame.DIRECTIONS;
        //8 Directions sensors
        for (; count < directions.length; count++) {
            Vector2d direction = directions[count];
            Vector2d target = center.floor().add(direction.scale(LunarGame.SENSOR_LENGTH)).rotate(center, lander.angle);
            input[count] = 1 - CollisionDetection.getRelativeTargetPosition(game.level.getAllLines(), center, target);
        }
        //Lander's angle
        input[count++] = lander.angle / 360;
        //Lander's speed
        input[count++] = Math.min(100, lander.speed.magnitude()) / 100;
        //Target distance
        Vector2d position = lander.position.add(new Vector2d(LunarGame.LANDER_WIDTH / 2, LunarGame.LANDER_HEIGHT));
        Vector2d delta = position.subtract(game.level.target.start.add(LunarGame.PLATFORM_WIDTH / 2, 0));

        //Target x distance
        input[count++] = delta.x / LunarGame.GAME_WIDTH;
        //Target y distance
        input[count++] = delta.y / LunarGame.GAME_HEIGHT;

        // Bias
        input[count++] = 1.0;

        network.setInput(input);
        double[] outputs = network.getOutputs();
        lander.inputThrust = outputs[0];
        lander.inputSteering = (outputs[1] * 2) - 1;

        String values[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "Angle", "Speed", "Target angle", "Target dist"};
        String debug = "Score: " + lander.score + "\n";
        for (int i = 0; i < values.length; i++) {
            debug += values[i] + ": " + input[i] + "\n";
        }
        debug += "Out thrust: " + lander.inputThrust + "\n";
        debug += "Out steering: " + lander.inputSteering;
        lunarWindow.setDebugText(debug);
    }

    private Genome createInitialGenome(InnovationService innovation) {
        //Inputs:
        // 8 Direction collision detectors
        // Current speed
        // Current angle
        // Target x distance
        // Target y distance
        // Bias

        //Outputs:
        // Thrust
        // Steering

        GenomeBuilder builder = new GenomeBuilder(innovation);
        InputNeuronGene[] inputs = builder.addInputNeurons(13);
        HiddenNeuronGene[] hidden = builder.addHiddenNeurons(3);
        OutputNeuronGene[] outputs = builder.addOutputNeurons(2);

        for (InputNeuronGene input : inputs) {
            for (HiddenNeuronGene hiddenNeuronGene : hidden) {
                builder.addConnection(input, hiddenNeuronGene, 0);
            }
        }

        for (HiddenNeuronGene hiddenNeuronGene : hidden) {
            for (OutputNeuronGene output : outputs) {
                builder.addConnection(hiddenNeuronGene, output, 0);
            }
        }

        return builder.getGenome();
    }

    private double randomWeight() {
        return random.nextDouble(-1, 1);
    }
}
