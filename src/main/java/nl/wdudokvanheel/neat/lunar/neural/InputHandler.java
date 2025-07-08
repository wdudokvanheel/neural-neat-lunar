package nl.wdudokvanheel.neat.lunar.neural;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static nl.wdudokvanheel.neat.lunar.game.ui.ClipBoard.getClipboardText;

/**
 * @Author Wesley Dudok van Heel
 */
public class InputHandler implements KeyListener {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private LunarNeat lunarNeat;

    public InputHandler(LunarNeat lunarNeat) {
        this.lunarNeat = lunarNeat;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            lunarNeat.speed = lunarNeat.speed > 0 ? 0 : 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_1) {
            lunarNeat.speed = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            lunarNeat.speed = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            lunarNeat.speed = 4;
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            lunarNeat.speed = 8;
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
            lunarNeat.speed = 16;
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            lunarNeat.renderGraphics = !lunarNeat.renderGraphics;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            lunarNeat.skipCurrentGame = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            lunarNeat.skipCurrentGame = true;
            lunarNeat.restartSimulation = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            lunarNeat.initialGenome = getClipboardText();
            lunarNeat.skipCurrentGame = true;
            lunarNeat.restartSimulation = true;
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

    }
}
