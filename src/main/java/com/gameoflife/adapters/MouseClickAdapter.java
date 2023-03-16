package com.gameoflife.adapters;

import com.gameoflife.gui.GamePanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseClickAdapter extends MouseAdapter {

    private GamePanel gamePanel;

    public MouseClickAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gamePanel.drawCell(e.getX(), e.getY());
    }
}
