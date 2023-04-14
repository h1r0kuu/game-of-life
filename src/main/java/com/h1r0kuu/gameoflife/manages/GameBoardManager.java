package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;

public class GameBoardManager {
    private static final Logger logger = LogManager.getLogger(GameBoardManager.class);

    private GameManager gameManager;
    private UIManager uiManager;
    private Grid grid;
    private final Stack<Cell[][]> gameBoardHistory = new Stack<>();

    public GameBoardManager() {
        logger.info("GameBoardManager init");
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

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public void setGrid(Grid grid) {
        logger.info("Set GameBoardManager Grid");
        this.grid = grid;
    }
}
