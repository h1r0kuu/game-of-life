package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.GameOfLife;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class ImageViewComponent extends ImageView {

    private static final int DEFAULT_WIDTH = 25;
    private static final int DEFAULT_HEIGHT = 25;

    public ImageViewComponent(String imagePath) {
        super();
        this.setImage(new Image(Objects.requireNonNull(GameOfLife.class.getResource(imagePath)).toExternalForm()));
        this.setFitHeight(DEFAULT_HEIGHT);
        this.setFitWidth(DEFAULT_WIDTH);
    }
}
