package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.controllers.AppController;
import com.h1r0kuu.gameoflife.controllers.LoaderController;
import com.h1r0kuu.gameoflife.models.Pattern;
import com.h1r0kuu.gameoflife.manages.*;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GameOfLife extends Application {
    private static final Logger logger = LogManager.getLogger(GameOfLife.class);

    private void loadPatterns() {
        LoaderController loaderController = new LoaderController();
        ProgressBar progressBar = loaderController.getProgressBar();

        Stage progressStage = new Stage();
        Scene progressScene = new Scene(loaderController);
        progressStage.setScene(progressScene);
        progressStage.show();

        ClassLoader classLoader = Main.class.getClassLoader();

        try (Stream<Path> walk = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
            long totalFiles = walk.count();
            AtomicInteger fileCount = new AtomicInteger();
            try (Stream<Path> walk2 = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
                walk2
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.toString().toLowerCase())
                        .filter(f -> f.endsWith("rle"))
                        .forEach(f -> {
                            try {
                                File file = new File(f);
                                String content = RLE.read(f);
                                String name = RLE.getName(content);
                                if(name == null) name = file.getName();
                                Pattern pattern = new Pattern(content);
                                if(pattern.getName() == null) {
                                    pattern.setName(name);
                                }
                                GameManager.patternManager.addPattern(pattern);
                                progressBar.setProgress(fileCount.incrementAndGet() / (double) totalFiles);
                            } catch (ArrayIndexOutOfBoundsException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        progressStage.hide();
    }

    public void startGame(String[] args) {
        logger.info("Launch");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Start");
        loadPatterns();

        AppController appController = new AppController();
        Scene scene = new Scene(appController);
        primaryStage.setTitle("Game Of Life");
        primaryStage.setScene(scene);
        primaryStage.show();

        GameLoopManager gameLoopManager = new GameLoopManager(appController.getGameManager(), appController.getGridService(), appController.getLifeRenderer());
        gameLoopManager.startGameLoop();
    }
}