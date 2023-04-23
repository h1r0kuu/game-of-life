package com.h1r0kuu.gameoflife.controllers;

import com.h1r0kuu.gameoflife.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class LoaderController extends StackPane {
    @FXML private Label progressText;
    @FXML private ProgressBar progressBar;

    public LoaderController() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/loader.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Label getProgressText() {
        return progressText;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
