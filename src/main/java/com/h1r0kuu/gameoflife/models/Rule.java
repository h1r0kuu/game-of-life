package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.service.grid.IGridService;

public interface Rule {
    boolean apply(Grid grid, IGridService IGridService, int row, int col);
    boolean apply(Grid grid, IGridService IGridService, int index);
}
