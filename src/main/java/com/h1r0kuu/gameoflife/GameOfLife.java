package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.components.*;
import com.h1r0kuu.gameoflife.handlers.CanvasMouseHandlers;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.manages.UIManager;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

public class GameOfLife extends Application {

    private StackPaneComponent stackPane;
    private CanvasWrapper canvasWrapper;
    private CanvasComponent canvas;
    private BorderPaneComponent borderPane;
    private PaneComponent topPane;
    private PaneComponent rightPane;
    private PaneComponent bottomPane;

    private ButtonComponent showBorderButton;
    private ChoiceBox<String> themeChoiceBox;
    private SliderComponent gameSpeedSlider;
    private ButtonComponent gameSpeedSliderText;

    private ButtonComponent fpsCounterButton;

    private ButtonComponent drawButton;
    private ButtonComponent moveButton;
    private ButtonComponent selectButton;

    private ButtonComponent nextGenerationButton;
    private ButtonComponent pauseButton;
    private ButtonComponent previousGenerationButton;
    private ButtonComponent clearBoardButton;

    private ButtonComponent generationCounterButton;


    public void initCanvas() {
        canvas = new CanvasComponent(1024, 1024);
        canvasWrapper = new CanvasWrapper(canvas);
        canvasWrapper.setPrefSize(100, 200);
        canvasWrapper.setBackground(Background.fill(Color.web("#666666")));
        canvasWrapper.setAlignment(Pos.CENTER);
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());
    }

    public void initBorderPane() {
        borderPane = new BorderPaneComponent();
        borderPane.setPickOnBounds(false);
    }

    public void initTopPane() {
        topPane = new PaneComponent();
        topPane.setPrefSize(600, 50);

        ImageViewComponent showBordersImage = new ImageViewComponent("icons/grid.png");
        showBorderButton = new ButtonComponent(556, 7, showBordersImage);

        themeChoiceBox = new ChoiceBox<>();
        themeChoiceBox.setLayoutX(461);
        themeChoiceBox.setLayoutY(13);

        ObservableList<String> themeNames = FXCollections.observableArrayList(GameManager.themeManager.getThemes().stream().map(t -> t.THEME_NAME).toList());
        themeChoiceBox.setItems(themeNames);
        if(themeNames.size() > 0) themeChoiceBox.setValue(themeNames.get(0));

        themeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            String newTheme = themeChoiceBox.getItems().get((Integer) number2);
            GameManager.themeManager.changeTheme(newTheme);
        });

        fpsCounterButton = new ButtonComponent(7, 7, LabelUtility.getText(60, LabelUtility.FPS));
        fpsCounterButton.setTextFill(Color.rgb(0, 255, 0));

        topPane.getChildren().addAll(showBorderButton, themeChoiceBox, fpsCounterButton);
        borderPane.setTop(topPane);
    }

    public void initRightPane() {
        rightPane = new PaneComponent();
        rightPane.setPrefSize(50, 350);

        ImageViewComponent drawButtonImage = new ImageViewComponent("icons/pen.png");
        drawButton = new ButtonComponent(7, 14, drawButtonImage);
        drawButton.getRectangle().setFill(ButtonComponent.DEFAULT_ACTIVE_FILL);
        drawButton.setActive(true);

        ImageViewComponent moveButtonImage = new ImageViewComponent("icons/pan.png");
        moveButton = new ButtonComponent(7, 100, moveButtonImage);

        ImageViewComponent selectButtonImage = new ImageViewComponent("icons/select.png");
        selectButton = new ButtonComponent(7, 57, selectButtonImage);

        rightPane.getChildren().addAll(drawButton, moveButton, selectButton);
        borderPane.setRight(rightPane);
    }

    public void initBottomPane() {
        bottomPane = new PaneComponent();
        bottomPane.setPrefSize(600, 50);

        ImageViewComponent nextGenerationButtonImage = new ImageViewComponent("icons/next.png");
        nextGenerationButton = new ButtonComponent(506, 7, nextGenerationButtonImage);

        ImageViewComponent pauseButtonImage = new ImageViewComponent("icons/pause.png");
        pauseButton = new ButtonComponent(463, 7, pauseButtonImage);

        ImageViewComponent previousGenerationButtonImage = new ImageViewComponent("icons/previous.png");
        previousGenerationButton = new ButtonComponent(421, 7, previousGenerationButtonImage);

        ImageViewComponent resetGenerationButtonImage = new ImageViewComponent("icons/reset.png");
        clearBoardButton = new ButtonComponent(378, 7, resetGenerationButtonImage);

        gameSpeedSlider = new SliderComponent(1, 64, 10);
        gameSpeedSlider.setLayoutX(182);
        gameSpeedSlider.setLayoutY(7);

        gameSpeedSliderText = new ButtonComponent(150, 7, LabelUtility.getText(10, LabelUtility.GAME_SPEED));

        generationCounterButton = new ButtonComponent(14, 7, LabelUtility.getText(0, LabelUtility.GENERATION_COUNTER));

        bottomPane.getChildren().addAll(generationCounterButton,
                nextGenerationButton,
                pauseButton,
                previousGenerationButton,
                gameSpeedSlider,
                gameSpeedSliderText,
                clearBoardButton);
        borderPane.setBottom(bottomPane);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stackPane = new StackPaneComponent();
        stackPane.setPrefSize(600, 400);

        initCanvas();
        initBorderPane();
        initTopPane();
        initRightPane();
        initBottomPane();

        GameManager gameManager = new GameManager(canvas,
                pauseButton,
                fpsCounterButton,
                generationCounterButton,
                drawButton,
                moveButton,
                selectButton,
                showBorderButton,
                gameSpeedSlider,
                gameSpeedSliderText);
        UIManager uiManager = gameManager.getUiManager();

        nextGenerationButton.setOnMousePressed(uiManager::handleNextGenerationButtonClick);
        pauseButton.setOnMouseClicked(uiManager::handlePauseButtonClick);
        previousGenerationButton.setOnMousePressed(uiManager::handlePreviousGenerationButtonClick);
        clearBoardButton.setOnMouseClicked(uiManager::handleClearBoardButtonClick);
        drawButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.DRAWING));
        moveButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.MOVING));
        selectButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.SELECTING));
        showBorderButton.setOnMouseClicked(uiManager::handleShowBorderButtonClick);
        gameSpeedSlider.setOnMouseClicked(uiManager::handleGameSpeedSliderClickAndDragged);
        gameSpeedSlider.setOnMouseDragged(uiManager::handleGameSpeedSliderClickAndDragged);
        canvas.setHandler(new CanvasMouseHandlers(canvas, gameManager, uiManager));

        stackPane.setGameManager(gameManager);
        stackPane.getChildren().addAll(canvasWrapper, borderPane);

        Scene scene = new Scene(stackPane);
        primaryStage.setTitle("Title");
        primaryStage.setScene(scene);
        primaryStage.show();
        stackPane.requestFocus();
        gameManager.startGameLoop();
    }

    public void startGame(String[] args) {
        launch(args);
    }

}