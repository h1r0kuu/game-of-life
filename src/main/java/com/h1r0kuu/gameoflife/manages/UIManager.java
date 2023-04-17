package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.GameOfLife;
import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.components.*;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.entity.Move;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class UIManager {
    private static final Logger logger = LogManager.getLogger(UIManager.class);

    private final GameManager gameManager;

    private final CanvasWrapper canvasWrapper;
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

    private final ComboBoxComponent<String> patternComboBox;

    public UIManager(GameManager gameManager,
                     CanvasWrapper canvasWrapper,
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
                     ButtonComponent cellInfoText,
                     ComboBoxComponent<String> patternComboBox) {
        this.gameManager = gameManager;

        this.canvasWrapper = canvasWrapper;
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

        this.patternComboBox = patternComboBox;
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

    public void handleClickClearSelectionButton(MouseEvent e) {
        gameManager.getGameBoardManager().clearSelectedCells();
    }

    public void handlePressMoveSelectedCellsButtons(MouseEvent e, Move move) {
        gameManager.getGameBoardManager().moveSelectedCells(move);
    }

    public ChangeListener<? super Number> handlePatternComboboxChange() {
        return ((observable, oldValue, newValue) -> {
            String newPattern = patternComboBox.getItems().get((Integer)newValue);
            canvas.grid.initPattern(GameManager.patternManager.getByName(newPattern));
        });
    }

    public CanvasWrapper getCanvasWrapper() {
        return canvasWrapper;
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
