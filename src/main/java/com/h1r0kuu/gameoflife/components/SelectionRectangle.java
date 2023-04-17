package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.entity.Cell;
import javafx.scene.shape.Rectangle;

public class SelectionRectangle extends Rectangle {

    public void setX(int row) {
        super.setTranslateX(row * Cell.CELL_SIZE);
    }

    public void setY(int col) {
        super.setTranslateY(col * Cell.CELL_SIZE);
    }

    public void setWidth(int colsLength) {
        super.setWidth(colsLength * Cell.CELL_SIZE);
    }

    public void setHeight(int rowsLength) {
        super.setHeight(rowsLength * Cell.CELL_SIZE);
    }
}
