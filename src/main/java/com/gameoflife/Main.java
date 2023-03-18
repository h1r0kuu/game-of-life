package com.gameoflife;

import com.gameoflife.gui.GamePanel;
import com.gameoflife.gui.OptionPanel;

public class Main {
    public static void main(String[] args){
        GamePanel gamePanel = new GamePanel();
        OptionPanel optionPanel = new OptionPanel(gamePanel);
        Game game = new Game(gamePanel, optionPanel);
        game.startGame();
    }
}
