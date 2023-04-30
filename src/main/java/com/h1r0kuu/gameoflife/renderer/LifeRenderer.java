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
                Color borderColor = currentTheme.GRID;
                Color cellColor = cell.getColor(currentTheme);
                if(cellColor != cell.getPreviousColor()) {
                    cell.setPreviousColor(cellColor);
                    drawCell(j, i, borderColor, cellColor);
                }
            }
        }

        if(grid.isShowBorders()) {
            drawBorders();
        }
    }

    public void drawBorders() {
        double w = Constants.CELL_SIZE;
        double h = Constants.CELL_SIZE;

        gc.setStroke(GameManager.themeManager.getCurrentTheme().GRID.darker());
        gc.setLineWidth(1);

        for (int i = 0; i <= rows; i += 5) {
            double x = i * w;
            gc.strokeLine(x, 0, x, rows * Constants.CELL_SIZE);
        }

        for (int j = 0; j <= cols; j += 5) {
            double y = j * h;
            gc.strokeLine(0, y, cols * Constants.CELL_SIZE, y);
        }
    }

    public void drawCell(int row, int col, Color borderColor, Color cellColor) {
        double size = Constants.CELL_SIZE;

        double x = row * size;
        double y = col * size;

        double borderSize = 1.0;

        if (grid.isShowBorders()) {
            gc.setFill(borderColor);
            gc.fillRect(x, y, size, size);
            gc.setFill(cellColor);
            gc.fillRect(x + (borderSize / 2), y + (borderSize / 2), size - borderSize, size - borderSize);
        } else {
            gc.setFill(GameManager.getCurrentTheme().BACKGROUND);
            gc.fillRect(x, y, size, size);
            gc.setFill(cellColor);
            gc.fillRect(x, y, size, size);
        }
    }
}
