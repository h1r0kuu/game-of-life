package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import com.h1r0kuu.gameoflife.theme.Theme;
import com.h1r0kuu.gameoflife.theme.ThemeManager;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane anchorPane;
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
    private ChoiceBox<String> themes;

    @FXML
    public void initialize() {
        setupGrid();
        setupThemes();

        anchorPane.setStyle("-fx-background-color: " + DEFAULT_BG_COLOR);
        scaleTransform = new Scale(1, 1);
        canvas.getTransforms().add(scaleTransform);
        canvas.setOnScroll(event -> {
            double zoomFactor = 1.5;
            if (event.getDeltaY() <= 0) {
                zoomFactor = 1 / zoomFactor;
            }
            double oldScale = canvas.getScaleX();
            double scale = oldScale * zoomFactor;
            double f = (scale / oldScale) - 1;

            Bounds bounds = canvas.localToScene(canvas.getBoundsInLocal());
            double dx = (event.getX() - (bounds.getWidth() / 2 + bounds.getMinX()));
            double dy = (event.getY() - (bounds.getHeight() / 2 + bounds.getMinY()));
            canvas.setTranslateX(canvas.getTranslateX() - f * dx);
            canvas.setTranslateY(canvas.getTranslateY() - f * dy);
            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
        });
        setupCanvasMouseHandlers();

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

    private void setupCanvasMouseHandlers() {
        canvas.setOnMouseMoved(e -> {
            int gridx = (int) (e.getX() / (Cell.CELL_SIZE * 1.0));
            int gridy = (int) (e.getY() / (Cell.CELL_SIZE * 1.0));
            grid.hoverGrid(gridx, gridy);
        });
        canvas.setOnMousePressed(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            canvasX = canvas.getLayoutX();
            canvasY = canvas.getLayoutY();

            startX = event.getX();
            startY = event.getY();

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
            } else if(optionState.equals(OptionState.MOVING)) {

                mouseX = event.getX();
                mouseY = event.getY();
            }
        });

        canvas.setOnMouseDragged(e -> {
            if(optionState.equals(OptionState.DRAWING)) {
                int gridx = (int) (e.getX() / (Cell.CELL_SIZE * 1.0));
                int gridy = (int) (e.getY() / (Cell.CELL_SIZE * 1.0));

                grid.hoverGrid(gridx, gridy);
                if(e.isPrimaryButtonDown()) {
                    grid.reviveCell(gridx, gridy);
                } else {
                    grid.killCell(gridx, gridy);
                }
            } else if(optionState.equals(OptionState.MOVING)) {
                double deltaX = e.getSceneX() - mouseX;
                double deltaY = e.getSceneY() - mouseY;
                canvas.setLayoutX(canvasX + deltaX);
                canvas.setLayoutY(canvasY + deltaY);

            } else if(optionState.equals(OptionState.SELECTING)) {
                endX = e.getX();
                endY = e.getY();
                grid.selectRange(startX, startY, endX, endY);
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
            case DRAWING:
                drawState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
                break;
            case MOVING:
                moveState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
                break;
            case SELECTING:
                selectState.setStyle("-fx-background-color: #68ff00; -fx-text-fill: white");
                break;
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
        gameSpeedLabel.setText(String.format("%d gen/s", gameSpeedNum));
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