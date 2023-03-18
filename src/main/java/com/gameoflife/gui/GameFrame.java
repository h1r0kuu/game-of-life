package com.gameoflife.gui;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public GameFrame(GamePanel gPanel, OptionPanel optionPanel) {
        setTitle("Game of Life");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        gPanel.setPreferredSize(new Dimension((int) (WIDTH * 0.7), HEIGHT));
        add(gPanel, BorderLayout.WEST);

        optionPanel.setPreferredSize(new Dimension((int) (WIDTH - (WIDTH * 0.7)), HEIGHT));
        add(optionPanel, BorderLayout.EAST);

        setVisible(true);
    }
}
