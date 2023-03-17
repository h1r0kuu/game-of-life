package com.gameoflife.grid;

import com.gameoflife.entity.Cell;
import com.gameoflife.gui.GameFrame;
import com.gameoflife.gui.GamePanel;

import java.awt.*;

public class Grid {
    public static final int CELL_SIZE = 10;
    private final int rows = GamePanel.GamePanelWidth / CELL_SIZE;
    private final int columns = GamePanel.GamePanelHeight / CELL_SIZE;

    private Cell[][] grids = new Cell[rows][columns];

    public Grid() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                grids[i][j] = new Cell();
            }
        }
    }

    public void update(Graphics g) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(grids[i][j].isAlive()) {
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    public void randomize() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(Math.random() <= 0.1) {
                    grids[i][j].setAlive(true);
                }
            }
        }
    }

    public int getAliveNeighbours(int x, int y) {
        int neighbours = 0;
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) continue;
                if(x + i <= 0 || y + j <= 0 || x + i >= getRows() || y + j >= getColumns()) continue;
                if(grids[x + i][y + j].isAlive()) {
                    neighbours++;
                }
            }
        }
        return neighbours;
    }

    public void nextGeneration() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                int neighbours = getAliveNeighbours(i, j);
                if(neighbours < 2) {
                    grids[i][j].setAlive(false);
                } else if(neighbours > 3) {
                    grids[i][j].setAlive(false);
                } else if(!grids[i][j].isAlive() && neighbours == 3) {
                    grids[i][j].setAlive(true);
                } else {
                    grids[i][j].setAlive(grids[i][j].isAlive());
                }
            }
        }
    }


    public void hoverGrid(int x, int y, Graphics g) {
        g.setColor(Color.RED);
        g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        System.out.println("Grid(" + x + ", " + y + "): " + getAliveNeighbours(x, y));
    }

    public void reviewGrid(int x, int y) {
        grids[x][y].setAlive(true);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Cell[][] getGrids() {
        return grids;
    }
}
