package nl.wdudokvanheel.neat.lunar.neural;

import nl.wdudokvanheel.neat.lunar.simulation.AbstractLunarSimulation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static nl.wdudokvanheel.neat.lunar.game.ui.ClipBoard.getClipboardText;

public class InputHandler implements KeyListener {
    private AbstractLunarSimulation simulation;

    public InputHandler(AbstractLunarSimulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            simulation.speed = simulation.speed > 0 ? 0 : 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            simulation.speed = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            simulation.speed = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            simulation.speed = 4;
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            simulation.speed = 8;
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
            simulation.speed = 16;
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            simulation.renderGraphics = !simulation.renderGraphics;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            simulation.skipCurrentGame = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            simulation.skipCurrentGame = true;
            simulation.restartSimulation = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            simulation.initialGenome = getClipboardText();
            simulation.skipCurrentGame = true;
            simulation.restartSimulation = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
