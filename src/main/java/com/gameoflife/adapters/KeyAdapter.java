package com.gameoflife.adapters;

import com.gameoflife.Game;

import java.awt.event.KeyEvent;

public class KeyAdapter extends java.awt.event.KeyAdapter {

    private Game game;

    public KeyAdapter(Game game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            game.togglePause();
        }
    }
}
