package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.manages.PatternManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    private static final Logger logger = LogManager.getLogger(Grid.class);

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
        PatternManager patternManager = new PatternManager();
        Pattern pattern = patternManager.getByName("Shark loop");
        drawPattern(pattern, getRows() / 2, getCols() / 2);
        logger.info("Grid init");
    }


    public void update() {
        double canvasWidth = rows * Cell.CELL_SIZE;
        double canvasHeight = cols * Cell.CELL_SIZE;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = getCell(i, j);
                Color borderColor = cell.isHovered() ? HOVER_COLOR : GameManager.getCurrentTheme().GRID;
                Color cellColor = cell.getColor();
                drawCell(j, i, borderColor, cellColor);
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

    public void drawCell(int row, int col, Color borderColor, Color cellColor) {
        double w = Cell.CELL_SIZE;
        double h = Cell.CELL_SIZE;

        double x = row * w;
        double y = col * h;

        double borderSize = 1.0;

        if (showBorders) {
            graphics.setFill(borderColor);
            graphics.fillRect(x, y, w, h);
            graphics.setFill(cellColor);
            graphics.fillRect(x + (borderSize / 2), y + (borderSize / 2), w - borderSize, h - borderSize);
        } else if (getCell(row, col).isHovered()) {
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

        if (getCell(row, col).isSelected()) {
            graphics.setFill(Color.rgb(255, 255, 255, 0.7));
            graphics.fillRect(x, y, w, h);
        }
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }

    public void reviveCell(int row, int col) {
        Cell cell = getCell(row, col);
        cell.setLifeTime(0);
        cell.setDeadTime(0);
        cell.setAlive(true);
        cell.setEvent(CellEvent.REVIEW);

    }

    public void killCell(int row, int col) {
        Cell cell = getCell(row, col);
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
    }

    public void unselectCells() {
        List<Cell> selectedCellsCopy = new ArrayList<>(selectedCells);
        for(Cell c : selectedCellsCopy) {
            selectedCells.remove(c);
            c.setSelected(false);
        }
    }

    public void drawPattern(Pattern pattern, int rowStart, int colStart) {
        Cell[][] patternCells = pattern.getCells();
        for(int i = rowStart, patternI = 0; (i < rowStart + patternCells.length) && (patternI < patternCells.length); i++, patternI++) {
            for(int j = colStart, patternJ = 0; (j < colStart + patternCells[0].length) && (patternJ < patternCells[0].length); j++, patternJ++) {
                if(patternCells[patternI][patternJ].isAlive()) {
                    getCell(i, j).setAlive(true);
                }
            }
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