package com.gameoflife.adapters;

import java.awt.event.KeyEvent;

public class KeyAdapter extends java.awt.event.KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        System.out.println('d');
    }
}
