package com.gameoflife;

import com.gameoflife.gui.GamePanel;

public class Main {
    public static void main(String[] args){
        Game game = new Game(new GamePanel());
        game.startGame();
    }
}
