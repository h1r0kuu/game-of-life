package com.gameoflife.adapters;

import com.gameoflife.gui.GamePanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionAdapter implements MouseMotionListener {

    private final GamePanel gamePanel;


    public MouseMotionAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        gamePanel.setMouseXY(e.getX(), e.getY());
        if(SwingUtilities.isRightMouseButton(e)) {
            gamePanel.clearCell(e.getX(), e.getY());
        } else {
            gamePanel.drawCell(e.getX(), e.getY());
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        gamePanel.setMouseXY(e.getX(), e.getY());
    }
}
