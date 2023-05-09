package com.h1r0kuu.gameoflife.controllers;

import com.h1r0kuu.gameoflife.Main;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PopulationGraphController extends AnchorPane {
    @FXML private LineChart<Number, Number> lineChart;
    @FXML private NumberAxis population;
    @FXML private NumberAxis generation;
    private XYChart.Series<Number, Number> series;

    public PopulationGraphController() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/population.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        series = new XYChart.Series<>();
        lineChart.getData().add(series);

        population.setTickLabelFormatter(new NumberAxis.DefaultFormatter(population) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });

        generation.setTickLabelFormatter(new NumberAxis.DefaultFormatter(generation) {
            @Override
            public String toString(Number object) {
                return String.format("%d", object.intValue());
            }
        });
    }

    public ObservableList<XYChart.Data<Number, Number>> getData() {
        return series.getData();
    }
}
