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
    private IGridService IGridService;

    private final long timePerFrame = (long) (1_000_000_000.0 / Constants.FPS_SET);
    private long lastFrame = 0;
    private int frames = 0;
    private long lastCheck = 0;
    private long lastUpdate = 0;


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

            @Override
            public void handle(long now) {
                if (lastFrame == 0) {
                    lastFrame = now;
                    lastCheck = System.nanoTime();
                    lastUpdate = System.nanoTime();
                    return;
                }

                long elapsedNanos = now - lastFrame;
                if (elapsedNanos < timePerFrame) {
                    return;
                }

                lastFrame = now;

                lifeRenderer.redraw();
                frames++;

                long elapsedMillis = (System.nanoTime() - lastCheck) / 1_000_000L;
                if (elapsedMillis >= 1000) {
                    lastCheck = System.nanoTime();
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }

                elapsedMillis = (System.nanoTime() - lastUpdate) / 1_000_000L;
                long updateInterval = (long) (1000.0 / gameManager.getGameSpeed());
                if (elapsedMillis >= updateInterval && !gameManager.isPaused()) {
                    lastUpdate = System.nanoTime();
                    gameManager.nextGeneration();
                }
            }
        };
    }

    public void startGameLoop() {
        animationTimer.start();
    }
}
