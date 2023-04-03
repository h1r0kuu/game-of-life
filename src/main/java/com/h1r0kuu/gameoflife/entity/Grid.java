package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.OptionController;
import com.h1r0kuu.gameoflife.OptionState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class Grid {
    private final int rows;
    private final int cols;
    private Cell[][] cells;
    private Random random = new Random();
    private Cell hoveredCell;
    private final GraphicsContext graphics;

    public static final Color BORDER_COLOR = Color.web("#000000");
    public static final Color HOVER_COLOR = Color.web("#ff0000");

    public Grid(double canvasWidth, double canvasHeight, GraphicsContext graphics) {
        this((int) Math.floor(canvasHeight / Cell.CELL_SIZE),
                (int) Math.floor(canvasWidth / Cell.CELL_SIZE),
                graphics);
    }

    public Grid(int rows, int cols, GraphicsContext graphics) {
        this.rows = rows;
        this.cols = cols;
        this.graphics = graphics;
        cells = new Cell[rows][cols];
    }

    public void init() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void update() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                Color borderColor = cell.isHovered() ? HOVER_COLOR : BORDER_COLOR;
                Color cellColor = cell.isAlive() ? Cell.ALIVE_CELL_COLOR : Cell.DEAD_CELL_COLOR;
                OptionController.fillCell(graphics, borderColor, cellColor, i, j);
            }
        }
    }

    public void clearGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public void randomize(double probability) {
        clearGrid();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Math.random() <= probability / 100) {
                    cells[i][j].setAlive(true);
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
                        neighbourCol < 0 || neighbourCol >= cols) {
                    continue;
                }
                if (cells[neighbourRow][neighbourCol].isAlive()) {
                    neighbours++;
                }
            }
        }
        return neighbours;
    }

    public void nextGeneration() {
        Cell[][] nextGeneration = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int neighbours = getAliveNeighbours(i, j);
                boolean currentCellIsAlive = cells[i][j].isAlive();

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

                if(cells[i][j].isHovered()) {
                    nextGeneration[i][j].setHovered(true);
                    hoveredCell = nextGeneration[i][j];
                }

            }
        }
        cells = nextGeneration;
    }

    public void hoverGrid(int hoveredRow, int hoveredCol) {
        Cell cell = getCell(hoveredRow, hoveredCol);
        if(cell != null) {
            if(hoveredCell != null && hoveredCell != cell) hoveredCell.setHovered(false);
            cell.setHovered(true);
            hoveredCell = cell;
        }
    }

    public Cell getHoveredCell() {
        return hoveredCell;
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }

    public void reviveCell(int x, int y) {
        cells[x][y].setAlive(true);
    }

    public void killCell(int x, int y) {
        cells[x][y].setAlive(false);
    }
}
