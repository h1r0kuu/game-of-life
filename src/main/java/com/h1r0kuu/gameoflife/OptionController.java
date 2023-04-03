package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.entity.Cell;
import com.h1r0kuu.gameoflife.entity.Grid;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;

public class OptionController {
    private Grid grid;

    private final int FPS_SET = 180;
    private int gameSpeedNum = 10;
    private double scale = 1.0;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Button playToggle;
    @FXML private ImageView playImage;
    @FXML private Slider gameSpeed;
    @FXML private Slider gameZoom;
    @FXML private Button clearButton;
    @FXML private Button nextGenerationButton;
    @FXML private Button drawState;
    @FXML private Button moveState;
    @FXML private Button selectState;
    @FXML private Label generationCounter;

    private boolean isPaused = false;
    private AnimationTimer animationTimer;

    private OptionState optionState = OptionState.DRAWING;

    private double mouseX, mouseY;

    private int generation = 0;
    private double canvasTranslateX = 0;
    private double canvasTranslateY = 0;
    private double canvasScale = 1;
    @FXML
    public void initialize() {
        setupGrid();
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        Scale zoom = new Scale(1, 1);

        canvas.setOnScroll(event -> {
//            double mouseX = event.getX();
//            double mouseY = event.getY();
//
//            double currScaleX = canvas.getScaleX();
//            double currScaleY = canvas.getScaleY();
//
//            double newScaleX = Math.max(currScaleX * zoomFactor, 0.1);
//            double newScaleY = Math.max(currScaleY * zoomFactor, 0.1);
//
//            canvas.setTranslateX(canvas.getTranslateX() + mouseX);
//            canvas.setTranslateY(canvas.getTranslateY() + mouseY);
//
//            canvas.setScaleX(newScaleX);
//            canvas.setScaleY(newScaleY);
//
//            canvas.setTranslateX(canvas.getTranslateX() - mouseX);
//            canvas.setTranslateY(canvas.getTranslateY() - mouseY);
//
//            event.consume();
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            canvasScale *= zoomFactor;

            double oldMouseX = canvasTranslateX + event.getX();
            double oldMouseY = canvasTranslateY + event.getY();

            canvas.setTranslateX(canvasTranslateX);
            canvas.setTranslateY(canvasTranslateY);
            canvas.setScaleX(canvasScale);
            canvas.setScaleY(canvasScale);

            double newMouseX = canvasTranslateX + event.getX();
            double newMouseY = canvasTranslateY + event.getY();

            canvasTranslateX += oldMouseX - newMouseX;
            canvasTranslateY += oldMouseY - newMouseY;

            event.consume();
        });

        canvas.setOnMouseMoved(e -> {
            int gridx = (int) (e.getX() / (Cell.CELL_SIZE * 1.0));
            int gridy = (int) (e.getY() / (Cell.CELL_SIZE * 1.0));
            grid.hoverGrid(gridx, gridy);
        });

        canvas.setOnMouseClicked(e -> {
            if(optionState.equals(OptionState.DRAWING)) {
                int gridx = (int) (e.getX() / (Cell.CELL_SIZE * 1.0));
                int gridy = (int) (e.getY() / (Cell.CELL_SIZE * 1.0));
                if(e.isPrimaryButtonDown()) {
                    grid.getCell(gridx, gridy).setAlive(true);
                    fillCell(graphics, Grid.BORDER_COLOR, Cell.ALIVE_CELL_COLOR, gridx, gridy);
                } else {
                    grid.getCell(gridx, gridy).setAlive(false);
                    fillCell(graphics, Grid.BORDER_COLOR, Cell.DEAD_CELL_COLOR, gridx, gridy);
                }
            } else if(optionState.equals(OptionState.MOVING)) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        canvas.setOnMouseDragged(e -> {
            if(optionState.equals(OptionState.DRAWING)) {
                int gridx = (int) (e.getX() / (Cell.CELL_SIZE * 1.0));
                int gridy = (int) (e.getY() / (Cell.CELL_SIZE * 1.0));
                grid.hoverGrid(gridx, gridy);

                if(e.isPrimaryButtonDown()) {
                    grid.getCell(gridx, gridy).setAlive(true);
                    fillCell(graphics, Grid.BORDER_COLOR, Cell.ALIVE_CELL_COLOR, gridx, gridy);
                } else {
                    grid.getCell(gridx, gridy).setAlive(false);
                    fillCell(graphics, Grid.BORDER_COLOR, Cell.DEAD_CELL_COLOR, gridx, gridy);
                }
            } else if(optionState.equals(OptionState.MOVING)) {
                double deltaX = e.getX() - mouseX;
                double deltaY = e.getY() - mouseY;
                canvas.setTranslateX(canvas.getTranslateX() + deltaX);
                canvas.setTranslateY(canvas.getTranslateY() + deltaY);
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        drawState.setOnMouseClicked(e -> {
            moveState.setStyle(null);
            selectState.setStyle(null);
            optionState = OptionState.DRAWING;
            drawState.setStyle("-fx-background-color: #68ff00");
        });
        moveState.setOnMouseClicked(e -> {
            selectState.setStyle(null);
            drawState.setStyle(null);
            optionState = OptionState.MOVING;
            moveState.setStyle("-fx-background-color: #68ff00");
        });
        selectState.setOnMouseClicked(e -> {
            moveState.setStyle(null);
            drawState.setStyle(null);
            optionState = OptionState.SELECTING;
            selectState.setStyle("-fx-background-color: #68ff00");
        });

        animationTimer = new AnimationTimer() {
            final double timePerFrame = 1_000_000_000.0 / FPS_SET;
            long lastFrame = System.nanoTime();
            long now;

            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            long lastUpdate = System.currentTimeMillis();

            @Override
            public void handle(long l) {
                long updateInterval = (long) (1000.0 / (gameSpeedNum / 10) );

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

    public static void fillCell(GraphicsContext graphics, Color borderColor, Color cellColor, int gridx, int gridy) {
        graphics.setFill(borderColor);
        graphics.fillRect(gridx * Cell.CELL_SIZE, gridy * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
        graphics.setFill(cellColor);
        graphics.fillRect((gridx * Cell.CELL_SIZE) + 1, (gridy * Cell.CELL_SIZE) + 1, Cell.CELL_SIZE - 2, Cell.CELL_SIZE - 2);
    }

    public void togglePlay(MouseEvent mouseEvent) {
        if(isPaused) {
            isPaused = false;
            animationTimer.start();
            playImage.setImage(new Image(getClass().getResource("icons/play.png").toExternalForm()));
        } else {
            isPaused = true;
            playImage.setImage(new Image(getClass().getResource("icons/pause.png").toExternalForm()));
        }
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
    }

    public void changeGameScale(MouseEvent mouseEvent) {
        double zoom = gameZoom.getValue() / 100;
        canvas.setScaleX(zoom);
        canvas.setScaleY(zoom);
    }
}