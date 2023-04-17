package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.components.*;
import com.h1r0kuu.gameoflife.entity.Move;
import com.h1r0kuu.gameoflife.entity.Pattern;
import com.h1r0kuu.gameoflife.handlers.CanvasMouseHandlers;
import com.h1r0kuu.gameoflife.manages.*;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GameOfLife extends Application {
    private static final Logger logger = LogManager.getLogger(GameOfLife.class);

    private static final int PADDING = 10;

    private StackPaneComponent stackPane;
    private CanvasWrapper canvasWrapper;
    private CanvasComponent canvas;
    private BorderPaneComponent borderPane;
    private PaneComponent topPane;
    private PaneComponent rightPane;
    private PaneComponent bottomPane;
    private PaneComponent centerPane;

    private ButtonComponent showBorderButton;

    private ComboBoxComponent<String> patternComboBox;
    private ChoiceBox<String> themeChoiceBox;

    private SliderComponent gameSpeedSlider;
    private ButtonComponent gameSpeedSliderText;

    private ButtonComponent cellInfoText;

    private ButtonComponent fpsCounterButton;

    private ButtonComponent drawButton;
    private ButtonComponent drawPauseButton;

    private ButtonComponent moveButton;
    private ButtonComponent selectButton;
    private ButtonComponent selectAllButton;
    private ButtonComponent cancelSelectionButton;
    private ButtonComponent leftSelectionButton;
    private ButtonComponent rightSelectionButton;
    private ButtonComponent upSelectionButton;
    private ButtonComponent downSelectionButton;
    private ButtonComponent invertSelectionButton;
    private ButtonComponent rotateLeftSelectionButton;
    private ButtonComponent rotateRightSelectionButton;

    private ButtonComponent nextGenerationButton;
    private ButtonComponent pauseButton;
    private ButtonComponent previousGenerationButton;
    private ButtonComponent clearBoardButton;

    private ButtonComponent generationCounterButton;

    private GameManager gameManager;
    private GameBoardManager gameBoardManager;
    private GameLoopManager gameLoopManager;
    private UIManager uiManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stackPane = new StackPaneComponent();
        stackPane.setMaxSize(600, 600);

        initCanvas();
        initBorderPane();
        initTopPane();
        initRightPane();
        initBottomPane();
        initCenterPane();
        initManagers();

        nextGenerationButton.setOnMousePressed(uiManager::handleNextGenerationButtonClick);
        pauseButton.setOnMouseClicked(uiManager::handlePauseButtonClick);
        previousGenerationButton.setOnMousePressed(uiManager::handlePreviousGenerationButtonClick);
        clearBoardButton.setOnMouseClicked(uiManager::handleClearBoardButtonClick);

        drawButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.DRAWING));
        drawPauseButton.setOnMouseClicked(uiManager::handleDrawPauseButtonClick);

        moveButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.MOVING));
        selectButton.setOnMouseClicked(e -> uiManager.handleNewStateButtonClick(e, UserActionState.SELECTING));
        cancelSelectionButton.setOnMouseClicked(uiManager::handleClickClearSelectionButton);
        leftSelectionButton.setOnMouseClicked(e -> uiManager.handlePressMoveSelectedCellsButtons(e, Move.LEFT));
        rightSelectionButton.setOnMouseClicked(e -> uiManager.handlePressMoveSelectedCellsButtons(e, Move.RIGHT));
        upSelectionButton.setOnMouseClicked(e -> uiManager.handlePressMoveSelectedCellsButtons(e, Move.UP));
        downSelectionButton.setOnMouseClicked(e -> uiManager.handlePressMoveSelectedCellsButtons(e, Move.DOWN));

        showBorderButton.setOnMouseClicked(uiManager::handleShowBorderButtonClick);
        gameSpeedSlider.setOnMouseClicked(uiManager::handleGameSpeedSliderClickAndDragged);
        gameSpeedSlider.setOnMouseDragged(uiManager::handleGameSpeedSliderClickAndDragged);

        patternComboBox.handleChange(uiManager.handlePatternComboboxChange());

        canvas.setHandler(new CanvasMouseHandlers(canvas, gameManager, uiManager));

        stackPane.setGameManager(gameManager);
        stackPane.getChildren().addAll(canvasWrapper, borderPane);

        Scene scene = new Scene(stackPane, 600, 600);
        primaryStage.setTitle("Title");
        primaryStage.setScene(scene);
        logger.info("Start");
        loadPatterns(primaryStage);
        gameManager.startGameLoop();
    }

    public void initCanvas() {
        canvas = new CanvasComponent(2046, 2046);
        canvasWrapper = new CanvasWrapper(canvas);

    }

    public void initBorderPane() {
        borderPane = new BorderPaneComponent();
        borderPane.setPickOnBounds(false);
    }

    public void initTopPane() {
        topPane = new PaneComponent();
        topPane.setPickOnBounds(false);
        topPane.setPadding(new Insets(PADDING, PADDING, 0, PADDING));

        ImageViewComponent showBordersImage = new ImageViewComponent("icons/grid.png");

        showBorderButton = new ButtonComponent(556, 0, showBordersImage);

        patternComboBox = new ComboBoxComponent<>();

        themeChoiceBox = new ChoiceBox<>();
        themeChoiceBox.setLayoutX(350);

        ObservableList<String> themeNames = FXCollections.observableArrayList(GameManager.themeManager.getThemes().stream().map(t -> t.THEME_NAME).toList());
        themeChoiceBox.setItems(themeNames);
        if(themeNames.size() > 0) themeChoiceBox.setValue(themeNames.get(0));

        themeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            String newTheme = themeChoiceBox.getItems().get((Integer) number2);
            GameManager.themeManager.changeTheme(newTheme);
        });

        fpsCounterButton = new ButtonComponent(0, 0, LabelUtility.getText(60, LabelUtility.FPS));
        fpsCounterButton.setTextFill(Color.rgb(0, 255, 0));

        topPane.getChildren().addAll(showBorderButton, patternComboBox, themeChoiceBox, fpsCounterButton);
        borderPane.setTop(topPane);
    }

    public DropdownButtonComponent initSelectDropdown() {

        ImageViewComponent selectAllImage = new ImageViewComponent("icons/select_all.png");
        selectAllButton = new ButtonComponent(0, 0, selectAllImage);

        ImageViewComponent cancelSelectionImage = new ImageViewComponent("icons/selection-cancel.png");
        cancelSelectionButton = new ButtonComponent(0, 0, cancelSelectionImage);

        ImageViewComponent leftArrowImage = new ImageViewComponent("icons/left-arrow.png");
        leftSelectionButton = new ButtonComponent(0, 0, leftArrowImage);

        ImageViewComponent rightArrowImage = new ImageViewComponent("icons/right-arrow.png");
        rightSelectionButton = new ButtonComponent(0, 0, rightArrowImage);

        ImageViewComponent upArrowImage = new ImageViewComponent("icons/up-arrow.png");
        upSelectionButton = new ButtonComponent(0, 0, upArrowImage);

        ImageViewComponent downArrowImage = new ImageViewComponent("icons/down-arrow.png");
        downSelectionButton = new ButtonComponent(0, 0, downArrowImage);

        ImageViewComponent invertSelectionImage = new ImageViewComponent("icons/invert-selection.png");
        invertSelectionButton = new ButtonComponent(0, 0, invertSelectionImage);

        ImageViewComponent rotateLeftSelectionImage = new ImageViewComponent("icons/rotate-left.png");
        rotateLeftSelectionButton = new ButtonComponent(0, 0, rotateLeftSelectionImage);

        ImageViewComponent rotateRightSelectionImage = new ImageViewComponent("icons/rotate-right.png");
        rotateRightSelectionButton = new ButtonComponent(0, 0, rotateRightSelectionImage);

        DropdownButtonComponent dropdown = new DropdownButtonComponent(
                selectAllButton,
                cancelSelectionButton,
                leftSelectionButton,
                rightSelectionButton,
                upSelectionButton,
                downSelectionButton,
                invertSelectionButton,
                rotateLeftSelectionButton,
                rotateRightSelectionButton);
        dropdown.setLayoutY(57);
        dropdown.setLayoutX((int)(PADDING - selectButton.getRectangle().getWidth()));
        return dropdown;
    }

    public void initRightPane() {
        rightPane = new PaneComponent();
        rightPane.setPickOnBounds(false);
        rightPane.setPadding(new Insets(PADDING, PADDING, PADDING, 0));

        ImageViewComponent drawButtonImage = new ImageViewComponent("icons/pen.png");
        drawButton = new ButtonComponent(PADDING, 14, drawButtonImage);
        drawButton.getRectangle().setFill(ButtonComponent.DEFAULT_ACTIVE_FILL);
        drawButton.setActive(true);
//        DropButton dropdown
        ImageViewComponent penPause = new ImageViewComponent("icons/pen_pause.png");
        drawPauseButton = new ButtonComponent((int)(PADDING - drawButton.getRectangle().getWidth()), 14, penPause);
        drawPauseButton.setActive(true);

        ImageViewComponent moveButtonImage = new ImageViewComponent("icons/pan.png");
        moveButton = new ButtonComponent(PADDING, 100, moveButtonImage);

        ImageViewComponent selectButtonImage = new ImageViewComponent("icons/select.png");
        selectButton = new ButtonComponent(PADDING, 57, selectButtonImage);
        DropdownButtonComponent selectionDropdown = initSelectDropdown();
        selectButton.setDropdown(selectionDropdown);

        rightPane.getChildren().addAll(drawButton, drawPauseButton, moveButton, selectButton, selectionDropdown);

        borderPane.setRight(rightPane);
    }

    public void initBottomPane() {
        bottomPane = new PaneComponent();
        bottomPane.setPickOnBounds(false);
        bottomPane.setPadding(new Insets(0, PADDING, PADDING, PADDING));

        ImageViewComponent nextGenerationButtonImage = new ImageViewComponent("icons/next.png");
        nextGenerationButton = new ButtonComponent(506, 0, nextGenerationButtonImage);

        ImageViewComponent pauseButtonImage = new ImageViewComponent("icons/play.png");
        pauseButton = new ButtonComponent(463, 0, pauseButtonImage);

        ImageViewComponent previousGenerationButtonImage = new ImageViewComponent("icons/previous.png");
        previousGenerationButton = new ButtonComponent(421, 0, previousGenerationButtonImage);

        ImageViewComponent resetGenerationButtonImage = new ImageViewComponent("icons/reset.png");
        clearBoardButton = new ButtonComponent(378, 0, resetGenerationButtonImage);

        gameSpeedSlider = new SliderComponent(1, 64, 10);
        gameSpeedSlider.setLayoutX(182);
        gameSpeedSlider.setLayoutY(0);

        gameSpeedSliderText = new ButtonComponent(150, 0, LabelUtility.getText(10, LabelUtility.GAME_SPEED));

        generationCounterButton = new ButtonComponent(14, 0, LabelUtility.getText(0, LabelUtility.GENERATION_COUNTER));

        bottomPane.getChildren().addAll(generationCounterButton,
                nextGenerationButton,
                pauseButton,
                previousGenerationButton,
                gameSpeedSlider,
                gameSpeedSliderText,
                clearBoardButton);
        borderPane.setBottom(bottomPane);
    }

    public void initCenterPane() {
        centerPane = new PaneComponent();
        centerPane.setPickOnBounds(false);
        centerPane.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        cellInfoText = new ButtonComponent(PADDING + 5, 480, LabelUtility.getText(LabelUtility.CELL_INFO, 0,0,"dead"));

        centerPane.getChildren().addAll(cellInfoText);
        borderPane.setCenter(centerPane);
    }

    public void initManagers() {
        this.gameLoopManager = new GameLoopManager();
        this.gameBoardManager = new GameBoardManager();
        this.gameManager = new GameManager(gameLoopManager, gameBoardManager);

        this.uiManager = new UIManager(
                gameManager,
                canvasWrapper,
                canvas,
                pauseButton,
                fpsCounterButton,
                generationCounterButton,
                drawButton,
                drawPauseButton,
                moveButton,
                selectButton,
                showBorderButton,
                gameSpeedSlider,
                gameSpeedSliderText,
                cellInfoText,
                patternComboBox);

        gameManager.setUiManager(uiManager);

        gameBoardManager.setUiManager(uiManager);
        gameBoardManager.setGameManager(gameManager);
        gameBoardManager.setGrid(uiManager.getGrid());

        gameLoopManager.setGameManager(gameManager);
        gameLoopManager.setFpsCounterButton(fpsCounterButton);
    }



    private void loadPatterns(Stage primaryStage) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0.0);

        StackPane root = new StackPane();
        root.getChildren().add(progressBar);

        Stage progressStage = new Stage();
        Scene progressScene = new Scene(root, 400, 400);
        progressStage.setScene(progressScene);
        progressStage.show();

        new Thread(() -> {
            ClassLoader classLoader = Main.class.getClassLoader();

            try (Stream<Path> walk = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
                long totalFiles = walk.count();
                AtomicInteger fileCount = new AtomicInteger();
                try (Stream<Path> walk2 = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
                    walk2
                            .filter(p -> !Files.isDirectory(p))
                            .map(p -> p.toString().toLowerCase())
                            .filter(f -> f.endsWith("rle"))
                            .forEach(f -> {
                                try {
                                    File file = new File(f);
                                    String content = RLE.read(f);
                                    System.out.println(f);
                                    String name = RLE.getName(content);
                                    if(name == null) name = file.getName();
                                    Pattern pattern = new Pattern(name, content);
                                    GameManager.patternManager.addPattern(pattern);
                                    patternComboBox.addItem(name);
                                    Platform.runLater(() -> {
                                        progressBar.setProgress(fileCount.incrementAndGet() / (double) totalFiles);
                                    });
                                } catch (ArrayIndexOutOfBoundsException | IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                }
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                progressStage.hide();
                primaryStage.show();
            });

        }).start();
    }

    public void startGame(String[] args) {
        logger.info("Launch");
        launch(args);
    }
}