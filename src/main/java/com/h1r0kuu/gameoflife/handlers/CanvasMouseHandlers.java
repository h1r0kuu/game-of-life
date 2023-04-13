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

        int gridx = (int) (mouseX / (Cell.CELL_SIZE));
        int gridy = (int) (mouseY / (Cell.CELL_SIZE));
        canvas.grid.hoverGrid(gridx, gridy);
    }

    public void onMousePressed(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();
        selectX = e.getX();
        selectY = e.getY();
        if(GameManager.userActionState.equals(UserActionState.DRAWING)) {
            uiManager.setGamePause(true);

            int gridx = (int) (startX / (Cell.CELL_SIZE * 1.0));
            int gridy = (int) (startY / (Cell.CELL_SIZE * 1.0));

            if(e.isPrimaryButtonDown()) {
                canvas.grid.reviveCell(gridx, gridy);
            } else {
                canvas.grid.killCell(gridx, gridy);
            }
        }
    }

    public void onMouseDragged(MouseEvent e) {
        if (e.getX() >= 0 && e.getY() >= 0 && e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
            int cellStartX = (int) Math.floor(this.startX / Cell.CELL_SIZE);
            int cellStartY = (int) Math.floor(this.startY / Cell.CELL_SIZE);
            int cellEndX = (int) Math.floor(e.getX() / Cell.CELL_SIZE);
            int cellEndY = (int) Math.floor(e.getY() / Cell.CELL_SIZE);
            canvas.grid.hoverGrid(cellEndX, cellEndY);

            // Interpolate cells using Bresenham's line algorithm
            int dx = Math.abs(cellEndX - cellStartX);
            int dy = Math.abs(cellEndY - cellStartY);
            int sx = cellStartX < cellEndX ? 1 : -1;
            int sy = cellStartY < cellEndY ? 1 : -1;
            int err = dx - dy;

            if (GameManager.userActionState.equals(UserActionState.DRAWING)) {
                if (cellStartX >= 0 && cellStartX < canvas.grid.getRows() && cellStartY >= 0 && cellStartY < canvas.grid.getCols()) {
                    while (true) {

                        if (e.isPrimaryButtonDown()) {
                            canvas.grid.reviveCell(cellStartX, cellStartY);
                        } else {
                            canvas.grid.killCell(cellStartX, cellStartY);
                        }
                        if (cellStartX == cellEndX && cellStartY == cellEndY) {
                            break;
                        }
                        int e2 = 2 * err;
                        if (e2 > -dy) {
                            err -= dy;
                            cellStartX += sx;
                        }
                        if (e2 < dx) {
                            err += dx;
                            cellStartY += sy;
                        }
                    }
                }
            } else if (GameManager.userActionState.equals(UserActionState.SELECTING)) {
//                grid.selectRange(selectX, selectY, e.getX(), e.getY());
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
