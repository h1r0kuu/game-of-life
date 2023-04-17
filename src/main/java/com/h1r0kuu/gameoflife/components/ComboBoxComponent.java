package com.h1r0kuu.gameoflife.components;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class ComboBoxComponent<T> extends ComboBox<T> {

    public ComboBoxComponent() {}

    public ComboBoxComponent(T[] items) {
        super(FXCollections.observableArrayList(items));
    }

    public void setItems(T[] items) {
        super.setItems(FXCollections.observableArrayList(items));
    }

    public void addItem(T item) {
        getItems().add(item);
    }

    public void handleChange(ChangeListener<? super Number> listener) {
        getSelectionModel().selectedIndexProperty().addListener(listener);
    }
}
