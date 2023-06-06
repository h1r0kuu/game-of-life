package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.enums.PastingType;
import com.h1r0kuu.gameoflife.enums.UserActionState;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.models.Theme;
import com.h1r0kuu.gameoflife.service.grid.IGridService;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;

public class GameManager {
    private static final Logger logger = LogManager.getLogger(GameManager.class);

    private boolean isPaused = true;
    private boolean pausedByButton = true;
    private boolean pauseWhenDraw = true;
    private int gameSpeed = 10;
    private int generation = 0;

    private ImageView playImage;
    private Button drawButton;
    private Pane drawButtonGroup;
    private Button selectionButton;
    private Pane selectButtonGroup;
    private Button moveButton;

    private Button populationChartButton;

    private Button pasteAnd;
    private Button pasteCpy;
    private Button pasteOr;
    private Button pasteXor;

    private ComboBox<String> themes;

    private Label generationLabel;

    private ObservableList<XYChart.Data<Number, Number>> graphData;
    private Stage chartStage;

    private final Stack<Cell[]> gameBoardHistory = new Stack<>();
    private final Grid grid;
    private final IGridService iGridService;
    private final Canvas canvas;

    public static UserActionState userActionState = UserActionState.DRAWING;
    public static UserActionState userPreviousState = UserActionState.DRAWING;
    public static PastingType pastingType = PastingType.OR;
    public static final ThemeManager themeManager = new ThemeManager();
    public static final PatternManager patternManager = new PatternManager();

    public static Theme getCurrentTheme() {return themeManager.getCurrentTheme();}

    public GameManager(Grid grid, IGridService iGridService, Canvas canvas) {
        this.grid = grid;
        this.iGridService = iGridService;
        this.canvas = canvas;
        logger.info("GameManager init");
    }

    public void nextGeneration() {
        gameBoardHistory.push(grid.getCells());
        iGridService.nextGeneration();
        increaseGeneration();
        addData(generation, grid.getPopulation());
        generationLabel.setText(LabelUtility.getText(LabelUtility.GENERATION_COUNTER, generation));
    }

    public void previousGeneration() {
        if (!gameBoardHistory.isEmpty()) {
            grid.setCells(gameBoardHistory.pop());
            decreaseGeneration();
            generationLabel.setText(LabelUtility.getText(LabelUtility.GENERATION_COUNTER, generation));
        }
    }

    public void clearBoard() {
        iGridService.clearGrid();
        graphData.clear();
        setGeneration(0);
        generationLabel.setText(LabelUtility.getText(LabelUtility.GENERATION_COUNTER, generation));
    }


    public boolean isPaused() {
        return isPaused;
    }

    public void changeButtonImage(boolean pause) {
        if(!pause) {
            playImage.setImage(Constants.PAUSE_IMAGE);
        } else {
            playImage.setImage(Constants.PLAY_IMAGE);
        }
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        changeButtonImage(paused);
    }

    public boolean isPausedByButton() {
        return pausedByButton;
    }

    public void setPausedByButton(boolean pausedByButton) {
        this.pausedByButton = pausedByButton;
    }

    public boolean isPauseWhenDraw() {
        return pauseWhenDraw;
    }

    public void setPauseWhenDraw(boolean pauseWhenDraw) {
        this.pauseWhenDraw = pauseWhenDraw;
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


    public void changeState(UserActionState newState) {
        userActionState = newState;

        drawButton.setStyle(Constants.IDLE_BUTTON);
        moveButton.setStyle(Constants.IDLE_BUTTON);
        selectionButton.setStyle(Constants.IDLE_BUTTON);
        drawButtonGroup.setVisible(false);
        selectButtonGroup.setVisible(false);
        switch (userActionState) {
            case DRAWING -> {
                canvas.setCursor(Cursor.DEFAULT);
                drawButton.setStyle(Constants.ACTIVE_BUTTON);
                drawButtonGroup.setVisible(true);
            }
            case MOVING -> {
                canvas.setCursor(Cursor.MOVE);
                moveButton.setStyle(Constants.ACTIVE_BUTTON);
            }
            case SELECTING -> {
                canvas.setCursor(Cursor.CROSSHAIR);
                selectionButton.setStyle(Constants.ACTIVE_BUTTON);
                selectButtonGroup.setVisible(true);
            }
        }
    }

    public void changePasteMode(PastingType newType) {
        pastingType = newType;

        pasteAnd.setStyle(Constants.IDLE_BUTTON);
        pasteCpy.setStyle(Constants.IDLE_BUTTON);
        pasteOr.setStyle(Constants.IDLE_BUTTON);
        pasteXor.setStyle(Constants.IDLE_BUTTON);
        switch (newType) {
            case AND -> {
                pasteAnd.setStyle(Constants.ACTIVE_BUTTON);
            }
            case CPY -> {
                pasteCpy.setStyle(Constants.ACTIVE_BUTTON);
            }
            case OR -> {
                pasteOr.setStyle(Constants.ACTIVE_BUTTON);
            }
            case XOR -> {
                pasteXor.setStyle(Constants.ACTIVE_BUTTON);
            }
        }
    }

    public void changeTheme(String newTheme) {
        themeManager.changeTheme(newTheme);
        themes.setValue(newTheme);
    }

    public void setButtons(ImageView playImage, Button drawButton, Button selectionButton, Button moveButton, Button populationChart) {
        this.playImage = playImage;
        this.drawButton = drawButton;
        this.selectionButton = selectionButton;
        this.moveButton = moveButton;
        this.populationChartButton = populationChart;
    }

    public void setGroups(Pane selectButtonGroup, Pane drawButtonGroup) {
        this.selectButtonGroup = selectButtonGroup;
        this.drawButtonGroup = drawButtonGroup;
    }

    public void setPasteModeButtons(Button pasteAnd, Button pasteCpy, Button pasteOr, Button pasteXor) {
        this.pasteAnd = pasteAnd;
        this.pasteCpy = pasteCpy;
        this.pasteOr = pasteOr;
        this.pasteXor = pasteXor;
    }

    public void toggleChart() {
        if(chartStage.isShowing()) {
            chartStage.close();
            populationChartButton.setStyle(Constants.IDLE_BUTTON);
        } else {
            chartStage.show();
            populationChartButton.setStyle(Constants.ACTIVE_BUTTON);
        }
    }

    public Grid getGrid() {
        return grid;
    }

    public void setThemesCombobox(ComboBox<String> themes) {
        this.themes = themes;
    }

    public void setGenerationLabel(Label generationLabel) {
        this.generationLabel = generationLabel;
    }

    public void setGraphData(ObservableList<XYChart.Data<Number, Number>> graphData) {
        this.graphData = graphData;
    }

    public void addData(int generation, int population) {
        graphData.add(new XYChart.Data<>(generation, population));
    }

    public void setChartStage(Stage chartStage) {
        this.chartStage = chartStage;
    }

    public void onChartClose() {
        populationChartButton.setStyle(Constants.IDLE_BUTTON);
    }
}