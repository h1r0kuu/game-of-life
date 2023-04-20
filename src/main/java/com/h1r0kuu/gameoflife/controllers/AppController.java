package com.h1r0kuu.gameoflife.controllers;

import com.h1r0kuu.gameoflife.enums.PastingType;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
    @FXML private Button autoFitPattern;
    @FXML private Button fitPattern;
    @FXML private Button drawButton;
    @FXML private Pane drawButtonGroup;
    @FXML private Button selectButton;
    @FXML private Pane selectButtonGroup;
    @FXML private Button moveButton;
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
    @FXML private Button pauseOnDraw;
    @FXML private Canvas canvas;
    @FXML private Rectangle selectRectangle;
    @FXML private Rectangle pasteRectangle;
    @FXML private TextField searchBar;
    @FXML private ListView<String> patternList;
    @FXML private ComboBox<String> themes;
    @FXML private Slider gameSpeed;
    @FXML private Label gameSpeedLabel;
    @FXML private ScrollPane scrollPane;
    @FXML private StackPane canvasContainer;
    @FXML private Group zoomNode;
    @FXML private Label cellInfo;
    @FXML private VBox box;
    @FXML private VBox canvasOuter;

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
        SelectionRectangle rectangleForSelect = new SelectionRectangle(selectRectangle, pasteRectangle, canvasContainer);
        this.grid = new Grid(rows, cols, rectangleForSelect);
        this.lifeRenderer = new LifeRenderer(grid, gc);
        this.iGridService = new GridServiceImpl(grid);
        this.gameManager = new GameManager(grid, iGridService, canvas);
        this.uiHandler = new UiHandler(gameManager);

        grid.init();
        Pattern pattern = GameManager.patternManager.getByName("Copperhead");
        Cell[][] cells = RLE.decode(pattern.getRleString());
        iGridService.printCellsAsString(cells);
        iGridService.setPattern(cells);

        gameManager.setButtons(playImage, drawButton,selectButton,moveButton);
        gameManager.setGroups(selectButtonGroup, drawButtonGroup);
        gameManager.setPasteModeButtons(pasteAnd, pasteCpy, pasteOr, pasteXor);

        int percentage = (int) ((gameManager.getGameSpeed() * 100) / gameSpeed.getMax());
        String style = String.format("-track-color: linear-gradient(to right, #0096c9 %d%%, rgb(80,80,80) %d%%);", percentage, percentage);
        gameSpeed.setStyle(style);

        canvasOuter.setOnScroll(e-> {
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });

        updateScale();
        initCanvasHandlers();
        initButtonClicks();
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
        patternList.setOnMouseClicked(e -> choosePattern());

        gameSpeed.valueProperty().addListener((ov, old_val, new_val) -> uiHandler.handleGameSpeedChange(new_val, gameSpeedLabel, gameSpeed));

        moveSelectionLeft.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.LEFT));
        moveSelectionRight.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.RIGHT));
        moveSelectionUp.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.UP));
        moveSelectionDown.setOnMouseClicked(e -> iGridService.moveSelectedCells(MoveType.DOWN));
        inverseSelectedCells.setOnMouseClicked(e -> iGridService.inverseSelectedCells());
        rotateSelectionRight.setOnMouseClicked(e -> iGridService.rotateSelectedCells(MoveType.RIGHT));
        rotateSelectionLeft.setOnMouseClicked(e -> iGridService.rotateSelectedCells(MoveType.LEFT));

        pauseOnDraw.setOnMouseClicked(e -> uiHandler.handleOnPauseOnDrawButtonClick(e, pauseOnDraw));
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

        File file = new File("canvas-screenshot.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            System.out.println("Screenshot saved to file: " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Error saving screenshot to file: " + ex.getMessage());
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    private void updateScale() {
        canvasContainer.setScaleX(scaleValue);
        canvasContainer.setScaleY(scaleValue);
    }
    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * Constants.ZOOM_INTENSITY);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = scrollPane.getViewportBounds();

        double valX = scrollPane.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = scrollPane.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        if (scaleValue < Constants.MIN_SCALE || scaleValue > Constants.MAX_SCALE) {
            return;
        }

        updateScale();
        this.layout();

        Point2D posInZoomTarget = canvasContainer.parentToLocal(zoomNode.parentToLocal(mousePoint));

        Point2D adjustment = canvasContainer.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        scrollPane.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        scrollPane.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
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
