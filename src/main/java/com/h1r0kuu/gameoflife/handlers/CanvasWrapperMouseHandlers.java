package com.h1r0kuu.gameoflife.handlers;

import com.h1r0kuu.gameoflife.manages.GameManager;
import com.h1r0kuu.gameoflife.UserActionState;
import com.h1r0kuu.gameoflife.components.CanvasWrapper;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class CanvasWrapperMouseHandlers {
    private final CanvasWrapper canvasWrapper;
    private double lastX, lastY;

    public CanvasWrapperMouseHandlers(CanvasWrapper canvasWrapper) {
        this.canvasWrapper = canvasWrapper;
    }

    public void onMouseMoved(MouseEvent e) {
    }

    public void onMousePressed(MouseEvent e) {
        lastX = e.getSceneX();
        lastY = e.getSceneY();
    }

    public void onMouseDragged(MouseEvent e) {
        if(GameManager.userActionState.equals(UserActionState.MOVING)) {
            double deltaX = e.getSceneX() - lastX;
            double deltaY = e.getSceneY() - lastY;

            canvasWrapper.canvas.setTranslateX(canvasWrapper.canvas.getTranslateX() + deltaX);
            canvasWrapper.canvas.setTranslateY(canvasWrapper.canvas.getTranslateY() + deltaY);

            lastX = e.getSceneX();
            lastY = e.getSceneY();
        }
    }

    public void onScroll(ScrollEvent e) {
        double currentScale = canvasWrapper.canvas.getScaleX();
        double mouseX = e.getX();
        double mouseY = e.getY();

        double zoomFactor = 1.2;
        double deltaY = e.getDeltaY();
        if (deltaY < 0) {
            currentScale /= zoomFactor;
        } else {
            currentScale *= zoomFactor;
        }

        double newScale = Math.max(0.1, Math.min(currentScale, 10.0));
        double pivotX = (mouseX - canvasWrapper.canvas.getTranslateX()) / canvasWrapper.canvas.getWidth();
        double pivotY = (mouseY - canvasWrapper.canvas.getTranslateY()) / canvasWrapper.canvas.getHeight();

        double deltaX = (1 - newScale / currentScale) * pivotX;
        double deltaY1 = (1 - newScale / currentScale) * pivotY;

        canvasWrapper.canvas.setScaleX(newScale);
        canvasWrapper.canvas.setScaleY(newScale);

        canvasWrapper.canvas.setTranslateX(canvasWrapper.canvas.getTranslateX() + deltaX);
        canvasWrapper.canvas.setTranslateY(canvasWrapper.canvas.getTranslateY() + deltaY1);
    }
}
