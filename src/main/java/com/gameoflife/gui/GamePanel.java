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

    public GamePanel() {
        addMouseMotionListener(new MouseMotionAdapter(this));
        addMouseListener(new MouseClickAdapter(this));
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
    }

    public void checkGridHover(Graphics g) {
        int gridX = mouseX / Grid.CELL_SIZE;
        int gridY = mouseY / Grid.CELL_SIZE;;
        grid.hoverGrid(gridX, gridY, g);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        grid.update(g);
        grid.nextGeneration();
        checkGridHover(g);
    }
}