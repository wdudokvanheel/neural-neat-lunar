package nl.wdudokvanheel.neat.lunar.simulation;

import java.util.Random;

public class AbstractLunarSimulation {
    protected final Random random = new Random();

    public int speed = 16;
    public boolean renderGraphics = true;
    public boolean restartSimulation = false;
    public boolean skipCurrentGame = false;
    public String initialGenome = null;
}
