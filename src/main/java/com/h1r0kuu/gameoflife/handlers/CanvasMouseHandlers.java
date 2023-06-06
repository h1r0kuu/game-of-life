package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.enums.UserActionState;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.service.grid.IGridService;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class CanvasMouseHandlers {

    private final GameManager gameManager;
    private final Grid grid;
    private final IGridService gridService;
    private final Label cellInfo;
    private final int rows;
    private final int cols;

    private double mouseX;
    private double mouseY;
    private double startX;
    private double startY;
    private double selectX;
    private double selectY;
    private int prevRow, prevCol;

    public CanvasMouseHandlers(GameManager gameManager, IGridService gridService, Grid grid, Label cellInfo) {
        this.gameManager = gameManager;
        this.gridService = gridService;
        this.grid = grid;
        this.rows = grid.getRows();
        this.cols = grid.getCols();
        this.cellInfo = cellInfo;
    }

    public void onMouseMove(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        startX = e.getX();
        startY = e.getY();

        if (mouseX < 0 || mouseY < 0 || mouseX >= rows * Constants.CELL_SIZE || mouseY >= cols * Constants.CELL_SIZE) {
            return;
        }

        int row = (int) (mouseY / Constants.CELL_SIZE);
        int col = (int) (mouseX / Constants.CELL_SIZE);

        if (GameManager.userActionState.equals(UserActionState.PASTING)) {
            Cell[][] cellsToPaste = grid.getCellsToPaste();
            if((mouseX + (cellsToPaste[0].length * Constants.CELL_SIZE) <= rows * Constants.CELL_SIZE) &&
               (mouseY + (cellsToPaste.length * Constants.CELL_SIZE) <= cols * Constants.CELL_SIZE))
                grid.getRectangle().drawPasteRectangle(cellsToPaste, (int)mouseX, (int)mouseY);
        } else if (row != prevRow || col != prevCol) {
            prevRow = row;
            prevCol = col;
            Cell cell = grid.getCell(row, col);
            cellInfo.setText(LabelUtility.getText(LabelUtility.CELL_INFO, row, col, cell.isAlive() ? "alive" : "dead"));
        }
    }

    public void onMousePressed(MouseEvent e) {
        int x = (int)e.getX();
        int y = (int)e.getY();

        if (x < 0 || y < 0 || x >= rows * Constants.CELL_SIZE || y >= cols * Constants.CELL_SIZE) {
            return;
        }
        switch (GameManager.userActionState) {
            case DRAWING -> {
                if (gameManager.isPauseWhenDraw()) {
                    gameManager.setPaused(true);
                }
                int row = (int) (startY / Constants.CELL_SIZE);
                int col = (int) (startX / Constants.CELL_SIZE);
                if (e.isPrimaryButtonDown()) {
                    gridService.reviveCell(row, col);
                } else {
                    gridService.killCell(row, col);
                }
            }
            case SELECTING -> {
                grid.getRectangle().clear();
                selectX = x;
                selectY = y;
            }
            case PASTING -> {
                gridService.pasteCells((int) mouseX, (int) mouseY);
                if(GameManager.userPreviousState != UserActionState.PASTING)
                    GameManager.userActionState = GameManager.userPreviousState;
                else
                    GameManager.userActionState = UserActionState.SELECTING;
                grid.getRectangle().getRectangleForPaste().setHeight(0);
                grid.getRectangle().getRectangleForPaste().setWidth(0);
            }
        }
    }

    public void onMouseDragged(MouseEvent e) {
        if (mouseX < 0 || mouseY < 0 || mouseY >= cols * Constants.CELL_SIZE || mouseX >= rows * Constants.CELL_SIZE) {
            return;
        }
        mouseX = e.getX();
        mouseY = e.getY();

        int cellStartRow = (int) Math.floor(startY / Constants.CELL_SIZE);
        int cellStartCol = (int) Math.floor(startX / Constants.CELL_SIZE);
        int cellEndRow = (int) Math.floor(mouseY / Constants.CELL_SIZE);
        int cellEndCol = (int) Math.floor(mouseX / Constants.CELL_SIZE);

        switch (GameManager.userActionState) {
            case DRAWING -> {
                    int dx = Math.abs(cellEndRow - cellStartRow);
                    int dy = Math.abs(cellEndCol - cellStartCol);
                    int sx = cellStartRow < cellEndRow ? 1 : -1;
                    int sy = cellStartCol < cellEndCol ? 1 : -1;
                    int err = dx - dy;

                    while (true) {
                        if (e.isPrimaryButtonDown()) {
                            gridService.reviveCell(cellStartRow, cellStartCol);
                        } else {
                            gridService.killCell(cellStartRow, cellStartCol);
                        }
                        if (cellStartRow == cellEndRow && cellStartCol == cellEndCol) {
                            break;
                        }
                        int e2 = 2 * err;
                        if (e2 > -dy) {
                            err -= dy;
                            cellStartRow += sx;
                        }
                        if (e2 < dx) {
                            err += dx;
                            cellStartCol += sy;
                        }
                    }

            }
            case SELECTING -> {
                if (mouseX > 0 && mouseY > 0 && mouseY <= cols * Constants.CELL_SIZE && mouseX <= rows * Constants.CELL_SIZE) {
                    grid.getRectangle().selectRange(selectX, selectY, mouseX, mouseY);
                }
            }
        }

        startX = (int) e.getX();
        startY = (int) e.getY();
    }

    public void onMouseReleased(MouseEvent e) {
        if(GameManager.userActionState.equals(UserActionState.DRAWING)) {
            if(!gameManager.isPausedByButton()) gameManager.setPaused(false);
        }
    }
}