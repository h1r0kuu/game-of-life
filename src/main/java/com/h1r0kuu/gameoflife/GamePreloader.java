package com.h1r0kuu.gameoflife;

import com.h1r0kuu.gameoflife.controllers.AppController;
import com.h1r0kuu.gameoflife.controllers.PopulationChartController;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
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
                preloaderStage.setResizable(false);
                preloaderStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });
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
            try {
                loadPatterns();
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                preloaderStage.hide();
                try {
                    AppController appController = new AppController();
                    GameManager gameManager = appController.getGameManager();

                    Scene scene = new Scene(appController);
                    primaryStage.setResizable(false);
                    primaryStage.setTitle("Game Of Life");
                    primaryStage.setScene(scene);
                    primaryStage.show();

                    PopulationChartController populationChartController = new PopulationChartController();
                    Stage chartStage = new Stage();
                    chartStage.setScene(new Scene(populationChartController));

                    gameManager.setGraphData(populationChartController.getData());
                    gameManager.setChartStage(chartStage);
                    chartStage.setOnCloseRequest(event -> gameManager.onChartClose());


                    GameLoopManager gameLoopManager = new GameLoopManager(appController.getGameManager(), appController.getGridService(), appController.getLifeRenderer());
                    gameLoopManager.startGameLoop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        Platform.runLater(() -> preloaderStage.show());
    }

    private static String readFileContent(Path filePath, Charset charset) {
        try {
            byte[] encodedBytes = Files.readAllBytes(filePath);
            return new String(encodedBytes, charset);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage() + " ");
            return null;
        }
    }
    private void loadPatterns() throws IOException, URISyntaxException {
        Path path = getResourcePath(Constants.PATTERNS_FOLDER_NAME);
        if (path != null) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                long fileCount = Files.list(path).count();
                AtomicInteger fileCounter = new AtomicInteger();
                for (Path flPath : directoryStream) {
                    String content = readFileContent(flPath, StandardCharsets.UTF_8);
                    String name = null;
                    try {
                        name = RLE.getName(content);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (name == null) {
                        name = flPath.getFileName().toString();
                    }
                    Pattern pattern = new Pattern(content);
                    if (pattern.getName() == null) {
                        pattern.setName(name);
                    }

                    GameManager.patternManager.addPattern(pattern);
                    fileCounter.incrementAndGet();
                    double progress = fileCounter.get() / (double) fileCount;
                    Platform.runLater(() -> {
                        progressText.setText("Patterns loaded %d/%d".formatted(fileCounter.get(), fileCount));
                        progressBar.setProgress(progress);
                    });
                }
            }
        }
    }

    private static Path getResourcePath(String folderName) throws URISyntaxException, IOException {
        URL url = Main.class.getClassLoader().getResource(folderName);
        if (url != null) {
            URI uri = url.toURI();
            if (uri.getScheme().equals("file")) {
                return Paths.get(uri);
            } else if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                return fileSystem.getPath(folderName);
            }
        }
        return null;
    }
}