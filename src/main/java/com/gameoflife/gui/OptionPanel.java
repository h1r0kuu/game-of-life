package com.gameoflife.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OptionPanel extends JPanel {
    public OptionPanel(GamePanel gamePanel) {
        Button startButton = new Button();
        startButton.setLabel("Start");
        startButton.addActionListener(e -> gamePanel.setPaused(true));

        Button stopButton = new Button();
        stopButton.setLabel("Stop");
        stopButton.addActionListener(e -> gamePanel.setPaused(false));

        Button clearButton = new Button();
        clearButton.addActionListener(e -> gamePanel.clearGrid());
        clearButton.setLabel("Clear");

        TextField textField =new TextField();



        Button randomizeButton = new Button();
        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int probability = Integer.parseInt(textField.getText());
                    if(probability < 0 || probability > 100) {
                        JOptionPane.showMessageDialog(gamePanel, "Probability must be between 0 and 100");
                    } else {
                        gamePanel.randomizeGrid(probability);
                    }
                }
                catch (NumberFormatException error) {
                    JOptionPane.showMessageDialog(gamePanel, error.getMessage());
                }
            }
        });

        randomizeButton.setLabel("Randomize");

        setBackground(Color.WHITE);

        add(startButton);
        add(stopButton);
        add(clearButton);
        add(randomizeButton);
        add(textField);
    }
}
