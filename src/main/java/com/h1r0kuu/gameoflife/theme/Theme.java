package com.h1r0kuu.gameoflife.theme;

import javafx.scene.paint.Color;


public class Theme {
    public String THEME_NAME;
    public Color BACKGROUND;
    public Color GRID;
    public Color CELL_ALIVE;
    public CellShade cellAliveShade;
    public CellShadeDirection cellAliveShadeDir;
    public Color CELL_ALIVERAMP;
    public CellShade cellDeadShade;
    public CellShadeDirection cellDeadShadeDir;
    public Color CELL_DEAD;
    public Color CELL_DEADRAMP;

    public Theme(String themeName,
                 Color background,
                 Color grid,
                 Color cellAlive,
                 CellShade aliveShade,
                 CellShadeDirection aliveShadeDir,
                 Color cellAliveRamp,
                 Color cellDead,
                 CellShade deadShade,
                 CellShadeDirection deadShadeDir,
                 Color cellDeadRamp) {
        this.THEME_NAME = themeName;
        this.BACKGROUND = background;
        this.GRID = grid;

        this.CELL_ALIVE = cellAlive;
        this.cellAliveShade = aliveShade;
        this.cellAliveShadeDir = aliveShadeDir;
        this.CELL_ALIVERAMP = cellAliveRamp;

        this.CELL_DEAD = cellDead;
        this.cellDeadShade = deadShade;
        this.cellDeadShadeDir = deadShadeDir;
        this.CELL_DEADRAMP = cellDeadRamp;
    }

    public String getTHEME_NAME() {
        return THEME_NAME;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Theme theme = (Theme) o;

        return THEME_NAME.equals(theme.THEME_NAME);
    }

    @Override
    public int hashCode() {
        return THEME_NAME.hashCode();
    }
}
