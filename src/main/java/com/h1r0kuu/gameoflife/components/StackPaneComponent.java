package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.handlers.HotKeysHandler;
import javafx.scene.layout.StackPane;

public class StackPaneComponent extends StackPane {
    public StackPaneComponent() {
        super();
    }

    public void setGameManager(GameManager gameManager) {
        this.setOnKeyPressed(new HotKeysHandler(gameManager)::onKeyPressed);
    }
}