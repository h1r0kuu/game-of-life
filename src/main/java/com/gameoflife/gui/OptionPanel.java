package com.gameoflife.gui;

import javax.swing.*;
import java.awt.*;

public class OptionPanel extends JPanel {
    public static final int OptionPanelWidth = (int)(GameFrame.WIDTH * 0.3);
    public static final int OptionPanelHeight = GameFrame.HEIGHT;
    public OptionPanel() {
        Button button = new Button();
        setBackground(Color.WHITE);
        setBounds(GamePanel.GamePanelWidth, 0, OptionPanelWidth, OptionPanelHeight);
        setLayout(null);

        button.setBackground(Color.RED);
        button.setLabel("Stop");
        button.setBounds(0, 0, 111, 111);
        button.setLocation(0, 0);
        add(button);
    }
}
