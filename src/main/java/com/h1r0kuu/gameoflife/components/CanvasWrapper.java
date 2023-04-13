package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.handlers.CanvasWrapperMouseHandlers;
import javafx.scene.layout.VBox;

public class CanvasWrapper extends VBox {
    public CanvasComponent canvas;

    public CanvasWrapper(CanvasComponent canvas) {
        super();
        this.canvas = canvas;
        getChildren().add(canvas);
        CanvasWrapperMouseHandlers canvasWrapperMouseHandlers = new CanvasWrapperMouseHandlers(this);
        this.setOnMouseMoved(canvasWrapperMouseHandlers::onMouseMoved);
        this.setOnMousePressed(canvasWrapperMouseHandlers::onMousePressed);
        this.setOnMouseDragged(canvasWrapperMouseHandlers::onMouseDragged);
        this.setOnScroll(canvasWrapperMouseHandlers::onScroll);
    }
}
