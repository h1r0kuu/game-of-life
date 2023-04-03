package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.entity.Cell;

public class Grid {
    public static final int DEFAULT_CELL_SIZE = 10;

    private int rows;
    private int columns;
    private int cellSize;
    private Cell[][] grids;


    public Grid(int rows, int columns) {
        this(rows, columns, DEFAULT_CELL_SIZE);
    }

    public Grid(int rows, int columns, int cellSize) {
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        this.grids = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grids[i][j] = new Cell();
            }
        }
    }

    public void clearGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grids[i][j] = new Cell();
            }
        }
    }
    public void randomize(double probability) {
        clearGrid();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Math.random() <= probability / 100) {
                    grids[i][j].setAlive(true);
                }
            }
        }
    }

    public int getAliveNeighbours(int row, int column) {
        int neighbours = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int neighbourRow = row + i;
                int neighbourCol = column + j;
                if (neighbourRow < 0 || neighbourRow >= rows ||
                        neighbourCol < 0 || neighbourCol >= columns) {
                    continue;
                }
                if (grids[neighbourRow][neighbourCol].isAlive()) {
                    neighbours++;
                }
            }
        }
        return neighbours;
    }

    public void nextGeneration() {
        Cell[][] nextGeneration = new Cell[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int neighbours = getAliveNeighbours(i, j);
                boolean currentCellIsAlive = grids[i][j].isAlive();

                if (currentCellIsAlive && neighbours < 2) {
                    nextGeneration[i][j] = new Cell(false);
                } else if (currentCellIsAlive && (neighbours == 2 || neighbours == 3)) {
                    nextGeneration[i][j] = new Cell(true);
                } else if (currentCellIsAlive && neighbours > 3) {
                    nextGeneration[i][j] = new Cell(false);
                } else if (!currentCellIsAlive && neighbours == 3) {
                    nextGeneration[i][j] = new Cell(true);
                } else {
                    nextGeneration[i][j] = new Cell(currentCellIsAlive);
                }
            }
        }
        grids = nextGeneration;
    }


    public void reviveCell(int x, int y) {
        grids[x][y].setAlive(true);
    }

    public void killCell(int x, int y) {
        grids[x][y].setAlive(false);
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Cell getCell(int row, int column) {
        return grids[row][column];
    }

    public Cell[][] getGrids() {
        return grids;
    }
}