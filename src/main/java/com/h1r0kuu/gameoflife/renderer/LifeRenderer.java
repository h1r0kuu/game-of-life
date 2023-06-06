package com.h1r0kuu.gameoflife.renderer;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.models.Theme;
import com.h1r0kuu.gameoflife.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LifeRenderer {
    private final Grid grid;
    private final GraphicsContext gc;
    private final int rows;
    private final int cols;

    public LifeRenderer(Grid grid, GraphicsContext graphicsContext) {
        this.grid = grid;
        this.rows = grid.getRows();
        this.cols = grid.getCols();
        this.gc = graphicsContext;
    }

    public void redraw() {
        Theme currentTheme = GameManager.getCurrentTheme();

        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                Cell cell = grid.getCell(i, j);
                Color cellColor = cell.getColor(currentTheme);
                if(!cellColor.equals(cell.getPreviousColor())) {
                    cell.setPreviousColor(cellColor);
                    drawCell(j, i, cellColor);
                }
            }
        }
    }

    public void drawCell(int row, int col, Color cellColor) {
        double size = Constants.CELL_SIZE;

        double x = row * size;
        double y = col * size;

        gc.setFill(GameManager.getCurrentTheme().BACKGROUND);
        gc.fillRect(x, y, size, size);
        gc.setFill(cellColor);
        gc.fillRect(x, y, size, size);
    }
}
