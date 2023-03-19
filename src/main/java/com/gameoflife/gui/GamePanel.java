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

    private OptionPanel optionPanel;

    private boolean isPaused = false;
    private int speed = 50;

    private int generation = 0;


    public GamePanel() {
        initGamePanel();
    }

    public GamePanel(OptionPanel optionPanel) {
        this.optionPanel = optionPanel;
        initGamePanel();
    }

    public void initGamePanel() {
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


    public void setOptionPanel(OptionPanel optionPanel) {
        this.optionPanel = optionPanel;
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
        this.generation = 0;
        grid.clearGrid();
    }

    public void randomizeGrid(double probability) {
        this.generation = 0;
        grid.randomize(probability);
    }

    public void setMouseXY(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getGeneration() {
        return this.generation;
    }

    public void nextGeneration() {
        if(this.grid != null) {
            this.generation += 1;
            grid.nextGeneration();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.grid != null) {
            grid.update(g);
            int cellX = mouseX / grid.getCellSize();
            int cellY = mouseY / grid.getCellSize();
            optionPanel.updateCellInfo(cellX, cellY, grid.getAliveNeighbours(cellX, cellY));
            grid.hoverGrid(cellX, cellY, g);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}