package com.h1r0kuu.gameoflife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameOfLife extends Application {
    private static final Logger logger = LogManager.getLogger(GameOfLife.class);

    public void startGame(String[] args) {
        logger.info("Launch");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GamePreloader preloader = new GamePreloader();

        preloader.start(primaryStage);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}