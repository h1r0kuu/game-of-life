package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.components.CanvasComponent;
import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.manages.UIManager;
import javafx.scene.input.MouseEvent;
public class CanvasMouseHandlers {

    private final GameManager gameManager;
    private final UIManager uiManager;
    private final CanvasComponent canvas;
    private double mouseX, mouseY, startX, startY, endX, endY, selectX, selectY;

    public CanvasMouseHandlers(CanvasComponent canvasComponent, GameManager gameManager, UIManager uiManager) {
        this.canvas = canvasComponent;
        this.gameManager = gameManager;
        this.uiManager = uiManager;
    }

    public void onMouseMove(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        startX = e.getX();
        startY = e.getY();

        int row = (int) (mouseY / Cell.CELL_SIZE);
        int col = (int) (mouseX / Cell.CELL_SIZE);
        canvas.grid.hoverGrid(col, row);
        Cell cell = canvas.grid.getCell(col, row);
        uiManager.setCellInfo(col, row, cell.isAlive());
        if(GameManager.userActionState.equals(UserActionState.PASTING)) {

        }
    }

    public void onMousePressed(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();
        if(GameManager.userActionState.equals(UserActionState.DRAWING)) {
            if(gameManager.isPauseWhenDraw()) uiManager.setGamePause(true);
            int row = (int) (startY / Cell.CELL_SIZE);
            int col = (int) (startX / Cell.CELL_SIZE);

            if(e.isPrimaryButtonDown()) {
                canvas.grid.reviveCell(col, row);
            } else {
                canvas.grid.killCell(col, row);
            }
        } else if(GameManager.userActionState.equals(UserActionState.SELECTING)) {
            selectX = e.getX();
            selectY = e.getY();
            canvas.grid.unselectCells();
        } else if(GameManager.userActionState.equals(UserActionState.PASTING)) {
            canvas.grid.pasteCells((int)e.getX(), (int)e.getY());
            gameManager.changeState(UserActionState.SELECTING);
        }
    }

    public void onMouseDragged(MouseEvent e) {
        if (e.getX() >= 0 && e.getY() >= 0 && e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
            int cellStartRow = (int) Math.floor(this.startY / Cell.CELL_SIZE);
            int cellStartCol = (int) Math.floor(this.startX / Cell.CELL_SIZE);
            int cellEndRow = (int) Math.floor(e.getY() / Cell.CELL_SIZE);
            int cellEndCol = (int) Math.floor(e.getX() / Cell.CELL_SIZE);
            canvas.grid.hoverGrid(cellEndCol, cellEndRow);

            // Interpolate cells using Bresenham's line algorithm
            int dx = Math.abs(cellEndRow - cellStartRow);
            int dy = Math.abs(cellEndCol - cellStartCol);
            int sx = cellStartRow < cellEndRow ? 1 : -1;
            int sy = cellStartCol < cellEndCol ? 1 : -1;
            int err = dx - dy;

            if (GameManager.userActionState.equals(UserActionState.DRAWING)) {
                if (cellStartRow >= 0 && cellStartRow < canvas.grid.getRows() && cellStartCol >= 0 && cellStartCol < canvas.grid.getCols()) {
                    while (true) {

                        if (e.isPrimaryButtonDown()) {
                            canvas.grid.reviveCell(cellStartCol, cellStartRow);
                        } else {
                            canvas.grid.killCell(cellStartCol, cellStartRow);
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
                canvas.grid.selectRange(selectX, selectY, e.getX(), e.getY());
            }

            this.startX = (int) e.getX();
            this.startY = (int) e.getY();
        }
    }

    public void onMouseReleased(MouseEvent e) {
        if(GameManager.userActionState.equals(UserActionState.DRAWING)) {
            if(!gameManager.isPausedByButton()) uiManager.setGamePause(false);
        }
    }
}
