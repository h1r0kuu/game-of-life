package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.enums.MoveType;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.utils.Constants;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SelectionRectangle {

    private final Rectangle rectangle;
    private final Rectangle rectangleForPaste;
    private final StackPane canvasContainer;
    private BufferedImage samplePattern;

    private int selectStartRow;
    private int selectStartCol;
    private int selectEndRow;
    private int selectEndCol;
    private int selectedColsLength;
    private int selectedRowsLength;


    public SelectionRectangle(Rectangle rectangle, Rectangle rectangleForPaste, StackPane canvasContainer) {
        this.rectangle = rectangle;
        this.rectangleForPaste = rectangleForPaste;
        this.canvasContainer = canvasContainer;
    }

    public void setX(int row) {
        rectangle.setTranslateY(row * Constants.CELL_SIZE);
    }

    public void setY(int col) {
        rectangle.setTranslateX(col * Constants.CELL_SIZE);
    }

    public void setWidth(int colsLength) {
        rectangle.setWidth(colsLength * Constants.CELL_SIZE);
    }

    public void setHeight(int rowsLength) {
        rectangle.setHeight(rowsLength * Constants.CELL_SIZE);
    }

    public void drawPasteRectangle(Cell[][] cellsToPaste, int x, int y) {
        int rows = cellsToPaste.length;
        int cols = cellsToPaste[0].length;

        int rectWidth = cols * Constants.CELL_SIZE;
        int rectHeight = rows * Constants.CELL_SIZE;

        int pasteStartRow = (int) Math.floor(y / Constants.CELL_SIZE);
        int pasteStartCol = (int) Math.floor(x / Constants.CELL_SIZE);
        samplePattern = new BufferedImage(rectWidth, rectHeight, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < rectHeight; row++) {
            for (int col = 0; col < rectWidth; col++) {
                Cell cell = cellsToPaste[row / Constants.CELL_SIZE][col / Constants.CELL_SIZE];
                int color;
                if(cell.isAlive()) {
                    color = 0xFFCC6633;
                } else {
                    color = 0xFFFF0000;
                }
                samplePattern.setRGB(col, row, color);
            }
        }

        rectangleForPaste.setTranslateX(pasteStartCol * Constants.CELL_SIZE);
        rectangleForPaste.setTranslateY(pasteStartRow * Constants.CELL_SIZE);

        rectangleForPaste.setWidth(rectWidth);
        rectangleForPaste.setHeight(rectHeight);
        rectangleForPaste.setFill(new ImagePattern(SwingFXUtils.toFXImage(samplePattern, null)));
    }

    public void selectRange(double startX, double startY, double endX, double endY) {
        double minX = Math.min(startX, endX);
        double minY = Math.min(startY, endY);

        double maxX = Math.max(startX, endX);
        double maxY = Math.max(startY, endY);


        selectStartRow = (int) Math.floor(minY / Constants.CELL_SIZE);
        selectStartCol = (int) Math.floor(minX / Constants.CELL_SIZE);
        selectEndRow = (int) Math.floor(maxY / Constants.CELL_SIZE);
        selectEndCol = (int) Math.floor(maxX / Constants.CELL_SIZE);
        selectedColsLength = (selectEndRow - selectStartRow) + 1;
        selectedRowsLength = (selectEndCol - selectStartCol) + 1;

        update();
    }

    public void move(MoveType moveType) {
        switch (moveType) {
            case LEFT -> {
                selectStartCol -= 1;
                selectEndCol -= 1;
            }
            case RIGHT -> {
                selectStartCol += 1;
                selectEndCol += 1;
            }
            case UP -> {
                selectStartRow -= 1;
                selectEndRow -= 1;
            }
            case DOWN -> {
                selectStartRow += 1;
                selectEndRow += 1;
            }
        }
        update();
    }

    public void rotate(MoveType moveType) {
        int tempStartRow = selectStartRow;
        int tempStartCol = selectStartCol;
        int tempEndRow = selectEndRow;
        int tempEndCol = selectEndCol;
        int tempColsLength = selectedColsLength;
        int tempRowsLength = selectedRowsLength;

        switch (moveType) {
            case LEFT -> {
                selectStartRow = tempEndRow - tempRowsLength + 1;
                selectEndCol = tempStartCol + tempColsLength - 1;
            }
            case RIGHT -> {
                selectStartCol = tempEndCol - tempColsLength + 1;
                selectEndRow = tempEndRow - tempRowsLength + 1;
            }
        }

        selectedColsLength = tempRowsLength;
        selectedRowsLength = tempColsLength;

        update();
    }

    public void update() {
        setX(selectStartRow);
        setY(selectStartCol);
        setWidth(selectedRowsLength);
        setHeight(selectedColsLength);
    }

    public int getSelectStartRow() {
        return selectStartRow;
    }

    public void setSelectStartRow(int selectStartRow) {
        this.selectStartRow = selectStartRow;
    }

    public int getSelectStartCol() {
        return selectStartCol;
    }

    public void setSelectStartCol(int selectStartCol) {
        this.selectStartCol = selectStartCol;
    }

    public int getSelectEndRow() {
        return selectEndRow;
    }

    public void setSelectEndRow(int selectEndRow) {
        this.selectEndRow = selectEndRow;
    }

    public int getSelectEndCol() {
        return selectEndCol;
    }

    public void setSelectEndCol(int selectEndCol) {
        this.selectEndCol = selectEndCol;
    }

    public int getSelectedColsLength() {
        return selectedColsLength;
    }

    public void setSelectedColsLength(int selectedColsLength) {
        this.selectedColsLength = selectedColsLength;
    }

    public int getSelectedRowsLength() {
        return selectedRowsLength;
    }

    public void setSelectedRowsLength(int selectedRowsLength) {
        this.selectedRowsLength = selectedRowsLength;
    }

    public Rectangle getRectangleForPaste() {
        return rectangleForPaste;
    }
}
