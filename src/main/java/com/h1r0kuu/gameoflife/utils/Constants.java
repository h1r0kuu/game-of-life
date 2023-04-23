package com.h1r0kuu.gameoflife.utils;

import com.h1r0kuu.gameoflife.GameOfLife;
import javafx.scene.image.Image;

import java.util.Objects;

public class Constants {
    public static final int FPS_SET = 60;
    public static final int CELL_SIZE = 10;
    public static final int CELL_SHADE_SPEED = 25;
    public static final int[][] NEIGHBOUR_OFFSETS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0,  -1},          {0,  1},
            {1,  -1}, {1,  0}, {1,  1}
    };

    public static final String IDLE_BUTTON = "-fx-background-color: rgb(0, 0, 0, 0.76);";
    public static final String ACTIVE_BUTTON = "-fx-background-color: rgb(255, 0, 0, 0.76);";
    public static final Image PAUSE_IMAGE = new Image(Objects.requireNonNull(GameOfLife.class.getResource("/images/pause.png")).toExternalForm());
    public static final Image PLAY_IMAGE = new Image(Objects.requireNonNull(GameOfLife.class.getResource("/images/play.png")).toExternalForm());
    public static final double ZOOM_INTENSITY = 0.08;
    public static final double MIN_SCALE = 0;
    public static final double MAX_SCALE = 10;
}
