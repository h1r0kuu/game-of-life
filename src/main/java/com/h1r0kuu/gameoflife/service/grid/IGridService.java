package com.h1r0kuu.gameoflife.service.grid;

import com.h1r0kuu.gameoflife.enums.MoveType;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.models.Pattern;

public interface IGridService {
    void setPatternAtPosition(Cell[][] patternCells, int rowStart, int colStart);
    void setPattern(Pattern pattern);
    void setPattern(Cell[][] patternCells);
    void clearGrid();
    void randomize(double probability);
    int getAliveNeighbours(Grid grid, int row, int col);
    int getAliveNeighbours(Grid grid, int index);
    void nextGeneration();
    void reviveCell(int row, int col);
    void killCell(int row, int col);
    Cell[][] getSelectedCells();
    void pasteCells(int startX, int startY);
    void clearSelectedCells();
    void moveSelectedCells(MoveType moveType);
    void rotateSelectedCells(MoveType moveType);
    void inverseSelectedCells();
    void cancelSelection();
    Cell[][] getContent(Grid grid);
    void printCellsAsString(Cell[][] cells);
}