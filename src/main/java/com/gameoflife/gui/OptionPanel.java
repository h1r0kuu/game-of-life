package com.gameoflife.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Objects;


public class OptionPanel extends JPanel {

    private HashMap<String, ImageIcon> imageMap = new HashMap<>();

    private JButton resetButton;
    private JButton prevGeneration;
    private JButton nextGeneration;
    private JButton pauseToggle;

    private JButton drawButton;
    private JButton selectButton;
    private JButton panButton;

    private Button toggleGrid;

    private JSlider gameSpeed;
    private JSlider zoom;

    private JTextField probabilityField;

    private JLabel generationCount;
    private JLabel cellRow;
    private JLabel cellCol;
    private JLabel cellNeighbours;

    public void loadImages() {
        imageMap.put("pause", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pause.png"))));
        imageMap.put("play", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/play.png"))));

        imageMap.put("previous", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/previous.png"))));
        imageMap.put("next", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/next.png"))));

        imageMap.put("reset", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/reset.png"))));

        imageMap.put("pen", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pen.png"))));
        imageMap.put("pan", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/pan.png"))));
        imageMap.put("select", new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/select.png"))));
    }

    public OptionPanel(GamePanel gamePanel) {
        loadImages();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        resetButton = new JButton(imageMap.get("reset"));
        resetButton.addActionListener(e -> gamePanel.clearGrid());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(resetButton, gbc);

        prevGeneration = new JButton(imageMap.get("previous"));
        prevGeneration.addActionListener(e -> gamePanel.clearGrid());
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(prevGeneration, gbc);

        pauseToggle = new JButton(imageMap.get("pause"));
        pauseToggle.addActionListener(e -> {
            if(gamePanel.isPaused()) {
                gamePanel.setPaused(false);
                pauseToggle.setIcon(imageMap.get("pause"));
            } else {
                gamePanel.setPaused(true);
                pauseToggle.setIcon(imageMap.get("play"));
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(pauseToggle, gbc);

        nextGeneration = new JButton(imageMap.get("next"));
        nextGeneration.addActionListener(e -> gamePanel.clearGrid());
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(nextGeneration, gbc);

        panButton = new JButton(imageMap.get("pan"));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(panButton, gbc);

        drawButton = new JButton(imageMap.get("pen"));
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(drawButton, gbc);

        selectButton = new JButton(imageMap.get("select"));
        gbc.gridx = 3;
        gbc.gridy = 1;
        add(selectButton, gbc);

        gameSpeed = new JSlider(JSlider.HORIZONTAL, 1, 100, 50);
        gameSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gamePanel.setSpeed(((JSlider)e.getSource()).getValue());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 5, 10, 5);
        add(gameSpeed, gbc);



        probabilityField = new JTextField("50", 5);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(probabilityField, gbc);

        Button randomizeButton = new Button("Randomize");
        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int probability = Integer.parseInt(probabilityField.getText().trim());
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
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        add(randomizeButton, gbc);

        generationCount = new JLabel("Generation: " + gamePanel.getGeneration());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 10, 5);
        add(generationCount, gbc);

        cellRow = new JLabel("Cell row: 0");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(cellRow, gbc);

        cellCol = new JLabel("Cell column: 0");
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(cellCol, gbc);

        cellNeighbours = new JLabel("Neighbours: 0");
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(cellNeighbours, gbc);
    }

    public void updateGenerationLabel(int generation) {
        generationCount.setText("Generation: " + generation);
    }

    public void updateCellInfo(int row, int column, int neighbours) {
        cellRow.setText("Cell row: " + row);
        cellCol.setText("Cell column: " + column);
        cellNeighbours.setText("Neighbours: " + neighbours);
    }
}
