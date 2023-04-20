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

    private double mouseX;
    private double mouseY;
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private double selectX;
    private double selectY;
    public CanvasMouseHandlers(GameManager gameManager, IGridService gridService, Grid grid, Label cellInfo) {
        this.gameManager = gameManager;
        this.gridService = gridService;
        this.grid = grid;
        this.cellInfo = cellInfo;
    }

    public void onMouseMove(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        startX = e.getX();
        startY = e.getY();
        if (mouseX < 0 || mouseY < 0 ||
                mouseX >= grid.getRows() * Constants.CELL_SIZE || mouseY >= grid.getCols() * Constants.CELL_SIZE) {
            return;
        }
        int row = (int) (mouseY / Constants.CELL_SIZE);
        int col = (int) (mouseX / Constants.CELL_SIZE);
        Cell cell = grid.getCell(row, col);
        cellInfo.setText(LabelUtility.getText(LabelUtility.CELL_INFO, row, col, cell.isAlive() ? "alive" : "dead"));

        if(GameManager.userActionState.equals(UserActionState.PASTING)) {
            Cell[][] cellsToPaste = grid.getCellsToPaste();
            grid.getRectangle().drawPasteRectangle(cellsToPaste, (int)mouseX, (int)mouseY);
        }
    }

    public void onMousePressed(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();

        UserActionState userActionState = GameManager.userActionState;

        if(userActionState.equals(UserActionState.DRAWING)) {
            if(gameManager.isPauseWhenDraw()) gameManager.setPaused(true);
            int row = (int) (startY / Constants.CELL_SIZE);
            int col = (int) (startX / Constants.CELL_SIZE);

            if(e.isPrimaryButtonDown()) {
                gridService.reviveCell(row, col);
            } else {
                gridService.killCell(row, col);
            }
        } else if(userActionState.equals(UserActionState.SELECTING)) {
            selectX = e.getX();
            selectY = e.getY();
        } else if(userActionState.equals(UserActionState.PASTING)) {
            gridService.pasteCells((int)mouseX, (int)mouseY);
            GameManager.userActionState = UserActionState.SELECTING;
            grid.getRectangle().getRectangleForPaste().setHeight(0);
            grid.getRectangle().getRectangleForPaste().setWidth(0);
        }
    }

    public void onMouseDragged(MouseEvent e) {
        if (mouseX < 0 || mouseY < 0 ||
            mouseX >= grid.getRows() * Constants.CELL_SIZE || mouseY >= grid.getCols() * Constants.CELL_SIZE) {
            return;
        }
        int cellStartRow = (int) Math.floor(startY / Constants.CELL_SIZE);
        int cellStartCol = (int) Math.floor(startX / Constants.CELL_SIZE);
        int cellEndRow = (int) Math.floor((int)e.getY() / Constants.CELL_SIZE);
        int cellEndCol = (int) Math.floor((int)e.getX() / Constants.CELL_SIZE);

        if (GameManager.userActionState.equals(UserActionState.DRAWING)) {
            if (cellStartRow >= 0 && cellStartRow < grid.getCols() && cellStartCol >= 0 && cellStartCol < grid.getRows()) {
                // Interpolate cells using Bresenham's line algorithm
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
        } else if (GameManager.userActionState.equals(UserActionState.SELECTING)) {
            grid.getRectangle().selectRange(selectX, selectY, e.getX(), e.getY());
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
