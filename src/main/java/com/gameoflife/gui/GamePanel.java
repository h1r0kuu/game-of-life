package com.gameoflife.gui;

import com.gameoflife.adapters.MouseClickAdapter;
import com.gameoflife.adapters.MouseMotionAdapter;
import com.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private Grid grid;
    private int mouseX;
    private int mouseY;
    public static final int GamePanelWidth = (int)(GameFrame.WIDTH * 0.7);
    public static final int GamePanelHeight = GameFrame.HEIGHT;

    private boolean isPaused = false;

    public GamePanel() {
        addMouseMotionListener(new MouseMotionAdapter(this));
        addMouseListener(new MouseClickAdapter(this));
        setFocusable(true);
        requestFocus();
        setBounds(0, 0, GamePanelWidth, GamePanelHeight);
        this.grid = new Grid();
        this.grid.randomize();
    }

    public void drawCell(int x, int y) {
        int gridX = mouseX / Grid.CELL_SIZE;
        int gridY = mouseY / Grid.CELL_SIZE;;
        grid.reviewGrid(gridX, gridY);
    }

    public void setMouseXY(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        int gridX = mouseX / Grid.CELL_SIZE;
        int gridY = mouseY / Grid.CELL_SIZE;
        grid.hoverGrid(gridX, gridY, getGraphics());
        repaint();
    }

//    public void checkGridHover(Graphics g) {
//        int gridX = mouseX / Grid.CELL_SIZE;
//        int gridY = mouseY / Grid.CELL_SIZE;;
//        grid.hoverGrid(gridX, gridY, g);
//    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        grid.update(g);
        if(isPaused) {
            grid.nextGeneration();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}