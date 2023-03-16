package com.gameoflife.entity;

public class Cell {
    private boolean isAlive = false;

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }
}
