package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.manages.GameManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    public final int rows;
    public final int cols;
    private Cell[][] cells;
    private List<Cell> selectedCells = new ArrayList<>();
    private Cell hoveredCell;
    private boolean showBorders = false;
    private final GraphicsContext graphics;

    public static final Color HOVER_COLOR = Color.web("#ff0000");

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
        double canvasWidth = rows * Cell.CELL_SIZE;
        double canvasHeight = cols * Cell.CELL_SIZE;

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = getCell(i, j);
                Color borderColor = cell.isHovered() ? HOVER_COLOR : GameManager.getCurrentTheme().GRID;
                Color cellColor = cell.getColor();
                drawCell(i, j, borderColor, cellColor);
                if(showBorders) {
                    graphics.setStroke(GameManager.getCurrentTheme().GRID.darker());
                    graphics.setLineWidth(1);
                    if (j % 5 == 0) {
                        double y = j * Cell.CELL_SIZE;
                        graphics.strokeLine(0, y, canvasWidth, y);
                    }
                }
            }
            if(showBorders) {
                if (i % 5 == 0) {
                    double x = i * Cell.CELL_SIZE;
                    graphics.strokeLine(x, 0, x, canvasHeight);
                }
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
                    getCell(i, j).setAlive(true);
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
                if (getCell(neighbourRow, neighbourCol).isAlive()) {
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
                Cell cell = getCell(i,j);
                boolean currentCellIsAlive = cell.isAlive();
                boolean wasAlive = cell.wasAlive();

                if(currentCellIsAlive && (neighbours == 2 || neighbours == 3)) {
                    nextGeneration[i][j] = new Cell(true);
                    nextGeneration[i][j].setWasAlive(false);
                } else if (!currentCellIsAlive && neighbours == 3) {
                    nextGeneration[i][j] = new Cell(true);
                    nextGeneration[i][j].setWasAlive(false);
                } else {
                    nextGeneration[i][j] = new Cell(false);
                    if(currentCellIsAlive) nextGeneration[i][j].setWasAlive(true);
                    if(wasAlive) nextGeneration[i][j].setWasAlive(true);
                }
                nextGeneration[i][j].setLifeTime(cell.getLifetime());
                nextGeneration[i][j].setDeadTime(cell.getDeadTime());
                nextGeneration[i][j].setEvent(cell.getEvent());
                nextGeneration[i][j].update();

                if(cell.isHovered()) {
                    nextGeneration[i][j].setHovered(true);
                    hoveredCell = nextGeneration[i][j];
                }

                if(cell.isSelected()) {
                    selectedCells.remove(cell);
                    nextGeneration[i][j].setSelected(true);
                    selectedCells.add(nextGeneration[i][j]);
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
//            cellInfo.setText(hoveredRow + " " + hoveredCol + "=" + (cell.isAlive() ? 1 : 0) + ";" + (cell.wasAlive() ? 1 : 0));
            hoveredCell = cell;
        }
    }

    public void drawCell(int gridx, int gridy, Color borderColor, Color cellColor) {
        double x = gridx * Cell.CELL_SIZE;
        double y = gridy * Cell.CELL_SIZE;
        double w = Cell.CELL_SIZE;
        double h = Cell.CELL_SIZE;

        double borderSize = 1.0;

        if (showBorders) {
            graphics.setFill(borderColor);
            graphics.fillRect(x, y, w, h);
            graphics.setFill(cellColor);
            graphics.fillRect(x + (borderSize / 2), y + (borderSize / 2), w - borderSize, h - borderSize);
        } else if (getCell(gridx, gridy).isHovered()) {
            graphics.setFill(borderColor);
            graphics.fillRect(x, y, w, h);
            graphics.setFill(cellColor);
            graphics.fillRect(x + (borderSize / 2), y + (borderSize / 2), w - borderSize, h - borderSize);
        } else {
            graphics.setFill(GameManager.getCurrentTheme().BACKGROUND);
            graphics.fillRect(x, y, w, h);
            graphics.setFill(cellColor);
            graphics.fillRect(x, y, w, h);
        }

        if (getCell(gridx, gridy).isSelected()) {
            graphics.setFill(Color.rgb(255, 255, 255, 0.7));
            graphics.fillRect(x, y, w, h);
        }
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }

    public void reviveCell(int x, int y) {
        Cell cell = getCell(x, y);
        cell.setLifeTime(0);
        cell.setDeadTime(0);
        cell.setAlive(true);
        cell.setEvent(CellEvent.REVIEW);

    }

    public void killCell(int x, int y) {
        Cell cell = getCell(x, y);
        cell.setAlive(false);
        cell.setEvent(CellEvent.KILL);
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }
    public void selectRange(double startX, double startY, double endX, double endY) {

        int minX = (int)Math.min(startX, endX);
        int maxX = (int)Math.max(startX, endX);
        int minY = (int)Math.min(startY, endY);
        int maxY = (int)Math.max(startY, endY);
//                int gridx = (int) (x / (Cell.CELL_SIZE * 1.0));
//                int gridy = (int) (y / (Cell.CELL_SIZE * 1.0));
//                Cell cell = getCell(gridx, gridy);
//                cell.setSelected(true);
//                if(!selectedCells.contains(cell)){
//                    selectedCells.add(cell);


        unselectCells();
        graphics.setFill(Color.rgb(255, 255, 255, 0.7));
        graphics.fillRect(minX, minY, maxX - minX, maxY - minY);

//        for (int x = minX; x <= maxX; x++) {
//            for (int y = minY; y <= maxY; y++) {
//
//                }
//            }
//        }



    }

    public void unselectCells() {
        List<Cell> selectedCellsCopy = new ArrayList<>(selectedCells);
        for(Cell c : selectedCellsCopy) {
            selectedCells.remove(c);
            c.setSelected(false);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
}