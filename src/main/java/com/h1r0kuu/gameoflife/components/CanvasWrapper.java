package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.handlers.CanvasWrapperMouseHandlers;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CanvasWrapper extends ScrollPane {
    public CanvasComponent canvas;

    private double scaleValue = 0.7;
    private double zoomIntensity = 0.08;
    private Node target;
    private Node zoomNode;

    public Rectangle selectionRectangle;
    public CanvasWrapper(CanvasComponent canvas) {
        super();
        this.canvas = canvas;

        StackPane stackPane = new StackPaneComponent();
        this.target = stackPane;

        selectionRectangle = canvas.getSelectionRectangle();
        stackPane.setAlignment(Pos.TOP_LEFT);
        stackPane.getChildren().addAll(canvas, selectionRectangle);

        this.zoomNode = new Group(stackPane);
        setContent(outerNode(zoomNode));

        setStyle("-fx-background: #666666;");
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        setFitToWidth(true);
        setPannable(false);
        CanvasWrapperMouseHandlers canvasWrapperMouseHandlers = new CanvasWrapperMouseHandlers(this);
        this.setOnMouseMoved(canvasWrapperMouseHandlers::onMouseMoved);
        this.setOnMousePressed(canvasWrapperMouseHandlers::onMousePressed);
        this.setOnMouseDragged(canvasWrapperMouseHandlers::onMouseDragged);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, canvasWrapperMouseHandlers::onMouseDragged);
        this.setOnScroll(canvasWrapperMouseHandlers::onScroll);

        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        VBox vBox = new VBox(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout();

        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}