package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.controllers.AppController;
import com.h1r0kuu.gameoflife.manages.GameLoopManager;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Pattern;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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
        ClassLoader classLoader = Main.class.getClassLoader();

        long totalFiles = 0;
        try (Stream<Path> walk = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
            totalFiles = walk.filter(p -> !Files.isDirectory(p)).count();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger fileCount = new AtomicInteger();
        try (Stream<Path> walk2 = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource("all")).toURI()))) {
            long finalTotalFiles = totalFiles;
            walk2.filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("rle"))
                    .forEach(f -> {
                        try {
                            File file = new File(f);
                            String content = RLE.read(f);
                            String name = RLE.getName(content);
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
                        } catch (ArrayIndexOutOfBoundsException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}