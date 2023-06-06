package com.h1r0kuu.gameoflife.components;

import com.h1r0kuu.gameoflife.enums.MoveType;
import com.h1r0kuu.gameoflife.models.Cell;
import com.h1r0kuu.gameoflife.utils.Constants;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class SelectionRectangle {

    private final Rectangle rectangle;
    private final Rectangle rectangleForPaste;

    private int selectStartRow;
    private int selectStartCol;
    private int selectEndRow;
    private int selectEndCol;
    private int selectedColsLength;
    private int selectedRowsLength;


    public SelectionRectangle(Rectangle rectangle, Rectangle rectangleForPaste) {
        this.rectangle = rectangle;
        this.rectangleForPaste = rectangleForPaste;
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

        int[] colors = new int[rectWidth * rectHeight];
        for (int row = 0; row < rectHeight; row++) {
            for (int col = 0; col < rectWidth; col++) {
                Cell cell = cellsToPaste[row / Constants.CELL_SIZE][col / Constants.CELL_SIZE];
                int color = cell.isAlive() ? 0xFFCC6633 : 0xFFFF0000;
                colors[row * rectWidth + col] = color;
            }
        }

        WritableImage image = new WritableImage(rectWidth, rectHeight);
        PixelWriter writer = image.getPixelWriter();
        writer.setPixels(0, 0, rectWidth, rectHeight, PixelFormat.getIntArgbInstance(), colors, 0, rectWidth);

        rectangleForPaste.setTranslateX(pasteStartCol * Constants.CELL_SIZE);
        rectangleForPaste.setTranslateY(pasteStartRow * Constants.CELL_SIZE);
        rectangleForPaste.setWidth(rectWidth);
        rectangleForPaste.setHeight(rectHeight);
        rectangleForPaste.setFill(new ImagePattern(image));
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

    public void update() {
        setX(selectStartRow);
        setY(selectStartCol);
        setWidth(selectedRowsLength);
        setHeight(selectedColsLength);
    }

    public void clear() {
        selectStartRow = 0;
        selectEndRow = 0;
        selectStartCol = 0;
        selectEndCol = 0;
        selectedColsLength = 0;
        selectedRowsLength = 0;
        setWidth(0);
        setHeight(0);
    }

    public int getSelectStartRow() {
        return selectStartRow;
    }

    public int getSelectStartCol() {
        return selectStartCol;
    }

    public int getSelectEndRow() {
        return selectEndRow;
    }

    public int getSelectEndCol() {
        return selectEndCol;
    }

    public int getSelectedColsLength() {
        return selectedColsLength;
    }

    public int getSelectedRowsLength() {
        return selectedRowsLength;
    }

    public Rectangle getRectangleForPaste() {
        return rectangleForPaste;
    }
}