package com.h1r0kuu.gameoflife;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOfLife extends Application {
    private static final String FXML_PATH = "hello-view.fxml";
    private static final String APP_TITLE = "Game Of Life";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameOfLife.class.getResource(FXML_PATH));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}