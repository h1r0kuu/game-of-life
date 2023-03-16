package com.gameoflife.gui;

import javax.swing.*;

public class GameFrame extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public GameFrame(GamePanel gPanel) {
        add(gPanel);
        setTitle("Game of Life");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
