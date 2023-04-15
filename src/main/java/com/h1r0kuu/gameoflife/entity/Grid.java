package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.manages.PatternManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Grid {
    private static final Logger logger = LogManager.getLogger(Grid.class);

    public final int rows;
    public final int cols;
    private Cell[][] cells;
    private Cell[][] selectedCells;
    public Cell[][] cellsToPaste;
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

                if(isSelected(cell)) {
                    nextGeneration[i][j].setSelected(true);
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
            graphics.setFill(Color.rgb(0, 0, 255, 0.7));
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
        double minX = Math.min(startX, endX);
        double minY = Math.min(startY, endY);

        double maxX = Math.max(startX, endX);
        double maxY = Math.max(startY, endY);


        int startRow = (int) Math.floor(minX / Cell.CELL_SIZE);
        int startCol = (int) Math.floor(minY / Cell.CELL_SIZE);

        int endRow = (int) Math.floor(maxX / Cell.CELL_SIZE);
        int endCol = (int) Math.floor(maxY / Cell.CELL_SIZE);
        int rows = (endRow - startRow) + 1;
        int cols = (endCol - startCol) + 1;

        selectedCells = new Cell[cols][rows];

        for (int row = startRow, i = 0; (row <= endRow) && (i < rows); row++, i++) {
            for (int col = startCol, j = 0; (col <= endCol) && (j < cols); col++, j++) {
                Cell cell = getCell(row, col);
                cell.setSelected(true);
                if (!isSelected(cell)) {
                    selectedCells[j][i] = cell;
                }
            }
        }
    }

    public void pasteCells(int startX, int startY) {
        int height = cellsToPaste.length;
        int width = cellsToPaste[0].length;

        int startRow = (int) Math.floor(startY / Cell.CELL_SIZE);
        int startCol = (int) Math.floor(startX / Cell.CELL_SIZE);
        int endRow = startRow + height;
        int endCol = startCol + width;

        for (int row = startRow, i = 0; (row <= endRow) && (i < height); row++, i++) {
            for (int col = startCol, j = 0; (col <= endCol) && (j < width); col++, j++) {
                Cell cell = getCell(col, row);
                cell.setAlive(cellsToPaste[i][j].isAlive());
                cell.setLifeTime(0);
                cell.setDeadTime(0);
            }
        }
    }

    public boolean isSelected(Cell cell) {
        boolean result = false;
        if(selectedCells != null && selectedCells.length > 0) {
            for(int i = 0; i < selectedCells.length; i++) {
                for (int j = 0; j < selectedCells[0].length; j++) {
                    if(selectedCells[i][j] != null && selectedCells[i][j].equals(cell)) result = true;
                }
            }
        }
        return result;
    }

    public void unselectCells() {
        if(selectedCells != null && selectedCells.length > 0) {
            for(int i = 0; i < selectedCells.length; i++) {
                for(int j = 0; j < selectedCells[0].length; j++) {
                    selectedCells[i][j].setSelected(false);
                }
            }
            selectedCells = null;
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

    public Cell[][] getSelectedCells() {
        return selectedCells;
    }
}