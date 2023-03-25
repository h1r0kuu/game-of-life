package com.gameoflife.gui;

import com.gameoflife.adapters.MouseClickAdapter;
import com.gameoflife.adapters.MouseMotionAdapter;
import com.gameoflife.adapters.MouseWheelAdapter;
import com.gameoflife.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

public class GamePanel extends JPanel {
    private Grid grid;
    private int mouseX;
    private int mouseY;

    private OptionPanel optionPanel;

    private boolean isPaused = false;
    private int speed = 50;

    private int generation = 0;

    public double scaleFactor = 1.0;

    public boolean zooming = false;

    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;

    public GamePanel() {
        initGamePanel();
    }

    public GamePanel(OptionPanel optionPanel) {
        this.optionPanel = optionPanel;
        initGamePanel();
    }

    public void initGamePanel() {
        addMouseMotionListener(new MouseMotionAdapter(this));
        addMouseListener(new MouseClickAdapter(this));
        addMouseWheelListener(new MouseWheelAdapter(this));

        setFocusable(true);
        requestFocus();
        grid = new Grid(100, 100, Grid.DEFAULT_CELL_SIZE);
        randomizeGrid(20);

    }


    public void setOptionPanel(OptionPanel optionPanel) {
        this.optionPanel = optionPanel;
    }

    public void drawCell(int x, int y) {
        int gridX = mouseX / grid.getCellSize();
        int gridY = mouseY / grid.getCellSize();
        grid.reviveCell(gridX, gridY);
    }

    public void clearCell(int x, int y) {
        int gridX = mouseX / grid.getCellSize();
        int gridY = mouseX / grid.getCellSize();
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

    public void zoom(double factor) {
        scaleFactor *= factor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

            AffineTransform at = new AffineTransform();

            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            double zoomDiv = scaleFactor / scaleFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(scaleFactor, scaleFactor);
            prevZoomFactor = zoomFactor;
            g2d.transform(at);
            zooming = false;


        if(this.grid != null) {
            grid.update(g2d);
            int cellX = (int) (mouseX / (grid.getCellSize() * scaleFactor));
            int cellY = (int) (mouseY / (grid.getCellSize() * scaleFactor));
            optionPanel.updateCellInfo(cellX, cellY, grid.getAliveNeighbours(cellX, cellY));
            grid.hoverGrid(cellX, cellY, g2d);
        }
        g2d.dispose();

    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
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