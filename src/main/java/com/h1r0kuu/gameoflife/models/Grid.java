package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.components.SelectionRectangle;

public class Grid {
    private Rule rule;

    public final int rows;
    public final int cols;
    private Cell[] cells;
    private Cell[][] cellsToPaste;
    private final SelectionRectangle rectangle;
    private int population = 0;

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

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}