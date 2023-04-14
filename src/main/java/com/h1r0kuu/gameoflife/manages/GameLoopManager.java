package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.components.ButtonComponent;
import com.h1r0kuu.gameoflife.utils.LabelUtility;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameLoopManager {
    private static final Logger logger = LogManager.getLogger(GameLoopManager.class);

    private AnimationTimer animationTimer;
    private GameManager gameManager;
    private ButtonComponent fpsCounterButton;

    private static final int FPS_SET = 180;

    public GameLoopManager() {
        logger.info("GameLoopManager init");
        createTimer();
    }

    public void createTimer() {
        logger.info("Timer creation");
        this.animationTimer = new AnimationTimer() {

            final double timePerFrame = 1_000_000_000.0 / FPS_SET;
            long lastFrame = System.nanoTime();
            long now;
            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            long lastUpdate = System.currentTimeMillis();

            @Override
            public void handle(long l) {
                long updateInterval = (long) (1000.0 / gameManager.getGameSpeed());
                now = System.nanoTime();
                if(now - lastFrame >= timePerFrame) {
                    gameManager.getGameBoardManager().redrawBoard();
                    lastFrame = now;
                    frames++;
                }

                if(System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
                    fpsCounterButton.setText(LabelUtility.getText(frames, LabelUtility.FPS));
                    double percentage = (frames - 1) / ((FPS_SET / 2.0) - 1);
                    int rValue = (int) ((1 - percentage) * 255);
                    int gValue = (int) (percentage * 255);
                    fpsCounterButton.setTextFill(Color.rgb(rValue, gValue, 0));
                    frames = 0;
                }

                if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
                    lastUpdate = System.currentTimeMillis();
                    if (!gameManager.isPaused()) {
                        gameManager.getGameBoardManager().nextGeneration();
                    }
                }
            }
        };
    }

    public void startGameLoop() {
        animationTimer.start();
    }

    public void stopGameLoop() {
        animationTimer.stop();
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setFpsCounterButton(ButtonComponent fpsCounterButton) {
        this.fpsCounterButton = fpsCounterButton;
    }
}
