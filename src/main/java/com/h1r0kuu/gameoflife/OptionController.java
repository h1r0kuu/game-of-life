package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.theme.Theme;
import com.h1r0kuu.gameoflife.theme.ThemeManager;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class OptionController {
    private Grid grid;
    private final int FPS_SET = 180;
    private int gameSpeedNum = 10;
    private boolean isPaused = false;
    private boolean pausedByButton = false;
    private AnimationTimer animationTimer;
    private OptionState optionState = OptionState.DRAWING;
    private double mouseX, mouseY;
    private int generation = 0;
    private double canvasX, canvasY;
    private Scale scaleTransform;
    private double zoom = 1.0;
    private double startX, startY, endX, endY;
    private static final Color DEFAULT_BG_COLOR = Color.web("#151310");
    private static final ThemeManager themeManager = new ThemeManager();
    public static Theme getCurrentTheme() { return themeManager.getCurrentTheme(); }

    @FXML
    private Pane anchorPane;
    @FXML
    private Pane canvasWrapper;
    @FXML
    private Canvas canvas;
    @FXML
    private Button playToggle;
    @FXML
    private ImageView playImage;
    @FXML
    private Slider gameSpeed;
    @FXML
    private Slider gameZoom;
    @FXML
    private Button clearButton;
    @FXML
    private Button nextGenerationButton;
    @FXML
    private Button drawState;
    @FXML
    private Button moveState;
    @FXML
    private Button selectState;
    @FXML
    private Button showBorderButton;
    @FXML
    private Label generationCounter;
    @FXML
    private Label gameSpeedLabel;
    @FXML
    private Label cellInfo;

    @FXML
    private ChoiceBox<String> themes;
    private double lastX;
    private double lastY;

    @FXML
    public void initialize() {
        setupGrid();
        setupThemes();


        canvasWrapper.setOnScroll(event -> {
            double zoomFactor = 1.1;
            double deltaY = event.getDeltaY();

            double mouseX = event.getX();
            double mouseY = event.getY();

            if (deltaY < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            canvas.setScaleX(canvas.getScaleX() * zoomFactor);
            canvas.setScaleY(canvas.getScaleY() * zoomFactor);

            double canvasMouseX = canvas.sceneToLocal(new Point2D(mouseX, mouseY)).getX();
            double canvasMouseY = canvas.sceneToLocal(new Point2D(mouseX, mouseY)).getY();
            double canvasDeltaX = canvasMouseX - canvas.getBoundsInLocal().getWidth() / 2;
            double canvasDeltaY = canvasMouseY - canvas.getBoundsInLocal().getHeight() / 2;
            canvas.setTranslateX(canvas.getTranslateX() - canvasDeltaX * (zoomFactor - 1));
            canvas.setTranslateY(canvas.getTranslateY() - canvasDeltaY * (zoomFactor - 1));

            event.consume();
        });

        setupCanvasMouseHandlers();
        setupPaneMouseHandlers();

        drawState.setOnMouseClicked(e -> {
            setOptionState(OptionState.DRAWING);
        });
        moveState.setOnMouseClicked(e -> {
            setOptionState(OptionState.MOVING);
        });
        selectState.setOnMouseClicked(e -> {
            setOptionState(OptionState.SELECTING);
        });

        startGameLoop();
    }

    private void setupThemes() {
        ObservableList<String> themeNames = FXCollections.observableArrayList(themeManager.getThemes().stream().map(t -> t.THEME_NAME).toList());
        themes.setItems(themeNames);
        if(themeNames.size() > 0) themes.setValue(themeNames.get(0));

        themes.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            String newTheme = themes.getItems().get((Integer) number2);
            themeManager.changeTheme(newTheme);
        });
    }

    private void setupPaneMouseHandlers() {
        canvasWrapper.setOnMousePressed(event -> {
            lastX = event.getSceneX();
            lastY = event.getSceneY();
        });

        canvasWrapper.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - lastX;
            double deltaY = event.getSceneY() - lastY;
            double newX = canvasWrapper.getLayoutX() + deltaX;
            double newY = canvasWrapper.getLayoutY() + deltaY;
            if (newX > 0) {
                newX = 0;
            } else if (newX < -canvasWrapper.getWidth() + canvas.getWidth()) {
                newX = -canvasWrapper.getWidth() + canvas.getWidth();
            }
            if (newY > 0) {
                newY = 0;
            } else if (newY < -canvasWrapper.getHeight() + canvas.getHeight()) {
                newY = -canvasWrapper.getHeight() + canvas.getHeight();
            }
            canvasWrapper.setLayoutX(newX);
            canvasWrapper.setLayoutY(newY);
            lastX = event.getSceneX();
            lastY = event.getSceneY();

        });
    }

    private void setupCanvasMouseHandlers() {
        canvas.setOnMouseMoved(e -> {

            mouseX = e.getSceneX();
            mouseY = e.getSceneY();

            canvasX = canvas.getLayoutX();
            canvasY = canvas.getLayoutY();

            startX = e.getX();
            startY = e.getY();

            int gridx = (int) (startX / (Cell.CELL_SIZE * 1.0));
            int gridy = (int) (startY / (Cell.CELL_SIZE * 1.0));
            grid.hoverGrid(gridx, gridy, cellInfo);
        });
        
        canvas.setOnMousePressed(event -> {
            endX = event.getX();
            endY = event.getY();

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
                    grid.selectRange(startX, startY, endX, endY);
                }


                this.startX = e.getX();
                this.startY = e.getY();
            }
        });

        canvas.setOnMouseReleased(e -> {
            if(optionState.equals(OptionState.DRAWING)) {
                if(!pausedByButton) setPaused(false);
            }
        });
    }

    private void setOptionState(OptionState optionState) {
        this.optionState = optionState;

        drawState.setStyle("");
        moveState.setStyle("");
        selectState.setStyle("");

        switch (optionState) {
            case DRAWING -> drawState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
            case MOVING -> moveState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
            case SELECTING -> selectState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
        }
    }

    public void startGameLoop() {
        animationTimer = new AnimationTimer() {
            final double timePerFrame = 1_000_000_000.0 / FPS_SET;
            long lastFrame = System.nanoTime();
            long now;

            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            long lastUpdate = System.currentTimeMillis();

            @Override
            public void handle(long l) {
                long updateInterval = (long) (1000.0 / gameSpeedNum );

                now = System.nanoTime();
                if(now - lastFrame >= timePerFrame) {
                    grid.update();
                    lastFrame = now;
                    frames++;
                }

                if(System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
                    System.out.println("FPS: " + frames);
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

    public void setPaused(boolean pause) {
        isPaused = pause;
        if(pause) {
            playImage.setImage(new Image(getClass().getResource("icons/pause.png").toExternalForm()));
        } else {
            animationTimer.start();
            playImage.setImage(new Image(getClass().getResource("icons/play.png").toExternalForm()));
        }
    }

    public void togglePlay(MouseEvent mouseEvent) {
        setPaused(!isPaused);
        pausedByButton = isPaused;
    }

    public void nextGen() {
        grid.nextGeneration();
        generationCounter.setText("Generation: " + generation++);
    }

    public void nextGeneration(MouseEvent mouseEvent) {
        nextGen();
        grid.update();
    }

    public void clearGrid(MouseEvent mouseEvent) {
        grid.clearGrid();
        grid.update();
    }

    public void changeGameSpeed(MouseEvent mouseEvent) {
        gameSpeedNum = (int)gameSpeed.getValue();
        gameSpeedLabel.setText(String.format("%d gps", gameSpeedNum));
    }


    public void changeGameScale(MouseEvent mouseEvent) {
        double zoom = gameZoom.getValue() / 100;
        canvas.setScaleX(zoom);
        canvas.setScaleY(zoom);
    }

    public void toggleGrid(MouseEvent mouseEvent) {
        grid.setShowBorders(!grid.isShowBorders());
        if(grid.isShowBorders()) {
            showBorderButton.setStyle("-fx-background-color: #68ff00");
        } else {
            showBorderButton.setStyle(null);
        }
    }
}