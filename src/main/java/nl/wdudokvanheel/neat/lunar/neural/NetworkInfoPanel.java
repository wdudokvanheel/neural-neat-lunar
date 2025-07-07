package nl.wdudokvanheel.neat.lunar.neural;

import nl.wdudokvanheel.neat.lunar.game.model.Vector2d;
import nl.wdudokvanheel.neural.network.Network;
import nl.wdudokvanheel.neural.network.neuron.Connection;
import nl.wdudokvanheel.neural.network.neuron.InputNeuron;
import nl.wdudokvanheel.neural.network.neuron.Neuron;
import nl.wdudokvanheel.neural.network.neuron.OutputNeuron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class NetworkInfoPanel extends JPanel {
    private Logger logger = LoggerFactory.getLogger(NetworkInfoPanel.class);

    public static int WIDTH = 800;
    public static int HEIGHT = 400;

    private Color activeBackground = Color.decode("#eaeaea");
    private Color inActiveBackground = Color.decode("#c1c1c1");
    private Color colorPositiveConnection = Color.decode("#11cf20");
    private Color colorNegativeConnection = Color.decode("#cf0f4c");
    private Color separator = Color.decode("#1d1d1d");

    private NeatLander lander;

    private int networkPos = 10;

    private Network network;
    private int neuronSize = 15;
    private int neuronSpacing = 15;
    private int layers = 0;
    private int maxNeuronsPerLayer = 0;
    private Color myColor;

    public NetworkInfoPanel() {
        this.myColor = Color.decode("#6c0dcf");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getBackgroundColor());
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        if (lander == null)
            return;

        g.setColor(myColor);
        drawConnections(g2, network);
        drawNeurons(g2, network);
    }

    private void drawNeurons(Graphics2D g, Network network) {
        for (Neuron neuron : network.getAllNeurons()) {
            Vector2d position = getNeuronPosition(neuron);

            g.setStroke(new BasicStroke(2));
            g.setColor(separator);
            g.drawOval((int) position.x, (int) position.y, neuronSize, neuronSize);
            g.setColor(getNeuronColor(neuron));
            g.fillOval((int) position.x, (int) position.y, neuronSize, neuronSize);
//            drawCenter(g, "#" + neuron.getId(), position.x + neuronSize / 2, position.y + neuronSize / 2 - 15);
        }
    }

    private Vector2d getNeuronPosition(Neuron neuron) {
        int index = getIndex(network, neuron);
        int layerIndex;
        int neuronsInLayer;

        if (neuron instanceof InputNeuron) {
            layerIndex = 0;
            neuronsInLayer = network.inputNeurons.size();
        } else if (neuron instanceof OutputNeuron) {
            layerIndex = layers + 1;
            neuronsInLayer = network.outputNeurons.size();
        } else {
            layerIndex = neuron.layer;
            neuronsInLayer = getNeuronsPerLayer(neuron.layer);
        }

        int cellHeight = neuronSize + neuronSpacing;
        int totalHeight = neuronsInLayer * neuronSize + (neuronsInLayer - 1) * neuronSpacing;
        int startY = (HEIGHT - totalHeight) / 2;

        int x = networkPos + (layerIndex * neuronSpacing * 2);
        int y = startY + index * cellHeight;

        return new Vector2d(x, y);
    }

    private void drawConnections(Graphics2D g, Network network) {
        for (Neuron neuron : network.getAllNeurons()) {
            if (neuron instanceof InputNeuron)
                continue;

            for (Connection input : neuron.inputs) {
                Vector2d source = getNeuronPosition(input.source).add(neuronSize / 2);
                Vector2d target = getNeuronPosition(input.target).add(neuronSize / 2);

                Color color;
                if (input.weight > 0) {
                    color = colorPositiveConnection;
                } else {
                    color = colorNegativeConnection;
                }

                double distance = Math.sqrt(Math.pow(target.x - source.x, 2) + Math.pow(target.y - source.y, 2));

                double unitX = (target.x - source.x) / distance;
                double unitY = (target.y - source.y) / distance;

                double x1 = source.x + neuronSize / 2 * unitX;
                double y1 = source.y + neuronSize / 2 * unitY;
                double x2 = target.x - neuronSize / 2 * unitX;
                double y2 = target.y - neuronSize / 2 * unitY;

                g.setStroke(new BasicStroke((float) ((Math.max(0.3, Math.abs(input.weight))))));
                g.setColor(color);
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            }
        }
    }

    private int getIndex(Network network, Neuron neuron) {
        if (neuron instanceof InputNeuron) {
            return network.inputNeurons.indexOf(neuron);
        }
        if (neuron instanceof OutputNeuron) {
            return network.outputNeurons.indexOf(neuron);
        }

        int index = 0;
        for (Neuron hiddenNeuron : network.hiddenNeurons) {
            if (hiddenNeuron == neuron)
                return index;
            if (hiddenNeuron.layer == neuron.layer)
                index++;
        }

        return index;
    }

    private Color getNeuronColor(Neuron neuron) {
        int alpha = 255;
        if (neuron instanceof InputNeuron) {
//            double val = bird.inputValues[network.inputNeurons.indexOf(neuron)];
//            alpha = (int) Math.round(Math.abs(val * 255));
//            if (val > 0)
//                return new Color(17, 207, 32, Math.min(255, alpha));
//            else {
//                return new Color(207, 15, 76, alpha);
//            }
            return Color.decode("#134ccf");
        }
        if (neuron instanceof OutputNeuron) {
//            double val = lander.outputValue;
//            if (val < 0.5) {
//                return new Color(207, 15, 76);
//            } else {
            return new Color(17, 207, 32, Math.min(255, alpha));
//            }
        }
        return Color.decode("#134ccf");
    }

    public void setLander(NeatLander lander) {
        this.lander = lander;
        this.network = new Network(lander.getGenome());
        this.layers = network.getLayers();
        this.maxNeuronsPerLayer = Math.max(network.inputNeurons.size(), network.outputNeurons.size());
        for (int i = 0; i < layers; i++) {
            int neurons = getNeuronsPerLayer(i);
            if (neurons > maxNeuronsPerLayer)
                maxNeuronsPerLayer = neurons;
        }
    }

    private int getNeuronsPerLayer(int layer) {
        int total = 0;
        for (Neuron hiddenNeuron : network.hiddenNeurons) {
            if (hiddenNeuron.layer == layer) {
                total++;
            }
        }
        return total;
    }

    private Color getBackgroundColor() {
        if (lander == null || !lander.alive) {
            return inActiveBackground;
        }

        return activeBackground;
    }
}

