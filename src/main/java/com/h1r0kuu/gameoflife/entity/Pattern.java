package com.h1r0kuu.gameoflife.entity;

import com.h1r0kuu.gameoflife.utils.RLE;

public class Pattern {
    private int id;
    private String name;
    private Cell[][] cells;
    private String rleString;

    public Pattern(Cell[][] cells) {
        this.cells = cells;
    }

    public Pattern(String name, Cell[][] cells) {
        this(cells);
        this.name = name;
        this.rleString = RLE.encode(cells);
    }

    public Pattern(String name, String rleString) {
        this.cells = RLE.decode(rleString);
        this.name = name;
        this.rleString = rleString;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pattern pattern = (Pattern) o;

        return id == pattern.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
