package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.enums.UserActionState;
import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.service.grid.IGridService;
import com.h1r0kuu.gameoflife.utils.RLE;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class HotKeysHandler {

    private final GameManager gameManager;
    private final IGridService iGridService;

    public HotKeysHandler(GameManager gameManager, IGridService iGridService) {
        this.gameManager = gameManager;
        this.iGridService = iGridService;
    }

    public void onKeyPressed(KeyEvent e) {
        if (e.isControlDown() && e.getCode() == KeyCode.C) {
            ClipboardContent content = new ClipboardContent();
            Cell[][] selectedCells = iGridService.getSelectedCells();
            content.putString(RLE.encode(selectedCells, "B3/S23"));
            Clipboard.getSystemClipboard().setContent(content);
            e.consume();
        } else if(e.isControlDown() && e.getCode() == KeyCode.V) {
            String rleString = Clipboard.getSystemClipboard().getString();
            try {
                Cell[][] copiedCells = RLE.decode(rleString);
                GameManager.userActionState = UserActionState.PASTING;
                gameManager.getGrid().setCellsToPaste(copiedCells);
            } catch (RuntimeException ex) {
                System.out.println(ex);
            }
        } else if(e.getCode() == KeyCode.X) {
//            gameManager.getUiManager().toggleShowBorder();
        } else if(e.getCode() == KeyCode.PAUSE || e.getCode() == KeyCode.HOME) {
            gameManager.setPaused(!gameManager.isPaused());
        } else if(e.getCode() == KeyCode.A) {
            gameManager.previousGeneration();
        } else if(e.getCode() == KeyCode.D) {
            gameManager.nextGeneration();
        } else if(e.getCode() == KeyCode.C) {
            GameManager.themeManager.nextTheme();

        } else if(e.getCode() == KeyCode.R) {
            gameManager.clearBoard();
        } else if(e.getCode() == KeyCode.F) {
//            ButtonComponent fpsCounterButton = gameManager.getUiManager().getFpsCounterButton();;
//            fpsCounterButton.setVisible(!fpsCounterButton.isVisible());
        }
    }
}
