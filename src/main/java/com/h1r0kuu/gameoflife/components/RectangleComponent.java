package com.h1r0kuu.gameoflife.components;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class RectangleComponent extends Rectangle {


    public RectangleComponent(int width, int height) {
        super(width, height);
        this.setFill(ButtonComponent.IDLE_BUTTON_COLOR);
        this.setStrokeType(StrokeType.INSIDE);
        this.setStroke(Color.WHITE);
        this.setStrokeWidth(1);
    }
}
