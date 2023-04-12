package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.theme.Theme;
import com.h1r0kuu.gameoflife.theme.ThemeManager;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Stack;

public class OptionController {

    private Grid grid;
    private final int FPS = 180;
    private int gameSpeed = 10;
    private boolean isPaused = false;
    private boolean pausedByButton = false;
    private AnimationTimer animationTimer;
    private OptionState optionState = OptionState.DRAWING;
    private double mouseX, mouseY;
    private int generation = 0;
    private double canvasX, canvasY, lastX, lastY;
    private double startX, startY, endX, endY, selectX, selectY;
    private static final ThemeManager themeManager = new ThemeManager();
    public static Theme getCurrentTheme() { return themeManager.getCurrentTheme(); }
    private static final Color IDLE_BUTTON_COLOR = Color.rgb(0, 0, 0, 0.77);
    private static final Color HOVERED_BUTTON_COLOR = Color.web("#FF0000");

    @FXML
    private VBox canvasWrapper;

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ImageView playImage;

    @FXML
    private Slider gameSpeedSlider;

    @FXML
    private Slider gameZoomSlider;

    @FXML
    private Group clearButton;

    @FXML
    private Group nextGenerationButton;

    @FXML
    private Group drawButton;

    @FXML
    private Group moveButton;

    @FXML
    private Group selectButton;

    @FXML
    private Group showBorderButton;

    @FXML
    private Label generationCounterLabel;

    @FXML
    private Label gameSpeedLabel;

    @FXML
    private Label cellInfoLabel;

    @FXML
    private Group fpsCounterGroup;

    private Label fpsCounterLabel;

    @FXML
    private ChoiceBox<String> themeChoiceBox;

    private Stack<Cell[][]> gameBoardHistory = new Stack<>();



    @FXML
    public void initialize() {
        setupGrid();
        setupThemes();
        getRectangle(drawButton).setFill(HOVERED_BUTTON_COLOR);
        this.fpsCounterLabel = (Label) fpsCounterGroup.getChildren().get(1);
        borderPane.setPickOnBounds(false);
        canvasWrapper.setOnScroll(event -> {
            double currentScale = canvas.getScaleX();
            double mouseX = event.getX();
            double mouseY = event.getY();

            double zoomFactor = 1.2;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                currentScale /= zoomFactor;
            } else {
                currentScale *= zoomFactor;
            }

            double newScale = Math.max(0.1, Math.min(currentScale, 10.0));
            double pivotX = (mouseX - canvas.getTranslateX()) / canvas.getWidth();
            double pivotY = (mouseY - canvas.getTranslateY()) / canvas.getHeight();

            double deltaX = (1 - newScale / currentScale) * pivotX;
            double deltaY1 = (1 - newScale / currentScale) * pivotY;

            canvas.setScaleX(newScale);
            canvas.setScaleY(newScale);

            canvas.setTranslateX(canvas.getTranslateX() + deltaX);
            canvas.setTranslateY(canvas.getTranslateY() + deltaY1);
        });


        setupCanvasMouseHandlers();
        setupPaneMouseHandlers();
        setupHotkeys();

        drawButton.setOnMouseClicked(e -> {
            setOptionState(OptionState.DRAWING);
        });
        moveButton.setOnMouseClicked(e -> {
            setOptionState(OptionState.MOVING);
        });
        selectButton.setOnMouseClicked(e -> {
            setOptionState(OptionState.SELECTING);
        });

        startGameLoop();
    }

    private void setupGrid() {
        int rows = (int) Math.floor(canvas.getWidth() / Cell.CELL_SIZE);
        int cols = (int) Math.floor(canvas.getHeight() / Cell.CELL_SIZE);

        GraphicsContext graphics = canvas.getGraphicsContext2D();
        this.grid = new Grid(rows, cols, graphics);
        grid.init();
        grid.randomize(25);
        grid.update();

        canvas.setScaleX(1.0);
        canvas.setScaleY(1.0);
    }

    private void setupHotkeys() {
        canvas.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.C) {
                ClipboardContent content = new ClipboardContent();
                content.putImage(canvas.snapshot(null, null));
                Clipboard.getSystemClipboard().setContent(content);
                e.consume();
            } else if(e.getCode() == KeyCode.X) {
                toggleGrid();
            } else if(e.getCode() == KeyCode.PAUSE || e.getCode() == KeyCode.HOME) {
                setPaused(!isPaused);
            } else if(e.getCode() == KeyCode.A) {
                previousGeneration();
            } else if(e.getCode() == KeyCode.D) {
                nextGen();
            } else if(e.getCode() == KeyCode.C) {
                themeManager.nextTheme();
            } else if(e.getCode() == KeyCode.R) {
                clearGrid();
            } else if(e.getCode() == KeyCode.F) {
                fpsCounterGroup.setVisible(!fpsCounterGroup.isVisible());
            }
        });
    }

    private void setupThemes() {
        ObservableList<String> themeNames = FXCollections.observableArrayList(themeManager.getThemes().stream().map(t -> t.THEME_NAME).toList());
        themeChoiceBox.setItems(themeNames);
        if(themeNames.size() > 0) themeChoiceBox.setValue(themeNames.get(0));

        themeChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            String newTheme = themeChoiceBox.getItems().get((Integer) number2);
            themeManager.changeTheme(newTheme);
        });
    }

    private void setupPaneMouseHandlers() {
        canvasWrapper.setOnMousePressed(event -> {
            lastX = event.getSceneX();
            lastY = event.getSceneY();

        });

        canvasWrapper.setOnMouseDragged(event -> {
            if(optionState.equals(OptionState.MOVING)) {
                double deltaX = event.getSceneX() - lastX;
                double deltaY = event.getSceneY() - lastY;

                canvas.setTranslateX(canvas.getTranslateX() + deltaX);
                canvas.setTranslateY(canvas.getTranslateY() + deltaY);

                lastX = event.getSceneX();
                lastY = event.getSceneY();
            }
        });
    }

    private void setupCanvasMouseHandlers() {
        canvas.setOnMouseMoved(e -> {
            canvas.requestFocus();

            mouseX = e.getX();
            mouseY = e.getY();

            canvasX = canvas.getLayoutX();
            canvasY = canvas.getLayoutY();

            startX = e.getX();
            startY = e.getY();

            int gridx = (int) (startX / (Cell.CELL_SIZE * 1.0));
            int gridy = (int) (startY / (Cell.CELL_SIZE * 1.0));
            grid.hoverGrid(gridx, gridy, cellInfoLabel);
        });
        
        canvas.setOnMousePressed(event -> {
            endX = event.getX();
            endY = event.getY();
            selectX = event.getX();
            selectY = event.getY();
            if(optionState.equals(OptionState.DRAWING)) {
                setPaused(true);
                int gridx = (int) (startX / (Cell.CELL_SIZE * 1.0));
                int gridy = (int) (startY / (Cell.CELL_SIZE * 1.0));

                if(event.isPrimaryButtonDown()) {
                    grid.reviveCell(gridx, gridy);
                } else {
                    grid.killCell(gridx, gridy);
                }
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.getX() >= 0 && e.getY() >= 0 && e.getX() < canvas.getWidth() && e.getY() < canvas.getHeight()) {
                int startX = (int) Math.floor(this.startX / Cell.CELL_SIZE);
                int startY = (int) Math.floor(this.startY / Cell.CELL_SIZE);
                int endX = (int) Math.floor(e.getX() / Cell.CELL_SIZE);
                int endY = (int) Math.floor(e.getY() / Cell.CELL_SIZE);

                // Interpolate cells using Bresenham's line algorithm
                int dx = Math.abs(endX - startX);
                int dy = Math.abs(endY - startY);
                int sx = startX < endX ? 1 : -1;
                int sy = startY < endY ? 1 : -1;
                int err = dx - dy;

                if (optionState.equals(OptionState.DRAWING)) {
                    if (startX >= 0 && startX < grid.getRows() && startY >= 0 && startY < grid.getCols()) {
                        while (true) {

                            if (e.isPrimaryButtonDown()) {
                                grid.reviveCell(startX, startY);
                            } else {
                                grid.killCell(startX, startY);
                            }
                            if (startX == endX && startY == endY) {
                                break;
                            }
                            int e2 = 2 * err;
                            if (e2 > -dy) {
                                err -= dy;
                                startX += sx;
                            }
                            if (e2 < dx) {
                                err += dx;
                                startY += sy;
                            }
                        }
                    }
                } else if (optionState.equals(OptionState.SELECTING)) {

                    grid.selectRange(selectX, selectY, e.getX(), e.getY());
                }

                this.startX = e.getX();
                this.startY = e.getY();
            }
        });

        canvas.setOnMouseReleased(e -> {
            if(optionState.equals(OptionState.DRAWING)) {
                if(!pausedByButton) setPaused(false);
            } else if(optionState.equals(OptionState.SELECTING)) {

            }
        });
    }

    private void setOptionState(OptionState optionState) {
        this.optionState = optionState;

        Rectangle drawRect = getRectangle(drawButton);
        Rectangle moveRect = getRectangle(moveButton);
        Rectangle selectRect = getRectangle(selectButton);

        drawRect.setFill(IDLE_BUTTON_COLOR);
        moveRect.setFill(IDLE_BUTTON_COLOR);
        selectRect.setFill(IDLE_BUTTON_COLOR);

        switch (optionState) {
            case DRAWING -> drawRect.setFill(HOVERED_BUTTON_COLOR);
            case MOVING -> moveRect.setFill(HOVERED_BUTTON_COLOR);
            case SELECTING -> selectRect.setFill(HOVERED_BUTTON_COLOR);
        }
    }

    public void startGameLoop() {
        animationTimer = new AnimationTimer() {
            final double timePerFrame = 1_000_000_000.0 / FPS;
            long lastFrame = System.nanoTime();
            long now;

            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            long lastUpdate = System.currentTimeMillis();

            @Override
            public void handle(long l) {
                long updateInterval = (long) (1000.0 / gameSpeed );

                now = System.nanoTime();
                if(now - lastFrame >= timePerFrame) {
                    grid.update();
                    lastFrame = now;
                    frames++;
                }

                if(System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
                    fpsCounterLabel.setText(frames + " FPS");
                    double percentage = (frames - 1) / ((FPS / 2.0) - 1);
                    int rValue = (int) ((1 - percentage) * 255);
                    int gValue = (int) (percentage * 255);
                    fpsCounterLabel.setTextFill(Color.rgb(rValue, gValue, 0));
                    frames = 0;
                }

                if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
                    lastUpdate = System.currentTimeMillis();
                    if (!isPaused) {
                        nextGen();
                    }
                }
            }
        };
        animationTimer.start();
    }

    public void setPaused(boolean pause) {
        isPaused = pause;
        if(pause) {
            playImage.setImage(new Image(getClass().getResource("icons/pause.png").toExternalForm()));
        } else {
            animationTimer.start();
            playImage.setImage(new Image(getClass().getResource("icons/play.png").toExternalForm()));
        }
    }

    @FXML
    public void togglePlay() {
        setPaused(!isPaused);
        pausedByButton = isPaused;
    }

    public void setGenerationCounter(int newValue) {
        generationCounterLabel.setText("Generation: " + newValue);
    }

    @FXML
    public void nextGen() {
        gameBoardHistory.push(grid.getCells());
        grid.nextGeneration();
        setGenerationCounter(++generation);
    }

    @FXML
    public void previousGeneration() {
        if (!gameBoardHistory.isEmpty()) {
            grid.setCells(gameBoardHistory.pop());
            setGenerationCounter(--generation);
        }
    }

    @FXML
    public void clearGrid() {
        grid.clearGrid();
        setGenerationCounter(0);
    }

    @FXML
    public void changeGameSpeed() {
        gameSpeed = (int)gameSpeedSlider.getValue();
        gameSpeedLabel.setText(String.format("%d gps", gameSpeed));
    }


    public void changeGameScale(MouseEvent mouseEvent) {
        double zoom = gameZoomSlider.getValue() / 100;
        canvas.setScaleX(zoom);
        canvas.setScaleY(zoom);
    }

    public void toggleGrid() {
        grid.setShowBorders(!grid.isShowBorders());
        if(grid.isShowBorders()) {
            showBorderButton.setStyle("-fx-background-color: #68ff00");
        } else {
            showBorderButton.setStyle(null);
        }
    }

    public Rectangle getRectangle(Group group) {
        return (Rectangle) group.getChildren().get(0);
    }

    @FXML
    public void groupHover(MouseEvent event) {
        Group group = (Group) event.getSource();
        getRectangle(group).setFill(HOVERED_BUTTON_COLOR);
    }

    @FXML
    public void groupHoverExit(MouseEvent event) {
        Group group = (Group) event.getSource();
        getRectangle(group).setFill(IDLE_BUTTON_COLOR);
    }
}