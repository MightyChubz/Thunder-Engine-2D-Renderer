package com.chubz.renderer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * I recommend not using this class and only view it as a template on how to make input.
 */
public class InputHandler implements KeyListener {
    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public InputHandler(Game game) {
        game.addKeyListener(this);
    }

    public void keyPressed(KeyEvent e) {
        toggle(e, true);
    }

    public void keyReleased(KeyEvent e) {
        toggle(e, false);
    }

    public void releaseAll() {
        up = down = left = right = false;
    }

    private void toggle(KeyEvent e, boolean pressed) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP)
            up = pressed;

        if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN)
            down = pressed;

        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT)
            left = pressed;

        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT)
            right = pressed;
    }

    // unused
    public void keyTyped(KeyEvent e) {}
}
