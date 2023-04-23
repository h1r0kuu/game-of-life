package com.h1r0kuu.gameoflife.models;

import com.h1r0kuu.gameoflife.service.grid.IGridService;

import java.util.HashSet;
import java.util.Set;

public class LifeLikeRule implements Rule {

    private String ruleString;

    private final Set<Integer> survivalNeighboursCounts;
    private final Set<Integer> birthNeighboursCounts;

    public LifeLikeRule(Set<Integer> survivalNeighboursCounts, Set<Integer> birthNeighbours) {
        this.survivalNeighboursCounts = survivalNeighboursCounts;
        this.birthNeighboursCounts = birthNeighbours;
    }

    public LifeLikeRule(String ruleString) {
        this.ruleString = ruleString;
        String[] split = null;
        if(ruleString.toLowerCase().startsWith("b")) {
            split = ruleString.split("/");
            split[0] = split[0].substring(1);
            split[1] = split[1].substring(1);
        } else {
            split = ((new StringBuilder(ruleString)).reverse().toString()).split("/");
        }
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid rule string: " + ruleString);
        }

        try {
            birthNeighboursCounts = new HashSet<>();
            String birth = split[0];
            for (int i = 0; i < birth.length(); i++) {
                birthNeighboursCounts.add(Integer.valueOf(String.valueOf(birth.charAt(i))));
            }

            survivalNeighboursCounts = new HashSet<>();
            String creation = split[1];
            for (int i = 0; i < creation.length(); i++) {
                survivalNeighboursCounts.add(Integer.valueOf(String.valueOf(creation.charAt(i))));
            }

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rule string: " + ruleString);
        }
    }

    @Override
    public boolean apply(Grid grid, IGridService IGridService, int row, int col) {
        int n = IGridService.getAliveNeighbours(grid, row, col);

        if (grid.getCell(row, col).isAlive()) {
            return staysAlive(n);
        } else {
            return getsBirth(n);
        }
    }

    @Override
    public boolean apply(Grid grid, IGridService IGridService, int index) {
        int row = index / grid.getCols();
        int col = index % grid.getCols();
        return apply(grid, IGridService, row, col);
    }

    private boolean staysAlive(int n) {
        return survivalNeighboursCounts.contains(n);
    }

    private boolean getsBirth(int n) {
        return birthNeighboursCounts.contains(n);
    }
}
