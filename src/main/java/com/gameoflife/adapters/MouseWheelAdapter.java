package com.gameoflife.adapters;

import com.gameoflife.gui.GamePanel;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseWheelAdapter implements MouseWheelListener {

    private GamePanel gamePanel;

    public MouseWheelAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        gamePanel.zooming = true;
        if (e.getWheelRotation() < 0) {
            gamePanel.zoom(1.1); // Zoom in
        } else {
            gamePanel.zoom(0.9); // Zoom out
        }
    }
}
