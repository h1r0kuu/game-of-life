package com.h1r0kuu.gameoflife.components;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class DropdownButtonComponent extends Group {
    private List<ButtonComponent> dropdownButtons = new ArrayList<>();
    private static final int DROPDOWN_PADDING = 5;
    private static final int BUTTONS_PER_COLUMN = 6;
    private double currentX = 0;
    private double currentY = 0;
    private int currentColumnCount = 0;

    public DropdownButtonComponent(ButtonComponent ...buttons) {
        ObservableList<Node> childs = getChildren();
        for(ButtonComponent btn : buttons) {
            addSubButton(btn);
            childs.add(btn);
        }
    }

    public ButtonComponent getLastElement() {
        return dropdownButtons.get(dropdownButtons.size() - 1);
    }

    public void addSubButton(ButtonComponent subButton) {
        dropdownButtons.add(subButton);
        subButton.setLayoutX(this.getLayoutX() + currentX);
        subButton.setLayoutY(this.getLayoutY() + currentY);
        currentColumnCount++;

        if (currentColumnCount == BUTTONS_PER_COLUMN) {
            currentColumnCount = 0;
            currentX += subButton.getLayoutBounds().getWidth() + DROPDOWN_PADDING;
            currentY += 2 * this.getLayoutY();
        } else {
            currentY += subButton.getLayoutBounds().getHeight() + DROPDOWN_PADDING;
        }
    }

    public void toggleSubButtons(boolean visible) {
        for (ButtonComponent subButton : dropdownButtons) {
            subButton.setVisible(visible);
            subButton.setManaged(visible);
        }
    }
}
