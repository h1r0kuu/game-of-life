package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.renderer.LifeRenderer;
import com.h1r0kuu.gameoflife.service.grid.IGridService;
import com.h1r0kuu.gameoflife.utils.Constants;
import javafx.animation.AnimationTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameLoopManager {
    private static final Logger logger = LogManager.getLogger(GameLoopManager.class);

    private AnimationTimer animationTimer;
    private final LifeRenderer lifeRenderer;
    private final GameManager gameManager;
    //    private ButtonComponent fpsCounterButton;
    private IGridService IGridService;


    public GameLoopManager(GameManager gameManager, IGridService IGridService, LifeRenderer lifeRenderer) {
        logger.info("GameLoopManager init");
        this.gameManager = gameManager;
        this.IGridService = IGridService;
        this.lifeRenderer = lifeRenderer;
        createTimer();
    }

    public void createTimer() {
        logger.info("Timer creation");
        this.animationTimer = new AnimationTimer() {

            final double timePerFrame = 1_000_000_000.0 / Constants.FPS_SET;
            long lastFrame = System.nanoTime();
            long now;
            int frames = 0;
            long lastCheck = System.currentTimeMillis();
            long lastUpdate = System.currentTimeMillis();

            @Override
            public void handle(long l) {
                long updateInterval = (long) (1000.0 / gameManager.getGameSpeed());
                now = System.nanoTime();
                if (now - lastFrame >= timePerFrame) {
                    lifeRenderer.redraw();
                    lastFrame = now;
                    frames++;
                }

                if (System.currentTimeMillis() - lastCheck >= 1000) {
                    lastCheck = System.currentTimeMillis();
//                    fpsCounterButton.setText(LabelUtility.getText(frames, LabelUtility.FPS));
                    double percentage = (frames - 1) / ((Constants.FPS_SET / 2.0) - 1);
                    int rValue = (int) ((1 - percentage) * 255);
                    int gValue = (int) (percentage * 255);
//                    fpsCounterButton.setTextFill(Color.rgb(rValue, gValue, 0));
                    System.out.println(frames);

                    frames = 0;
                }

                if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
                    lastUpdate = System.currentTimeMillis();
                    if (!gameManager.isPaused()) {
                        gameManager.nextGeneration();
                    }
                }
            }
        };
    }

    public void startGameLoop() {
        animationTimer.start();
    }
}
