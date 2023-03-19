package com.gameoflife.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OptionPanel extends JPanel {
    private Button startButton;
    private Button stopButton;

    private JSlider gameSpeed;

    private Button clearButton;

    private JTextField probabilityField;

    private JLabel generationCount;
    private JLabel cellInfo;

    public OptionPanel(GamePanel gamePanel) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        startButton = new Button("Start");
        startButton.addActionListener(e -> gamePanel.setPaused(false));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(startButton, gbc);

        stopButton = new Button("Stop");
        stopButton.addActionListener(e -> gamePanel.setPaused(true));
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(stopButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);
        gameSpeed = new JSlider(JSlider.HORIZONTAL, 1, 100, 50);
        gameSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gamePanel.setSpeed(((JSlider)e.getSource()).getValue());
            }
        });
        add(gameSpeed, gbc);

        clearButton = new Button("Clear");
        clearButton.addActionListener(e -> gamePanel.clearGrid());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        add(clearButton, gbc);

        probabilityField = new JTextField("50", 5);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(probabilityField, gbc);

        Button randomizeButton = new Button("Randomize");
        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int probability = Integer.parseInt(probabilityField.getText());
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
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        add(randomizeButton, gbc);

        generationCount = new JLabel("Generation: " + gamePanel.getGeneration());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 10, 5);
        add(generationCount, gbc);

        String labelText = "<html>Cell row: 0 Cell column: 0 Neighbours: 0</html";
        cellInfo = new JLabel(labelText);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 5;
        gbc.weightx = 10.0;
        add(cellInfo, gbc);
    }

    public void updateGenerationLabel(int generation) {
        generationCount.setText("Generation: " + generation);
    }

    public void updateCellInfo(int row, int column, int neighbours) {
        cellInfo.setText("<html>Cell row: " + row + " Cell column: " + column + " Neighbours: " + neighbours + "</html");
    }
}
