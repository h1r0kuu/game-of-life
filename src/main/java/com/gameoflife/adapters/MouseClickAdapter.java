package com.gameoflife.adapters;

import com.gameoflife.gui.GamePanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseClickAdapter extends MouseAdapter {

    private final GamePanel gamePanel;

    public MouseClickAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            gamePanel.clearCell(e.getX(), e.getY());
        } else {
            gamePanel.drawCell(e.getX(), e.getY());
        }
    }
}