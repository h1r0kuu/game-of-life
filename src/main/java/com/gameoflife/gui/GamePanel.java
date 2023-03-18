package com.gameoflife.gui;

import com.gameoflife.adapters.MouseClickAdapter;
import com.gameoflife.adapters.MouseMotionAdapter;
import com.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GamePanel extends JPanel {

    private Grid grid;
    private int mouseX;
    private int mouseY;

    private boolean isPaused = false;


    public GamePanel() {
        setLayout(new BorderLayout());
        addMouseMotionListener(new MouseMotionAdapter(this));
        addMouseListener(new MouseClickAdapter(this));
        setFocusable(true);
        requestFocus();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int numRows = getWidth() / Grid.DEFAULT_CELL_SIZE;
                int numCols = getHeight() / Grid.DEFAULT_CELL_SIZE;
                grid = new Grid(numRows, numCols, Grid.DEFAULT_CELL_SIZE);
                randomizeGrid(20);
            }
        });
    }

    public void drawCell(int x, int y) {
        int gridX = x / grid.getCellSize();
        int gridY = y / grid.getCellSize();
        grid.reviveCell(gridX, gridY);
    }

    public void clearCell(int x, int y) {
        int gridX = x / grid.getCellSize();
        int gridY = y / grid.getCellSize();
        grid.killCell(gridX, gridY);
    }

    public void clearGrid() {
        grid.clearGrid();
    }

    public void randomizeGrid(double probability) {
        grid.randomize(probability);
    }

    public void setMouseXY(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        grid.update(g);
        int cellX = mouseX / grid.getCellSize();
        int cellY = mouseY / grid.getCellSize();
        grid.hoverGrid(cellX, cellY, g);
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