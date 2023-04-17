package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.components.CanvasComponent;
import com.h1r0kuu.gameoflife.components.SelectionRectangle;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.theme.Theme;
import com.h1r0kuu.gameoflife.utils.RLE;
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

    private int selectStartRow, selectStartCol, selectEndRow, selectEndCol, selectedRowsLength, selectedColsLength;

    private final GraphicsContext graphics;
    private final CanvasComponent canvas;

    private final double canvasWidth;
    private final double canvasHeight;

    public static final Color HOVER_COLOR = Color.web("#ff0000");

    private static final int[][] NEIGHBOUR_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    public Grid(int rows, int cols, GraphicsContext graphics, CanvasComponent canvasComponent) {
        this.rows = rows;
        this.cols = cols;
        this.graphics = graphics;
        this.canvasWidth = rows * Cell.CELL_SIZE;
        this.canvasHeight = cols * Cell.CELL_SIZE;
        this.canvas = canvasComponent;
        cells = new Cell[rows][cols];
    }

    public void init() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell();
            }
        }
        Pattern pattern = GameManager.patternManager.getByName("Shark loop");
//        initPattern(pattern);
        logger.info("Grid init");
    }

    public void initPattern(Pattern pattern) {
        clearGrid();
        Cell[][] patternCells = RLE.decode(pattern.getRleString());
        double centerX = cols / 2.0;
        double centerY = rows / 2.0;
        double startX = centerX - (patternCells[0].length / 2.0);
        double startY = centerY - (patternCells.length / 2.0);
        drawPattern(patternCells, (int)startX, (int)startY);

    }


    public void update() {
        Theme currentTheme = GameManager.getCurrentTheme();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = getCell(i, j);
                Color borderColor = cell.isHovered() ? HOVER_COLOR : currentTheme.GRID;
                Color cellColor = cell.getColor(currentTheme);
                drawCell(i, j, borderColor, cellColor);
            }
        }

        if(showBorders) {
            drawBorders();
        }
    }

    public void drawBorders() {
        double w = Cell.CELL_SIZE;
        double h = Cell.CELL_SIZE;

        graphics.setStroke(GameManager.themeManager.getCurrentTheme().GRID.darker());
        graphics.setLineWidth(1);

        for (int i = 0; i <= rows; i += 5) {
            double x = i * w;
            graphics.strokeLine(x, 0, x, canvasHeight);
        }

        for (int j = 0; j <= cols; j += 5) {
            double y = j * h;
            graphics.strokeLine(0, y, canvasWidth, y);
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

    public int getAliveNeighbours(int row, int column, Cell[][] cells) {
        int neighbours = 0;
        for (int[] offset : NEIGHBOUR_OFFSETS) {
            int neighbourRow = row + offset[0];
            int neighbourCol = column + offset[1];
            if (neighbourRow >= 0 && neighbourRow < rows &&
                    neighbourCol >= 0 && neighbourCol < cols &&
                    cells[neighbourRow][neighbourCol].isAlive()) {
                neighbours++;
            }
        }
        return neighbours;
    }

    public int getAliveNeighbours(int row, int column) {
        return getAliveNeighbours(row, column, cells);
    }

    public void nextGeneration() {
        Cell[][] nextGeneration = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int neighbours = getAliveNeighbours(i, j);
                Cell cell = getCell(i,j);
                boolean currentCellIsAlive = cell.isAlive();
                boolean wasAlive = cell.wasAlive();
                Cell nextGenCell = cell;

                if(currentCellIsAlive && (neighbours == 2 || neighbours == 3)) {
                    nextGenCell.setWasAlive(false);
                } else if (!currentCellIsAlive && neighbours == 3) {
                    nextGenCell = new Cell(true);
                    nextGenCell.setWasAlive(false);
                } else {
                    nextGenCell = new Cell(false);
                    if(currentCellIsAlive) nextGenCell.setWasAlive(true);
                    if(wasAlive) nextGenCell.setWasAlive(true);
                }
                nextGenCell.setLifeTime(cell.getLifetime());
                nextGenCell.setDeadTime(cell.getDeadTime());
                nextGenCell.setEvent(cell.getEvent());
                nextGenCell.update();

                if(cell.isHovered()) {
                    nextGenCell.setHovered(true);
                    hoveredCell = nextGenCell;
                }

                nextGeneration[i][j] = nextGenCell;
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
        Cell cell = getCell(row, col);

        double w = Cell.CELL_SIZE;
        double h = Cell.CELL_SIZE;

        double x = row * w;
        double y = col * h;

        double borderSize = 1.0;

        if (cell.isHovered() || showBorders) {
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


        selectStartRow = (int) Math.floor(minX / Cell.CELL_SIZE);
        selectStartCol = (int) Math.floor(minY / Cell.CELL_SIZE);

        selectEndRow = (int) Math.floor(maxX / Cell.CELL_SIZE);
        selectEndCol = (int) Math.floor(maxY / Cell.CELL_SIZE);
        selectedColsLength = (selectEndRow - selectStartRow) + 1;
        selectedRowsLength = (selectEndCol - selectStartCol) + 1;

        SelectionRectangle rec = canvas.getSelectionRectangle();
        rec.setFill(Color.rgb(0,0,255,.75));
        rec.setX(selectStartRow);
        rec.setY(selectStartCol);
        rec.setWidth(selectedColsLength);
        rec.setHeight(selectedRowsLength);
    }

    public void pasteCells(int startX, int startY) {
        int height = cellsToPaste.length;
        int width = cellsToPaste[0].length;

        int startRow = startY / Cell.CELL_SIZE;
        int startCol = startX / Cell.CELL_SIZE;
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

    public void drawPattern(Cell[][] patternCells, int rowStart, int colStart) {
        for(int i = rowStart, patternI = 0; (i < rowStart + patternCells.length) && (patternI < patternCells.length); i++, patternI++) {
            for(int j = colStart, patternJ = 0; (j < colStart + patternCells[0].length) && (patternJ < patternCells[0].length); j++, patternJ++) {
                if(patternCells[patternI][patternJ].isAlive()) {
                    getCell(j, i).setAlive(true);
                }
            }
        }
    }

    public void drawPastingRect(int startX, int startY) {
        int height = cellsToPaste.length;
        int width = cellsToPaste[0].length;
        int startRow = startX / Cell.CELL_SIZE;
        int startCol = startY / Cell.CELL_SIZE;

        SelectionRectangle rec = canvas.getSelectionRectangle();
        rec.setFill(Color.rgb(255,0,0,0.65));
        rec.setX(startRow);
        rec.setY(startCol);
        rec.setWidth(width);
        rec.setHeight(height);

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
        Cell[][] selectedCells = new Cell[selectedRowsLength][selectedColsLength];
        for (int row = selectStartRow, i = 0; (row <= selectEndRow) && (i < rows); row++, i++) {
            for (int col = selectStartCol, j = 0; (col <= selectEndCol) && (j < cols); col++, j++) {
                selectedCells[j][i] = cells[row][col];
            }
        }
        return selectedCells;
    }

    public void clearSelectedCells() {
        for (int row = selectStartRow, i = 0; (row <= selectEndRow) && (i < rows); row++, i++) {
            for (int col = selectStartCol, j = 0; (col <= selectEndCol) && (j < cols); col++, j++) {
                cells[row][col].setAlive(false);
            }
        }
    }

    public void moveSelectedCells(Move move) {
        SelectionRectangle rec = canvas.getSelectionRectangle();
        Cell[][] prevSelectedCells = getSelectedCells();
        switch (move) {
            case LEFT -> {
                selectStartRow -= 1;
                selectEndRow -= 1;
                rec.setX(selectStartRow);
            }
            case RIGHT -> {
                selectStartRow += 1;
                selectEndRow += 1;
                rec.setX(selectStartRow);
            }
            case UP -> {
                selectStartCol -= 1;
                selectEndCol -= 1;
                rec.setY(selectStartCol);
            }
            case DOWN -> {
                selectStartCol += 1;
                selectEndCol += 1;
                rec.setY(selectStartCol);
            }
        }

        for (int row = selectStartRow, i = 0; (row <= selectEndRow) && (i < rows); row++, i++) {
            for (int col = selectStartCol, j = 0; (col <= selectEndCol) && (j < cols); col++, j++) {
                cells[row][col] = prevSelectedCells[j][i];
            }
        }
    }

    public void setCellsToPaste(Cell[][] cellsToPaste) {
        this.cellsToPaste = cellsToPaste;
    }
}