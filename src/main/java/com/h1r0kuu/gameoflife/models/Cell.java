package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.enums.CellEvent;
import com.h1r0kuu.gameoflife.enums.CellShade;
import com.h1r0kuu.gameoflife.enums.CellShadeDirection;
import com.h1r0kuu.gameoflife.utils.Constants;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Cell {
    private boolean isAlive = false;
    private boolean wasAlive = false;
    private CellEvent event;
    private int lifetime = 0;
    private int deadTime = 0;

    private Color previousColor;

    public Cell() {}

    public Cell(boolean isAlive) {
        if(isAlive) setWasAlive(true);
        this.isAlive = isAlive;
    }

    public void update() {
        if (isAlive()) {
            lifetime++;
            deadTime = 0;
        } else {
            lifetime=0;
            deadTime++;
        }

    }

    public Color getColor(Theme currentTheme) {
        if (lifetime == 0 && !wasAlive && !isAlive) {
            return currentTheme.BACKGROUND;
        } else if ((wasAlive && !isAlive) || (Objects.equals(event, CellEvent.KILL))) {
            return getColorForState(currentTheme.CELL_DEAD, currentTheme.CELL_DEADRAMP,
                    currentTheme.cellDeadShade, currentTheme.cellDeadShadeDir, deadTime);
        } else if ((!wasAlive & isAlive) || Objects.equals(event, CellEvent.REVIEW)) {
            return getColorForState(currentTheme.CELL_ALIVE, currentTheme.CELL_ALIVERAMP,
                    currentTheme.cellAliveShade, currentTheme.cellAliveShadeDir, lifetime);
        } else if(lifetime == 0) {
            return getColorForState(currentTheme.CELL_ALIVE, currentTheme.CELL_ALIVERAMP,
                    currentTheme.cellAliveShade, currentTheme.cellAliveShadeDir, lifetime);
        }
        return currentTheme.BACKGROUND;
    }

    private Color getColorForState(Color baseColor, Color rampColor, CellShade shade,
                                   CellShadeDirection shadeDir, int time) {
        int red = (int) (baseColor.getRed() * 255);
        int green = (int) (baseColor.getGreen() * 255);
        int blue = (int) (baseColor.getBlue() * 255);
        int rampRed = (int) (rampColor.getRed() * 255);
        int rampGreen = (int) (rampColor.getGreen() * 255);
        int rampBlue = (int) (rampColor.getBlue() * 255);

        int value, ramp;
        switch (shade) {
            case R -> {
                value = red;
                ramp = rampRed;
            }
            case G -> {
                value = green;
                ramp = rampGreen;
            }
            case B -> {
                value = blue;
                ramp = rampBlue;
            }
            default -> {
                int redMax = 160 - (time + Constants.CELL_SHADE_SPEED);
                redMax = Math.max(redMax, 60);
                return Color.rgb(redMax, 0, 0);
            }
        }

        if (shadeDir == CellShadeDirection.MAX) {
            value -= (time + Constants.CELL_SHADE_SPEED);
            value = Math.max(value, ramp);
        } else {
            value += (time + Constants.CELL_SHADE_SPEED);
            value = Math.min(value, ramp);
        }

        if (shade == CellShade.R) {
            return Color.rgb(value, green, blue);
        } else if (shade == CellShade.G) {
            return Color.rgb(red, value, blue);
        } else {
            return Color.rgb(red, green, value);
        }
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(int deadTime) {
        this.deadTime = deadTime;
    }

    public void setLifeTime(int lifetime) {
        this.lifetime = lifetime;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public CellEvent getEvent() {return event;}

    public void setEvent(CellEvent event) {
        this.event = event;
    }

    public void setAlive(boolean alive) {
        if(alive) setWasAlive(true);
        this.isAlive = alive;
    }

    public void setWasAlive(boolean wasAlive) {
        this.wasAlive = wasAlive;
    }

    public boolean wasAlive() {
        return wasAlive;
    }

    public void reset() {
        setLifeTime(0);
        setDeadTime(0);
        setWasAlive(false);
        setAlive(false);
    }

    public Color getPreviousColor() {
        return previousColor;
    }

    public void setPreviousColor(Color previousColor) {
        this.previousColor = previousColor;
    }
}