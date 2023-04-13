package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.utils.LabelUtility;

import java.util.Stack;

public class GameBoardManager {
    private final GameManager gameManager;
    private final UIManager uiManager;
    private final Grid grid;
    private final Stack<Cell[][]> gameBoardHistory = new Stack<>();

    public GameBoardManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.uiManager = gameManager.getUiManager();
        this.grid = gameManager.getUiManager().getGrid();
    }

    public void nextGeneration() {
        gameBoardHistory.push(grid.getCells());
        grid.nextGeneration();
        uiManager.getGenerationCounterButton().setText(LabelUtility.getText(gameManager.increaseGeneration(), LabelUtility.GENERATION_COUNTER));
    }

    public void previousGeneration() {
        if (!gameBoardHistory.isEmpty()) {
            grid.setCells(gameBoardHistory.pop());
            uiManager.getGenerationCounterButton().setText(LabelUtility.getText(gameManager.decreaseGeneration(), LabelUtility.GENERATION_COUNTER));
        }
    }

    public void clearBoard() {
        grid.clearGrid();
        gameManager.setGeneration(0);
        uiManager.getGenerationCounterButton().setText(LabelUtility.getText(0, LabelUtility.GENERATION_COUNTER));
    }

    public void redrawBoard() {
        grid.update();
    }

}
