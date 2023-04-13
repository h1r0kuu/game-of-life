package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.components.ButtonComponent;
import javafx.scene.input.MouseEvent;

public class ButtonMouseHandlers {
    public static void onButtonHover(MouseEvent event) {
        ButtonComponent button = (ButtonComponent) event.getSource();
        if(!button.isActive()) button.getRectangle().setFill(ButtonComponent.HOVERED_BUTTON_COLOR);
    }

    public static void onButtonHoverExited(MouseEvent event) {
        ButtonComponent button = (ButtonComponent) event.getSource();
        if(!button.isActive()) button.getRectangle().setFill(ButtonComponent.IDLE_BUTTON_COLOR);
    }
}
