package com.gameoflife;

import com.gameoflife.adapters.KeyAdapter;
import com.gameoflife.gui.GameFrame;
import com.gameoflife.gui.GamePanel;

import java.awt.event.*;
import java.util.Timer;

public class Game implements Runnable {

    private Thread gameThread;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private final int FPS_SET = 5;

    private boolean isPaused = false;

    public void togglePause() {
        isPaused = !isPaused;
    }

    public Game(GamePanel gamePanel) {
        gamePanel.addKeyListener(new KeyAdapter());
        gamePanel.requestFocus();
        this.gamePanel = gamePanel;
        this.gameFrame = new GameFrame(gamePanel);
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1_000_000_000.0 / FPS_SET;
        long lastFrame = System.nanoTime();
        long now;

        int frames = 0;
        long lastCheck = System.currentTimeMillis();

        while (true) {
            now = System.nanoTime();
            if(now - lastFrame >= timePerFrame) {
                if(isPaused) {
                    gamePanel.repaint();
                }
                lastFrame = now;
                frames++;
            }

            if(System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }
}
