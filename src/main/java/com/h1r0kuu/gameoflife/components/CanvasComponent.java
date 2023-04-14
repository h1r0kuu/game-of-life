package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.handlers.CanvasMouseHandlers;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CanvasComponent extends Canvas {
    private static final Logger logger = LogManager.getLogger(CanvasComponent.class);

    public Grid grid;

    public CanvasComponent(int width, int height) {
        super(width, height);
        init();
        logger.info("CanvasComponent init");
    }

    private void init() {
        int rows = (int) Math.floor(getHeight() / Cell.CELL_SIZE);
        int cols = (int) Math.floor(getWidth() / Cell.CELL_SIZE);
        GraphicsContext graphics = this.getGraphicsContext2D();
        this.grid = new Grid(rows, cols, graphics);
        grid.init();
    }

    public void setHandler(CanvasMouseHandlers handler) {
        this.setOnMouseMoved(handler::onMouseMove);
        this.setOnMousePressed(handler::onMousePressed);
        this.setOnMouseDragged(handler::onMouseDragged);
        this.setOnMouseReleased(handler::onMouseReleased);
    }
}
