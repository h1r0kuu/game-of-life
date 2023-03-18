package com.gameoflife.entity;

public class Cell {
    private boolean isAlive = false;
    private boolean isHovered = false;

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
