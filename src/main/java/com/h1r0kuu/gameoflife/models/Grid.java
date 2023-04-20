package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.components.SelectionRectangle;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Grid {
    private static final Logger logger = LogManager.getLogger(Grid.class);

    private Rule rule;

    public final int rows;
    public final int cols;
    private Cell[] cells;
    private Cell[][] selectedCells;
    private Cell[][] cellsToPaste;
    private boolean showBorders = false;
    private final SelectionRectangle rectangle;

    public Grid(int rows, int cols, SelectionRectangle rectangle) {
        this.rows = rows;
        this.cols = cols;
        this.rule = new LifeLikeRule("B3/S23");
        this.rectangle = rectangle;
        cells = new Cell[rows * cols];
    }

    public void init() {
        for (int i = 0; i < cells.length; i++) {
            setCell(i, new Cell(false));
        }
    }

    public Cell getCell(int index) {
        return cells[index];
    }
    public Cell getCell(int row, int column) {
        return cells[row * cols + column];
    }
    public void setCell(int index, Cell cell) {
        cells[index] = cell;
    }

    public void setCell(int row, int column, Cell cell) {
        cells[row * cols + column] = cell;
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }

    public void drawPastingRect(int startX, int startY) {
        int height = cellsToPaste.length;
        int width = cellsToPaste[0].length;
        int startRow = startX / Constants.CELL_SIZE;
        int startCol = startY / Constants.CELL_SIZE;

        rectangle.setX(startRow);
        rectangle.setY(startCol);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    public void setCellsToPaste(Cell[][] cellsToPaste) {
        this.cellsToPaste = cellsToPaste;
    }

    public SelectionRectangle getRectangle() {
        return rectangle;
    }

    public Cell[][] getCellsToPaste() {
        return cellsToPaste;
    }

    public Cell getCellToPaste(int row, int col) {
        return cellsToPaste[row][col];
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}