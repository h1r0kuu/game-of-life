package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.GameOfLife;
import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.components.ButtonComponent;
import com.h1r0kuu.gameoflife.components.CanvasComponent;
import com.h1r0kuu.gameoflife.components.SliderComponent;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class UIManager {
    private static final Logger logger = LogManager.getLogger(UIManager.class);

    private final GameManager gameManager;

    private final CanvasComponent canvas;
    private final ButtonComponent pauseButton;
    private final ButtonComponent fpsCounterButton;
    private final ButtonComponent generationCounterButton;
    private final ButtonComponent drawButton;
    private final ButtonComponent drawPauseButton;

    private final ButtonComponent moveButton;
    private final ButtonComponent selectButton;
    private final ButtonComponent showBorderButton;

    private final SliderComponent gameSpeedSlider;
    private final ButtonComponent gameSpeedText;

    private final ButtonComponent cellInfoText;

    public UIManager(GameManager gameManager,
                     CanvasComponent canvas,
                     ButtonComponent pauseButton,
                     ButtonComponent fpsCounterButton,
                     ButtonComponent generationCounterButton,
                     ButtonComponent drawButton,
                     ButtonComponent drawPauseButton,
                     ButtonComponent moveButton,
                     ButtonComponent selectButton,
                     ButtonComponent showBorderButton,
                     SliderComponent gameSpeedSlider,
                     ButtonComponent gameSpeedText,
                     ButtonComponent cellInfoText) {
        this.gameManager = gameManager;

        this.canvas = canvas;
        this.pauseButton = pauseButton;
        this.fpsCounterButton = fpsCounterButton;
        this.generationCounterButton = generationCounterButton;

        this.drawButton = drawButton;
        this.drawPauseButton = drawPauseButton;

        this.moveButton = moveButton;
        this.selectButton = selectButton;
        this.showBorderButton = showBorderButton;

        this.gameSpeedSlider = gameSpeedSlider;
        this.gameSpeedText = gameSpeedText;

        this.cellInfoText = cellInfoText;
        logger.info("UIManager init");
    }

    public void setGamePause(boolean pause) {
        gameManager.setPaused(pause);
        if(pause) {
            pauseButton.changeImage(new Image(Objects.requireNonNull(GameOfLife.class.getResource("icons/play.png")).toExternalForm()));
        } else {
            gameManager.startGameLoop();
            pauseButton.changeImage(new Image(Objects.requireNonNull(GameOfLife.class.getResource("icons/pause.png")).toExternalForm()));
        }
    }

    public void handlePauseButtonClick(MouseEvent e) {
        setGamePause(!gameManager.isPaused());
        gameManager.setPausedByButton(gameManager.isPaused());
    }

    public void handleNextGenerationButtonClick(MouseEvent e) {
        gameManager.gameBoardManager.nextGeneration();
    }

    public void handlePreviousGenerationButtonClick(MouseEvent e) {
        gameManager.gameBoardManager.previousGeneration();
    }

    public void handleClearBoardButtonClick(MouseEvent e) {
        gameManager.gameBoardManager.clearBoard();
    }

    public void toggleShowBorder() {
        canvas.grid.setShowBorders(!canvas.grid.isShowBorders());
        if(canvas.grid.isShowBorders()) {
            showBorderButton.setActive(true);
            showBorderButton.getRectangle().setFill(ButtonComponent.DEFAULT_ACTIVE_FILL);
        } else {
            showBorderButton.setActive(false);
            showBorderButton.getRectangle().setFill(ButtonComponent.IDLE_BUTTON_COLOR);
        }
    }

    public void handleShowBorderButtonClick(MouseEvent e) {
        toggleShowBorder();
    }

    public void handleNewStateButtonClick(MouseEvent e, UserActionState userActionState) {
        gameManager.changeState(userActionState);
    }

    public void handleDrawPauseButtonClick(MouseEvent e) {
        gameManager.setPauseWhenDraw(!gameManager.isPauseWhenDraw());
        drawPauseButton.setActive(gameManager.isPauseWhenDraw());
    }

    public void handleGameSpeedSliderClickAndDragged(MouseEvent e) {
        int newGameSpeed = (int)gameSpeedSlider.getValue();
        gameManager.setGameSpeed(newGameSpeed);
        gameSpeedText.setText(LabelUtility.getText(newGameSpeed, LabelUtility.GAME_SPEED));
    }

    public ButtonComponent getFpsCounterButton() {
        return fpsCounterButton;
    }

    public ButtonComponent getGenerationCounterButton() {
        return generationCounterButton;
    }

    public Grid getGrid() {
        return canvas.grid;
    }

    public ButtonComponent getDrawButton() {
        return drawButton;
    }

    public ButtonComponent getMoveButton() {
        return moveButton;
    }

    public ButtonComponent getSelectButton() {
        return selectButton;
    }

    public void setCellInfo(int row, int col, boolean state) {
        cellInfoText.setText(LabelUtility.getText(LabelUtility.CELL_INFO, row, col, state ? "alive" : "dead"));
    }
}
