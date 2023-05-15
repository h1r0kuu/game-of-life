package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.enums.UserActionState;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

public class CanvasWrapperMouseHandlers {
    private final ScrollPane canvasWrapper;

    public CanvasWrapperMouseHandlers(ScrollPane canvasWrapper) {
        this.canvasWrapper = canvasWrapper;
    }


    public void onMouseDragged(MouseEvent e) {
        if(GameManager.userActionState.equals(UserActionState.MOVING)) {
            canvasWrapper.setPannable(true);
        }
    }

    public void onMouseReleased(MouseEvent e) {
        canvasWrapper.setPannable(false);
    }
}
