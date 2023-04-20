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
        for (int i = 0; i < grid.getCells().length; i++) {
            grid.setCell(i, new Cell(false));
        }
    }

    @Override
    public void randomize(double probability) {
        for (int i = 0; i < grid.getCells().length; i++) {
            if (Math.random() <= probability / 100) {
                grid.getCell(i).setAlive(true);
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
        int cols = grid.getCols();

        int row = index / cols;
        int col = index % cols;

        return getAliveNeighbours(grid, row, col);
    }

    @Override
    public void nextGeneration() {
        Cell[] nextGeneration = new Cell[rows * cols];

        for (int i = 0; i < rows * cols; i++) {
            Cell cell = grid.getCell(i);
            boolean currentCellIsAlive = cell.isAlive();
            boolean wasAlive = cell.wasAlive();
            Cell nextGenCell = cell;

            boolean willBeAlive = grid.getRule().apply(grid, this, i);

            if(currentCellIsAlive && willBeAlive) {
                nextGenCell.setWasAlive(false);
            } else if(!currentCellIsAlive && willBeAlive) {
                nextGenCell = new Cell(true);
                nextGenCell.setWasAlive(false);
            } else {
                nextGenCell = new Cell(false);
                if (currentCellIsAlive) nextGenCell.setWasAlive(true);
                if (wasAlive) nextGenCell.setWasAlive(true);
            }
            nextGenCell.setLifeTime(cell.getLifetime());
            nextGenCell.setDeadTime(cell.getDeadTime());
            nextGenCell.setEvent(cell.getEvent());
            nextGenCell.update();

            nextGeneration[i] = nextGenCell;
        }

        grid.setCells(nextGeneration);
    }

    @Override
    public void reviveCell(int row, int col) {
        Cell cell = grid.getCell(row, col);
        if(!cell.isAlive()) {
            cell.reset();
            cell.setAlive(true);
            cell.setEvent(CellEvent.REVIEW);
        }
    }

    @Override
    public void killCell(int row, int col) {
        Cell cell = grid.getCell(row, col);
        if(cell.isAlive()) {
            cell.reset();
            cell.setAlive(false);
            cell.setEvent(CellEvent.KILL);
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

        Cell[][] selectedCells = new Cell[selectedColsLength][selectedRowsLength];

        int i = 0;
        for (int row = startRow; row <= endRow; row++, i++) {
            int j = 0;
            for (int col = startCol; col <= endCol; col++, j++) {
                selectedCells[i][j] = grid.getCell(row, col);
            }
        }

        return selectedCells;
    }

    @Override
    public void pasteCells(int startX, int startY) {
        int cellSize = Constants.CELL_SIZE;
        int startRow = startY / cellSize;
        int startCol = startX / cellSize;
        int endRow = startRow + grid.getCellsToPaste().length;
        int endCol = startCol + grid.getCellsToPaste()[0].length;

        for (int i = 0; i < grid.getCellsToPaste().length; i++) {
            for (int j = 0; j < grid.getCellsToPaste()[i].length; j++) {
                int row = startRow + i;
                int col = startCol + j;
                if (row < endRow && col < endCol) {
                    Cell cell = grid.getCell(row, col);
                    switch (GameManager.pastingType) {
                        case AND -> {
                            cell.setAlive(grid.getCellToPaste(i, j).isAlive() && cell.isAlive());
                        }
                        case CPY -> {
                            cell.setAlive(grid.getCellToPaste(i, j).isAlive());
                        }
                        case OR -> {
                            cell.setAlive(grid.getCellToPaste(i, j).isAlive() || cell.isAlive());
                        }
                        case XOR -> {
                            cell.setAlive(grid.getCellToPaste(i, j).isAlive() ^ cell.isAlive());
                        }
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
