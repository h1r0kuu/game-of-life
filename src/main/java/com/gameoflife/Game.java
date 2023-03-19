package com.gameoflife;

import com.gameoflife.adapters.KeyAdapter;
import com.gameoflife.gui.GameFrame;
import com.gameoflife.gui.GamePanel;
import com.gameoflife.gui.OptionPanel;

public class Game implements Runnable {

    private Thread gameThread;
    private final GamePanel gamePanel;
    private OptionPanel optionPanel;
    private GameFrame gameFrame;
    private final int FPS_SET = 120;


    public void togglePause() {
        gamePanel.setPaused(!gamePanel.isPaused());
    }

    public Game(GamePanel gamePanel, OptionPanel optionPanel) {
        gamePanel.addKeyListener(new KeyAdapter(this));
        this.gamePanel = gamePanel;
        this.optionPanel = optionPanel;
        this.gamePanel.setOptionPanel(optionPanel);
        this.gameFrame = new GameFrame(gamePanel, optionPanel);
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

        long lastUpdate = System.currentTimeMillis();
        while (true) {
            long updateInterval = (long) (1000.0 / (gamePanel.getSpeed() / 10) );

            now = System.nanoTime();
            if(now - lastFrame >= timePerFrame) {
                gamePanel.repaint();
                lastFrame = now;
                frames++;
            }

            if(System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }

            if (System.currentTimeMillis() - lastUpdate >= updateInterval) {
                lastUpdate = System.currentTimeMillis();
                if (!gamePanel.isPaused()) {
                    optionPanel.updateGenerationLabel(gamePanel.getGeneration() + 1);
                    gamePanel.nextGeneration();
                }
            }
        }
    }
}
