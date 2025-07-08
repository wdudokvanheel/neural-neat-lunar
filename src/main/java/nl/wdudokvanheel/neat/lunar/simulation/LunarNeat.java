package nl.wdudokvanheel.neat.lunar.simulation;

import nl.wdudokvanheel.neat.lunar.game.level.RandomizedSingleMountainLevel;
import nl.wdudokvanheel.neat.lunar.game.logic.CollisionDetection;
import nl.wdudokvanheel.neat.lunar.game.logic.LunarGame;
import nl.wdudokvanheel.neat.lunar.game.logic.score.ScoreCalculator;
import nl.wdudokvanheel.neat.lunar.game.model.Lander;
import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neat.lunar.game.ui.ClipBoard;
import nl.wdudokvanheel.neat.lunar.game.ui.LunarWindow;
import nl.wdudokvanheel.neat.lunar.neural.*;
import nl.wdudokvanheel.neural.neat.NeatConfiguration;
import nl.wdudokvanheel.neural.neat.NeatContext;
import nl.wdudokvanheel.neural.neat.NeatEvolution;
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
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class LunarNeat extends AbstractLunarSimulation{
    private Logger logger = LoggerFactory.getLogger(LunarNeat.class);

    NeatContext<NeatLander> context;

    private LunarWindow lunarWindow;
    private NetworkInfoPanel infoPanel;
    private GenomeSerializationPanel genomePanel;


    private final Random r = new Random();

    ExecutorService executor = new ForkJoinPool(10);

    public static void main(String[] args) {
        new LunarNeat();
    }

    public LunarNeat() {
        logger.debug("Starting Lunar Lander NEAT evolution");

        lunarWindow = new LunarWindow();
        infoPanel = new NetworkInfoPanel();
        genomePanel = new GenomeSerializationPanel();
        JPanel east = new JPanel();
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.add(infoPanel);
        east.add(genomePanel);
        lunarWindow.add(east, BorderLayout.EAST);
        lunarWindow.pack();
        lunarWindow.addKeyListener(new InputHandler(this));

        SerializationService serializationService = new SerializationService();

        // Check copy+paste buffer for champion valid genome
        String clipboard = ClipBoard.getClipboardText();
        if (clipboard != null && serializationService.deserialize(clipboard) != null) {
            logger.info("Found valid genome on clipboard");
            initialGenome = clipboard;
            genomePanel.setText(clipboard);
        }

        while (true) {
            lunarWindow.setTitle("Lunar Lander NEAT");
            restartSimulation = false;
            context = NeatEvolution.createContext(new LanderFactory(), generateConfiguration());

            Genome blueprint = createInitialGenome(context.innovationService);
            Genome champion = null;

            if (this.initialGenome != null) {
                champion = serializationService.deserialize(this.initialGenome);
                context.innovationService.importFromGenome(champion);
            }

            if (champion != null) {
                logger.info("Starting with a champion");
                infoPanel.setLander(new NeatLander(champion));
            } else {
                infoPanel.setLander(new NeatLander(blueprint));
            }
            infoPanel.repaint();

            NeatEvolution.generateInitialPopulation(context, new NeatLander(blueprint), champion == null ? null : new NeatLander(champion));

            //Run game
            for (int i = 0; i < 100000; i++) {
                LunarGame game = startNewGame();
                if (restartSimulation) {
                    break;
                }
                setFitness();
                logger.debug("\t\tFittest of generation #{}: {}/{} Winners: {}",
                        context.generation,
                        context.getFittestCreature().getFitness(),
                        ScoreCalculator.getTotalWeight(),
                        getWinners(game));

                if (getWinners(game) > 0) {
                    genomePanel.setText(serializationService.serialize(getWinningGenome(game)));
                    // Pause when we have a winner
                    speed = 0;
                }

                genomePanel.setText(serializationService.serialize(context.getFittestCreature().getGenome()));
                infoPanel.setLander(context.getFittestCreature());

                NeatEvolution.nextGeneration(context);
            }
        }
    }

    private NeatConfiguration generateConfiguration() {
        NeatConfiguration conf = new NeatConfiguration();
        conf.populationSize = 1000;
        conf.targetSpecies = 20;
        conf.adjustSpeciesThreshold = true;
        conf.speciesThreshold = 1.5;
        conf.mutateAddConnectionProbability = 0.6;
        conf.mutateAddNeuronProbability = 0.15;
        conf.mutateToggleConnectionProbability = 0.05;
        conf.maxSpeciesThreshold = 100000000;
        conf.ultraChampionClones = 100;
        conf.minSpeciesThreshold = 0.1;
        conf.interspeciesCrossover = 0.05;
        conf.mutateWeightProbability = 0.5;
        conf.mutateRandomizeWeightsProbability = 0.3;
        conf.eliminateStagnantSpecies = false;
        conf.bottomElimination = 0.1;
        conf.minimumSpeciesSizeForChampionCopy = 5;
        conf.copyChampionsAllSpecies = true;
        conf.setInitialLinks = true;
        conf.initialLinkActiveProbability = 0.5;
        conf.initialLinkWeight = 1.0;
        return conf;
    }

    private int getWinners(LunarGame game) {
        return game.landers.stream().filter(lander -> lander.reachedGoal).collect(Collectors.toList()).size();
    }

    private Genome getWinningGenome(LunarGame game) {
        return ((NeatLander) game.landers.stream().filter(lander -> lander.reachedGoal).collect(Collectors.toList()).getFirst()).getGenome();
    }

    private void setFitness() {
        for (NeatLander lander : context.creatures) {
            double score = lander.score;
            lander.setFitness(score);
        }
    }

    private void destroyNoFuel() {
        for (NeatLander lander : context.creatures) {
            if (!lander.alive || lander.reachedGoal)
                continue;
            if (lander.fuel <= 0) {
                lander.alive = false;
            }
        }
    }

    private void checkStagnantLander() {
        for (NeatLander lander : context.creatures) {
            if (!lander.alive || lander.reachedGoal)
                continue;

            if (lander.lastPosition.equals(lander.position.floor())) {
                lander.stagnant++;
                if (lander.stagnant >= 60) {
                    lander.alive = false;
                }
            } else {
                lander.stagnant = 0;
            }
            lander.lastPosition.set(lander.position);
        }
    }

    private LunarGame startNewGame() {
        LunarGame game = new LunarGame(new RandomizedSingleMountainLevel());
        lunarWindow.setGame(game);

        for (NeatLander lander : context.creatures) {
            game.addLander(lander);
        }

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
        NeatLander fittestCreature = context.getFittestCreature();
        lunarWindow.setTitle("Lunar Touchdown :: Generation " + context.generation + " :: " + context.creatures.size() + " creatures :: " + context.species.size() + "/" + context.configuration.targetSpecies + " Species (" + context.configuration.speciesThreshold + ") :: Fitness " + fittestCreature.getFitness());
        return game;
    }

    private void logicUpdate(LunarGame game) {
        //Run network on each lander to set input/output
        int landerCount = 0;
        for (Lander lander : game.landers) {
            if (lander.alive && !lander.reachedGoal) {
                landerCount++;
            }
        }

        CountDownLatch latch = new CountDownLatch(landerCount); // Initialize the CountDownLatch with the number of tasks

        for (Lander lander : game.landers) {
            if (lander.alive && !lander.reachedGoal) {
                executor.submit(() -> {
                    updateNeuralNetwork(game, (NeatLander) lander);
                    latch.countDown(); // Decrement the latch count after task completion
                });
            }
        }

        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            // Handle the InterruptedException
        }

        //Kill landers after 2 secs without movement
        checkStagnantLander();

        // Kill landers out of fuel
        destroyNoFuel();

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
        return r.nextDouble(-1, 1);
    }
}
