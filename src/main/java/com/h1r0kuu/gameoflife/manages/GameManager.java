package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.components.ButtonComponent;
import com.h1r0kuu.gameoflife.components.CanvasComponent;
import com.h1r0kuu.gameoflife.components.SliderComponent;
import com.h1r0kuu.gameoflife.theme.Theme;

public class GameManager {
    private final GameLoopManager gameLoopManager;
    public final GameBoardManager gameBoardManager;
    private final UIManager uiManager;

    private boolean isPaused = false;
    private boolean pausedByButton = false;
    private int gameSpeed = 10;
    private int generation = 0;

    public static UserActionState userActionState = UserActionState.DRAWING;
    public static final ThemeManager themeManager = new ThemeManager();
    public static Theme getCurrentTheme() { return themeManager.getCurrentTheme(); }

    public GameManager(CanvasComponent canvas,
                       ButtonComponent pauseButton,
                       ButtonComponent fpsCounterButton,
                       ButtonComponent generationCounterButton,
                       ButtonComponent drawButton,
                       ButtonComponent moveButton,
                       ButtonComponent selectButton,
                       ButtonComponent showBorderButton,
                       SliderComponent gameSpeedSlider,
                       ButtonComponent gameSpeedText) {
        this.uiManager = new UIManager(
                this,
                canvas,
                pauseButton,
                fpsCounterButton,
                generationCounterButton,
                drawButton,
                moveButton,
                selectButton,
                showBorderButton,
                gameSpeedSlider,
                gameSpeedText);
        this.gameLoopManager = new GameLoopManager(this);
        this.gameBoardManager = new GameBoardManager(this);
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

    public UIManager getUiManager() {
        return uiManager;
    }
}
