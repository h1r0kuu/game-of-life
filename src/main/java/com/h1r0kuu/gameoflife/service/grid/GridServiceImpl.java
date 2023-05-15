package com.h1r0kuu.gameoflife.service.grid;

import com.h1r0kuu.gameoflife.components.SelectionRectangle;
import com.h1r0kuu.gameoflife.enums.CellEvent;
import com.h1r0kuu.gameoflife.enums.MoveType;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.models.LifeLikeRule;
import com.h1r0kuu.gameoflife.models.Pattern;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.RLE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GridServiceImpl implements IGridService {

    private final Grid grid;
    private final int rows;
    private final int cols;

    public GridServiceImpl(Grid grid) {
        this.grid = grid;
        this.rows = grid.getRows();
        this.cols = grid.getCols();
    }

    @Override
    public void setPatternAtPosition(Cell[][] patternCells, int rowStart, int colStart) {
        for(int patternI = 0; patternI < patternCells.length; patternI++) {
            for(int patternJ = 0; patternJ < patternCells[0].length; patternJ++) {
                int i = rowStart + patternI;
                int j = colStart + patternJ;
                if(patternCells[patternI][patternJ].isAlive()) {
                    grid.getCell(i, j).setAlive(true);
                    grid.increasePopulation();
                }
            }
        }
    }

    @Override
    public void setPattern(Pattern pattern) {
        Cell[][] patternCells = RLE.decode(pattern.getRleString());
        grid.setRule(new LifeLikeRule(pattern.getRule()));
        setPattern(patternCells);
    }

    @Override
    public void setPattern(Cell[][] patternCells) {
        int rowStart = (grid.getRows() / 2) - (patternCells.length / 2);
        int colStart = (grid.getCols() / 2) - (patternCells[0].length / 2);
        setPatternAtPosition(patternCells, rowStart, colStart);
    }

    @Override
    public void clearGrid() {
        grid.setPopulation(0);
        for (int i = 0; i < grid.getCells().length; i++) {
            grid.getCell(i).setAlive(false);
        }
    }

    @Override
    public void randomize(double probability) {
        clearSelectedCells();

        int startRow = grid.getRectangle().getSelectStartRow();
        int endRow = grid.getRectangle().getSelectEndRow();
        int startCol = grid.getRectangle().getSelectStartCol();
        int endCol = grid.getRectangle().getSelectEndCol();

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (Math.random() <= probability / 100) {
                    grid.getCell(row, col).setAlive(true);
                    grid.increasePopulation();
                }
            }
        }
    }

    @Override
    public int getAliveNeighbours(Grid grid, int row, int col) {
        int neighbours = 0;
        for (int[] offset : Constants.NEIGHBOUR_OFFSETS) {
            int neighbourRow = row + offset[0];
            int neighbourCol = col + offset[1];
            int neighbourIndex = neighbourRow * cols + neighbourCol;

            if (neighbourRow >= 0 && neighbourRow < rows &&
                    neighbourCol >= 0 && neighbourCol < cols &&
                    grid.getCell(neighbourIndex).isAlive()) {
                neighbours++;
            }
        }

        return neighbours;
    }

    @Override
    public int getAliveNeighbours(Grid grid, int index) {
        int row = index / cols;
        int col = index % cols;
        return getAliveNeighbours(grid, row, col);
    }

    @Override
    public void nextGeneration() {
        for (int i = 0; i < rows * cols; i++) {
            final int index = i;
            Cell cell = grid.getCell(index);
            boolean isCurrentCellAlive  = cell.isAlive();
            boolean willBeAlive = grid.getRule().apply(grid, this, index);
            cell.setNextState(willBeAlive);
            if(isCurrentCellAlive && !willBeAlive) {
                grid.decreasePopulation();
            } else if(!isCurrentCellAlive && willBeAlive) {
                grid.increasePopulation();
            }

            if (isCurrentCellAlive && willBeAlive || !isCurrentCellAlive  && willBeAlive) {
                cell.setWasAlive(false);
            } else if (isCurrentCellAlive ) {
                cell.setWasAlive(true);
            }

            cell.update();
        }

        for (int i = 0; i < rows * cols; i++) {
            Cell cell = grid.getCell(i);
            cell.applyNextState();
        }
    }

    @Override
    public void reviveCell(int row, int col) {
        Cell cell = grid.getCell(row, col);
        if(!cell.isAlive()) {
            cell.reset();
            cell.setAlive(true);
            cell.setEvent(CellEvent.REVIEW);
            grid.increasePopulation();
        }
    }

    @Override
    public void killCell(int row, int col) {
        Cell cell = grid.getCell(row, col);
        if(cell.isAlive()) {
            cell.reset();
            cell.setAlive(false);
            cell.setEvent(CellEvent.KILL);
            grid.decreasePopulation();
        }
    }

    @Override
    public Cell[][] getSelectedCells() {
        int startRow = grid.getRectangle().getSelectStartRow();
        int endRow = grid.getRectangle().getSelectEndRow();
        int startCol = grid.getRectangle().getSelectStartCol();
        int endCol = grid.getRectangle().getSelectEndCol();
        int selectedRowsLength = grid.getRectangle().getSelectedRowsLength();
        int selectedColsLength = grid.getRectangle().getSelectedColsLength();

        if(selectedRowsLength == 0 && selectedColsLength == 0)
            return new Cell[0][0];

        Cell[][] selectedCells = new Cell[selectedColsLength][selectedRowsLength];

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                selectedCells[i - startRow][j - startCol] = grid.getCell(i, j);
            }
        }

        return selectedCells;
    }

    @Override
    public void pasteCells(int startX, int startY) {
        Cell[][] cellsToPaste = grid.getCellsToPaste();
        if(cellsToPaste.length == 0)
            return;
        int cellSize = Constants.CELL_SIZE;
        int startRow = startY / cellSize;
        int startCol = startX / cellSize;
        int endRow = startRow + cellsToPaste.length;
        int endCol = startCol + cellsToPaste[0].length;

        for (int i = 0; i < grid.getCellsToPaste().length; i++) {
            for (int j = 0; j < grid.getCellsToPaste()[i].length; j++) {
                int row = startRow + i;
                int col = startCol + j;
                if (row < endRow && col < endCol) {
                    Cell cell = grid.getCell(row, col);
                    switch (GameManager.pastingType) {
                        case AND -> cell.setAlive(grid.getCellToPaste(i, j).isAlive() && cell.isAlive());
                        case CPY -> cell.setAlive(grid.getCellToPaste(i, j).isAlive());
                        case OR -> cell.setAlive(grid.getCellToPaste(i, j).isAlive() || cell.isAlive());
                        case XOR -> cell.setAlive(grid.getCellToPaste(i, j).isAlive() ^ cell.isAlive());
                    }
                }
            }
        }
    }

    @Override
    public void clearSelectedCells() {
        for (int row = grid.getRectangle().getSelectStartRow(); row <= grid.getRectangle().getSelectEndRow(); row++) {
            for (int col = grid.getRectangle().getSelectStartCol(); col <= grid.getRectangle().getSelectEndCol(); col++) {
                grid.getCell(row, col).setAlive(false);
                grid.decreasePopulation();
            }
        }
    }

    @Override
    public void moveSelectedCells(MoveType moveType) {
        SelectionRectangle rectangle = grid.getRectangle();

        int selectedRowsLength = rectangle.getSelectedRowsLength();
        int selectedColsLength = rectangle.getSelectedColsLength();

        Cell[][] selectedCells = getSelectedCells();
        Cell[][] prevSelectedCells = new Cell[selectedColsLength][selectedRowsLength];
        for(int i = 0; i < selectedColsLength; i++) {
            for(int j = 0; j < selectedRowsLength; j++) {
                prevSelectedCells[i][j] = new Cell(selectedCells[i][j].isAlive());
            }
        }
        clearSelectedCells();
        rectangle.move(moveType);
        setCellToPrevSelected(rectangle, prevSelectedCells);
    }

    @Override
    public void rotateSelectedCells(MoveType moveType) {
        SelectionRectangle rectangle = grid.getRectangle();
        Cell[][] prevSelectedCells = getSelectedCells();

        rectangle.rotate(moveType);

        setCellToPrevSelected(rectangle, prevSelectedCells);
    }

    private void setCellToPrevSelected(SelectionRectangle rectangle, Cell[][] prevSelectedCells) {
        int i =0;
        for (int row = rectangle.getSelectStartRow(); row <= rectangle.getSelectEndRow(); row++, i++) {
            int j = 0;
            for (int col = rectangle.getSelectStartCol(); col <= rectangle.getSelectEndCol(); col++, j++) {
                grid.setCell(row, col, prevSelectedCells[i][j]);
            }
        }
    }

    @Override
    public void inverseSelectedCells() {
        SelectionRectangle rectangle = grid.getRectangle();
        int i = 0;
        for (int row = rectangle.getSelectStartRow(); row <= rectangle.getSelectEndRow(); row++, i++) {
            int j = 0;
            for (int col = rectangle.getSelectStartCol(); col <= rectangle.getSelectEndCol(); col++, j++) {
                Cell cell = grid.getCell(row, col);
                cell.setAlive(!cell.isAlive());
            }
        }
    }

    @Override
    public void cancelSelection() {
        SelectionRectangle rectangle = grid.getRectangle();
        grid.setCellsToPaste(new Cell[0][0]);
        rectangle.clear();
    }

    @Override
    public Cell[][] getContent(Grid grid) {
        int minRow = grid.getRows();
        int maxRow = 0;
        int minCol = grid.getCols();
        int maxCol = 0;

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                if (grid.getCell(i, j).isAlive()) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        Cell[][] croppedGrid = new Cell[maxRow - minRow + 1][maxCol - minCol + 1];

        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                croppedGrid[i - minRow][j - minCol] = grid.getCell(i, j);
            }
        }

        return croppedGrid;
    }

    @Override
    public void printCellsAsString(Cell[][] cells) {
        for(Cell[] row : cells ) {
            for(Cell cell : row) {
                if(cell == null) {
                    System.out.print("n");
                } else {
                    System.out.print(cell.isAlive() ? "o" : ".");
                }
            }
            System.out.println();
        }
    }
}