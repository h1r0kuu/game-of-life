package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.components.ButtonComponent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HotKeysHandler {

    private final GameManager gameManager;

    public HotKeysHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void onKeyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.C) {
            ClipboardContent content = new ClipboardContent();
            Clipboard.getSystemClipboard().setContent(content);
            e.consume();
        } else if(e.getCode() == KeyCode.X) {
            gameManager.getUiManager().toggleShowBorder();
        } else if(e.getCode() == KeyCode.PAUSE || e.getCode() == KeyCode.HOME) {
            gameManager.setPaused(!gameManager.isPaused());
        } else if(e.getCode() == KeyCode.A) {
            gameManager.gameBoardManager.previousGeneration();
            gameManager.gameBoardManager.previousGeneration();
        } else if(e.getCode() == KeyCode.D) {
            gameManager.gameBoardManager.nextGeneration();
        } else if(e.getCode() == KeyCode.C) {
            GameManager.themeManager.nextTheme();
        } else if(e.getCode() == KeyCode.R) {
            gameManager.gameBoardManager.clearBoard();
        } else if(e.getCode() == KeyCode.F) {
            ButtonComponent fpsCounterButton = gameManager.getUiManager().getFpsCounterButton();;
            fpsCounterButton.setVisible(!fpsCounterButton.isVisible());
        }
    }
}
