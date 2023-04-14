package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.theme.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameManager {
    private static final Logger logger = LogManager.getLogger(GameManager.class);

    private final GameLoopManager gameLoopManager;
    public final GameBoardManager gameBoardManager;
    private UIManager uiManager;

    private boolean isPaused = true;
    private boolean pausedByButton = true;
    private int gameSpeed = 10;
    private int generation = 0;

    public static UserActionState userActionState = UserActionState.DRAWING;
    public static final ThemeManager themeManager = new ThemeManager();
    public static Theme getCurrentTheme() { return themeManager.getCurrentTheme(); }

    public GameManager(GameLoopManager gameLoopManager,
                       GameBoardManager gameBoardManager) {
        this.gameLoopManager = gameLoopManager;
        this.gameBoardManager = gameBoardManager;
        logger.info("GameManager init");
    }

    public void startGameLoop() {
        gameLoopManager.startGameLoop();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isPausedByButton() {
        return pausedByButton;
    }

    public void setPausedByButton(boolean pausedByButton) {
        this.pausedByButton = pausedByButton;
    }

    public int increaseGeneration() {
        return ++this.generation;
    }

    public int decreaseGeneration() {
        return --this.generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public GameBoardManager getGameBoardManager() {
        return gameBoardManager;
    }

    public UIManager getUiManager() {
        return uiManager;
    }
}
