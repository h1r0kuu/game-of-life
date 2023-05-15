package com.h1r0kuu.gameoflife.controllers;

import com.h1r0kuu.gameoflife.enums.PastingType;
import com.h1r0kuu.gameoflife.handlers.CanvasWrapperMouseHandlers;
import com.h1r0kuu.gameoflife.handlers.UiHandler;
import com.h1r0kuu.gameoflife.renderer.LifeRenderer;
import com.h1r0kuu.gameoflife.GameOfLife;
import com.h1r0kuu.gameoflife.Main;
import com.h1r0kuu.gameoflife.components.SelectionRectangle;
import com.h1r0kuu.gameoflife.enums.MoveType;
import com.h1r0kuu.gameoflife.enums.UserActionState;
import com.h1r0kuu.gameoflife.handlers.CanvasMouseHandlers;
import com.h1r0kuu.gameoflife.handlers.HotKeysHandler;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.models.Grid;
import com.h1r0kuu.gameoflife.models.Pattern;
import com.h1r0kuu.gameoflife.service.grid.IGridService;
import com.h1r0kuu.gameoflife.service.grid.GridServiceImpl;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class AppController extends VBox {
    private static final Logger logger = LogManager.getLogger(GameOfLife.class);

    private double scaleValue = 0.7;

    // define UI elements
    @FXML private Button newPattern;
    @FXML private Button openPattern;
    @FXML private Button savePattern;
    @FXML private Button takePatternPicture;
    @FXML private Button autoFitPatternButton;
    @FXML private Button fitPatternButton;
    @FXML private Button drawButton;
    @FXML private Pane drawButtonGroup;
    @FXML private Button selectButton;
    @FXML private Pane selectButtonGroup;
    @FXML private Button moveButton;
    @FXML private Button populationChart;
    @FXML private Button zoomIn;
    @FXML private Button zoomOut;
    @FXML private Button showBorderButton;
    @FXML private Button previousGenerationButton;
    @FXML private ImageView playImage;
    @FXML private Button playButton;
    @FXML private Button nextGenerationButton;
    @FXML private Button pasteAnd;
    @FXML private Button pasteCpy;
    @FXML private Button pasteOr;
    @FXML private Button pasteXor;
    @FXML private Button moveSelectionLeft;
    @FXML private Button moveSelectionRight;
    @FXML private Button moveSelectionUp;
    @FXML private Button moveSelectionDown;
    @FXML private Button rotateSelectionRight;
    @FXML private Button rotateSelectionLeft;
    @FXML private Button inverseSelectedCells;
    @FXML private Button cancelSelection;
    @FXML private Label randomProbabilityLabel;
    @FXML private Slider randomProbability;
    @FXML private Button randomize;
    @FXML private Button pauseOnDraw;
    @FXML private Canvas canvas;
    @FXML private Rectangle selectRectangle;
    @FXML private Rectangle pasteRectangle;
    @FXML private TextField searchBar;
    @FXML private Label clearSearchBar;
    @FXML private ListView<String> patternList;
    @FXML private ComboBox<String> themes;
    @FXML private Slider gameSpeed;
    @FXML private Label gameSpeedLabel;
    @FXML private ScrollPane scrollPane;
    @FXML private StackPane canvasContainer;
//    @FXML private Group zoomNode;
    @FXML private Label cellInfo;
    @FXML private Label generationLabel;
    @FXML private VBox box;
    @FXML private VBox wrapper;
    @FXML private VBox outerNode;

    private Grid grid;
    private GameManager gameManager;
    private UiHandler uiHandler;
    private LifeRenderer lifeRenderer;
    private IGridService iGridService;

    public AppController() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/app.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        logger.info("GUI init");
        int rows = (int) Math.floor(canvas.getHeight() / Constants.CELL_SIZE);
        int cols = (int) Math.floor(canvas.getWidth() / Constants.CELL_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        SelectionRectangle rectangleForSelect = new SelectionRectangle(selectRectangle, pasteRectangle);
        this.grid = new Grid(rows, cols, rectangleForSelect);
        this.lifeRenderer = new LifeRenderer(grid, gc);
        this.iGridService = new GridServiceImpl(grid);
        this.gameManager = new GameManager(grid, iGridService, canvas);
        this.uiHandler = new UiHandler(gameManager);

        grid.init();
        Pattern pattern = GameManager.patternManager.getByName("Copperhead");
        if(pattern != null) {
            Cell[][] cells = RLE.decode(pattern.getRleString());
            iGridService.setPattern(cells);
        }
        gameManager.setButtons(playImage, drawButton, selectButton, moveButton, showBorderButton, populationChart);
        gameManager.setGroups(selectButtonGroup, drawButtonGroup);
        gameManager.setPasteModeButtons(pasteAnd, pasteCpy, pasteOr, pasteXor);
        gameManager.setThemesCombobox(themes);
        gameManager.setGenerationLabel(generationLabel);

        scrollPane.addEventFilter(ScrollEvent.SCROLL, this::handleScroll);

        updateScale();
        initCanvasHandlers();
        initScrollPaneHandlers();
        initButtonClicks();

        scrollPane.setHvalue(0.5);
        scrollPane.setVvalue(0.5);
    }

    private void handleScroll(ScrollEvent event) {
        double currentScale = canvas.getScaleX();

        double zoomFactor = 1.1;
        if (event.getDeltaY() < 0) {
            zoomFactor = 1 / zoomFactor;
        }

        double mousePosX = event.getX() - canvasContainer.getBoundsInParent().getMinX();
        double mousePosY = event.getY() - canvasContainer.getBoundsInParent().getMinY();

        canvas.setScaleX(currentScale * zoomFactor);
        canvas.setScaleY(currentScale * zoomFactor);

        double newMousePosX = event.getX() - canvasContainer.getBoundsInParent().getMinX();
        double newMousePosY = event.getY() - canvasContainer.getBoundsInParent().getMinY();
        double deltaX = newMousePosX - mousePosX;
        double deltaY = newMousePosY - mousePosY;
        scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / canvasContainer.getWidth());
        scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / canvasContainer.getHeight());

        event.consume();
    }

    public void initScrollPaneHandlers() {
        CanvasWrapperMouseHandlers canvasWrapperMouseHandlers = new CanvasWrapperMouseHandlers(scrollPane);
        scrollPane.addEventFilter(MouseEvent.MOUSE_RELEASED, canvasWrapperMouseHandlers::onMouseReleased);
        scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, canvasWrapperMouseHandlers::onMouseDragged);

    }

    public void initCanvasHandlers() {
        CanvasMouseHandlers canvasMouseHandlers = new CanvasMouseHandlers(gameManager, iGridService, grid, cellInfo);
        HotKeysHandler hotKeysHandler = new HotKeysHandler(gameManager, iGridService);
        canvas.setOnMouseMoved(canvasMouseHandlers::onMouseMove);
        canvas.setOnMousePressed(canvasMouseHandlers::onMousePressed);
        canvas.setOnMouseDragged(canvasMouseHandlers::onMouseDragged);
        canvas.setOnMouseReleased(canvasMouseHandlers::onMouseReleased);
        canvas.requestFocus();
        box.setOnKeyPressed(hotKeysHandler::onKeyPressed);
    }

    public void initButtonClicks() {
        nextGenerationButton.setOnMouseClicked(e -> gameManager.nextGeneration());
        previousGenerationButton.setOnMouseClicked(e -> gameManager.previousGeneration());
        playButton.setOnMouseClicked(uiHandler::handlePauseButtonClick);

        drawButton.setOnMouseClicked(e -> gameManager.changeState(UserActionState.DRAWING));
        selectButton.setOnMouseClicked(e -> gameManager.changeState(UserActionState.SELECTING));
        moveButton.setOnMouseClicked(e -> gameManager.changeState(UserActionState.MOVING));

        pasteAnd.setOnMouseClicked(e -> gameManager.changePasteMode(PastingType.AND));
        pasteCpy.setOnMouseClicked(e -> gameManager.changePasteMode(PastingType.CPY));
        pasteOr.setOnMouseClicked(e -> gameManager.changePasteMode(PastingType.OR));
        pasteXor.setOnMouseClicked(e -> gameManager.changePasteMode(PastingType.XOR));

        populationChart.setOnMouseClicked(e -> gameManager.toggleChart());

        showBorderButton.setOnMouseClicked(e -> gameManager.toggleShowBorders());

        ObservableList<String> themeNames = FXCollections.observableArrayList(GameManager.themeManager.getThemes().stream().map(t -> t.THEME_NAME).toList());
        themes.setItems(themeNames);
        if(themeNames.size() > 0) themes.setValue(themeNames.get(0));
        themes.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> uiHandler.handleThemeChange(number2, themes));

        newPattern.setOnMouseClicked(e -> gameManager.clearBoard());
        savePattern.setOnMouseClicked(e -> savePattern());
        openPattern.setOnMouseClicked(e -> openPattern());
        takePatternPicture.setOnMouseClicked(e -> screenCanvas());

        ObservableList<String> patterns = FXCollections.observableArrayList(GameManager.patternManager.getPatterns().stream().map(Pattern::getName).toList());
        patternList.getItems().addAll(patterns);
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> uiHandler.handleSearchTextChange(newValue, patternList, patterns));
        clearSearchBar.setOnMouseClicked(e -> {
            searchBar.setText("");
            uiHandler.handleSearchTextChange("", patternList, patterns);
        });
        patternList.setOnMouseClicked(e -> choosePattern());

        int percentage = (int) ((gameManager.getGameSpeed() * 100) / gameSpeed.getMax());
        String style = String.format("-track-color: linear-gradient(to right, #0096c9 %d%%, rgb(80,80,80) %d%%);", percentage, percentage);
        gameSpeed.setStyle(style);
        gameSpeed.valueProperty().addListener((ov, old_val, new_val) -> uiHandler.handleGameSpeedChange(new_val, gameSpeedLabel, gameSpeed));

        int randomPercentage = (int) ((randomProbability.getValue() * 100) / randomProbability.getMax());
        String styleProbability = String.format("-track-color: linear-gradient(to right, #0096c9 %d%%, rgb(80,80,80) %d%%);", randomPercentage, randomPercentage);
        randomProbability.setStyle(styleProbability);
        randomProbability.valueProperty().addListener((ov, old_val, new_val) -> uiHandler.handleRandomProbabilityChange(new_val, randomProbabilityLabel, randomProbability));

        moveSelectionLeft.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.LEFT));
        moveSelectionRight.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.RIGHT));
        moveSelectionUp.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.UP));
        moveSelectionDown.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.DOWN));
        inverseSelectedCells.setOnMouseClicked(e -> iGridService.inverseSelectedCells());
        rotateSelectionRight.setOnMouseClicked(e -> iGridService.rotateSelectedCells(MoveType.RIGHT));
        rotateSelectionLeft.setOnMouseClicked(e -> iGridService.rotateSelectedCells(MoveType.LEFT));
        cancelSelection.setOnMouseClicked(e -> iGridService.cancelSelection());

        pauseOnDraw.setOnMouseClicked(e -> uiHandler.handleOnPauseOnDrawButtonClick(e, pauseOnDraw));

        randomize.setOnMouseClicked(e -> iGridService.randomize(randomProbability.getValue()));
    }

    private void savePattern() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Pattern");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE", "*.rle"));
        File saveFile = fileChooser.showSaveDialog(Stage.getWindows().get(0));
        String rleString = RLE.encode(getGridService().getContent(getGrid()), "B3/S23");
        if (saveFile != null) {
            saveTextToFile(rleString, saveFile);
        }
    }

    private void openPattern() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Pattern");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE", "*.rle"));
        File selectedFile = fileChooser.showOpenDialog(Stage.getWindows().get(0));

        if (selectedFile != null) {
            try {

                Cell[][] pattern = RLE.decode(RLE.read(selectedFile.getAbsolutePath()));
                getGridService().clearGrid();
                getGridService().setPattern(pattern);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Selected file: " );
        }
    }

    private void choosePattern() {
        Pattern pattern = GameManager.patternManager.getByName(patternList.getSelectionModel().getSelectedItems().get(0));
        getGridService().clearGrid();
        getGridService().setPattern(pattern);

    }

    private void screenCanvas() {
        WritableImage snapshot = canvas.snapshot(null, null);

        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            File file = fileChooser.showSaveDialog(Stage.getWindows().get(0));
            if (file != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                System.out.println("Screenshot saved to file: " + file.getAbsolutePath());
            }
        } catch (IOException ex) {
            System.out.println("Error saving screenshot to file: " + ex.getMessage());
        }
    }

    private void saveTextToFile(String content, File file) {
        try(PrintWriter writer = new PrintWriter(file)) {
            writer.println(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    private void updateScale() {
        canvasContainer.setScaleX(scaleValue);
        canvasContainer.setScaleY(scaleValue);
    }

    public Grid getGrid() {
        return grid;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public IGridService getGridService() {
        return iGridService;
    }

    public LifeRenderer getLifeRenderer() {
        return lifeRenderer;
    }
}