package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.controllers.AppController;
import com.h1r0kuu.gameoflife.controllers.PopulationGraphController;
import com.h1r0kuu.gameoflife.manages.GameLoopManager;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Pattern;
import com.h1r0kuu.gameoflife.utils.Constants;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GamePreloader extends Preloader {
    private ProgressBar progressBar;
    private Label progressText;
    private Stage preloaderStage;
    private ExecutorService executor;

    public GamePreloader() {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/loader.fxml"));
                Parent root = fxmlLoader.load();
                progressBar = (ProgressBar) fxmlLoader.getNamespace().get("progressBar");
                progressText = (Label) fxmlLoader.getNamespace().get("progressText");
                preloaderStage = new Stage();
                preloaderStage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor = Executors.newSingleThreadExecutor();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        executor.execute(() -> {
            loadPatterns();

            Platform.runLater(() -> {
                preloaderStage.hide();
                try {
                    AppController appController = new AppController();

                    Scene scene = new Scene(appController);
                    primaryStage.setTitle("Game Of Life");
                    primaryStage.setScene(scene);
                    primaryStage.show();

                    PopulationGraphController populationGraphController = new PopulationGraphController();
                    Stage graphStage = new Stage();
                    graphStage.setScene(new Scene(populationGraphController));
                    graphStage.show();

                    appController.getGameManager().setGraphData(populationGraphController.getData());

                    GameLoopManager gameLoopManager = new GameLoopManager(appController.getGameManager(), appController.getGridService(), appController.getLifeRenderer());
                    gameLoopManager.startGameLoop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        Platform.runLater(() -> preloaderStage.show());
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            double progress = ((ProgressNotification) info).getProgress();
            Platform.runLater(() -> progressBar.setProgress(progress));
        }
    }

    private void loadPatterns() {
        ClassLoader classLoader = getClass().getClassLoader();

        URL url = classLoader.getResource(Constants.PATTERNS_FOLDER_NAME);
        String path = url.getPath();
        File folder = new File(path);
        File[] listFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".rle"));
        long finalTotalFiles = listFiles.length;

        AtomicInteger fileCount = new AtomicInteger();
        Arrays.stream(listFiles).forEach(file -> {
            String content = null;
            try {
                content = RLE.read(file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String name = null;
            try {
                name = RLE.getName(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (name == null) {
                name = file.getName();
            }
            Pattern pattern = new Pattern(content);
            if (pattern.getName() == null) {
                pattern.setName(name);
            }

            GameManager.patternManager.addPattern(pattern);
            fileCount.incrementAndGet();
            double progress = fileCount.get() / (double) finalTotalFiles;
            Platform.runLater(() -> {
                progressText.setText("Patterns loaded %d/%d".formatted(fileCount.get(), finalTotalFiles));
                progressBar.setProgress(progress);
            });
        });
    }
}