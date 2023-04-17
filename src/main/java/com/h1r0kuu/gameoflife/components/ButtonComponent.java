package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.handlers.ButtonMouseHandlers;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ButtonComponent extends Group {
    private RectangleComponent rectangleComponent;
    private LabelComponent labelComponent;
    private ImageViewComponent image;
    private DropdownButtonComponent dropdown;

    private boolean isActive = false;

    public static final Color IDLE_BUTTON_COLOR = Color.rgb(0, 0, 0, 0.76);
    public static final Color HOVERED_BUTTON_COLOR = Color.web("#FF0000");
    public static final Color DEFAULT_TEXT_FILL = Color.WHITE;
    public static final Color DEFAULT_ACTIVE_FILL = HOVERED_BUTTON_COLOR;

    public ButtonComponent(int layoutX, int layoutY) {
        super();
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setOnMouseEntered(ButtonMouseHandlers::onButtonHover);
        this.setOnMouseExited(ButtonMouseHandlers::onButtonHoverExited);

    }

    public ButtonComponent(int layoutX, int layoutY, String title) {
        this(layoutX, layoutY);
        this.rectangleComponent = new RectangleComponent(30, 30);
        this.labelComponent = new LabelComponent(title);
        this.labelComponent.setTextFill(DEFAULT_TEXT_FILL);
        this.labelComponent.boundsInLocalProperty().addListener((observable, oldBounds, newBounds) -> {
            double labelWidth = labelComponent.getBoundsInLocal().getWidth();
            double labelHeight = labelComponent.getBoundsInLocal().getHeight();
            rectangleComponent.setWidth(labelWidth + 10);
            rectangleComponent.setHeight(labelHeight + 10);
            double rectangleX = (labelComponent.getWidth() - rectangleComponent.getWidth()) / 2;
            double rectangleY = (labelComponent.getHeight() - rectangleComponent.getHeight()) / 2;
            rectangleComponent.setLayoutX(rectangleX);
            rectangleComponent.setLayoutY(rectangleY);
        });
        this.getChildren().addAll(rectangleComponent, labelComponent);
    }

    public ButtonComponent(int layoutX, int layoutY, ImageViewComponent image) {
        this(layoutX, layoutY);
        this.image = image;
        this.rectangleComponent = new RectangleComponent(30, 30);

        double imageX = (rectangleComponent.getWidth() - image.getFitWidth()) / 2;
        double imageY = (rectangleComponent.getHeight() - image.getFitHeight()) / 2;
        image.setTranslateX(imageX);
        image.setTranslateY(imageY);

        this.getChildren().addAll(rectangleComponent, this.image);
    }

    public RectangleComponent getRectangle() {
        return rectangleComponent;
    }

    public void setText(String newText) {
        if(labelComponent != null) labelComponent.setText(newText);
    }

    public void setTextFill(Paint paint) {
        if(labelComponent != null) labelComponent.setTextFill(paint);
    }

    public void changeImage(Image newImage) {
        image.setImage(newImage);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        if(dropdown != null) dropdown.toggleSubButtons(active);
        isActive = active;
        if(active) rectangleComponent.setFill(DEFAULT_ACTIVE_FILL);
    }

    public void setDropdown(DropdownButtonComponent dropdown) {
        this.dropdown = dropdown;
        dropdown.toggleSubButtons(isActive());
    }
}
