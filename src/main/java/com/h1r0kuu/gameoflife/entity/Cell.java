package com.h1r0kuu.gameoflife.entity;

import javafx.scene.paint.Color;

public class Cell {
    private boolean isAlive = false;
    private boolean isHovered = false;
    public static final int CELL_SIZE = 10;
    public static final Color ALIVE_CELL_COLOR = Color.web("#00522D");
    public static final Color DEAD_CELL_COLOR = Color.web("#151310");

    public Cell() {}

    public Cell(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
    }


}