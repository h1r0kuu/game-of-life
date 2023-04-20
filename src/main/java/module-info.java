module com.h1r0kuu.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.apache.logging.log4j;
    requires java.desktop;
    requires javafx.swing;

    opens com.h1r0kuu.gameoflife to javafx.fxml;
    exports com.h1r0kuu.gameoflife;
    exports com.h1r0kuu.gameoflife.manages;
    opens com.h1r0kuu.gameoflife.manages to javafx.fxml;
    exports com.h1r0kuu.gameoflife.models;
    opens com.h1r0kuu.gameoflife.models to javafx.fxml;
    exports com.h1r0kuu.gameoflife.handlers;
    opens com.h1r0kuu.gameoflife.handlers to javafx.fxml;
    exports com.h1r0kuu.gameoflife.enums;
    opens com.h1r0kuu.gameoflife.enums to javafx.fxml;
    exports com.h1r0kuu.gameoflife.controllers;
    opens com.h1r0kuu.gameoflife.controllers to javafx.fxml;
    exports com.h1r0kuu.gameoflife.service.grid;
    opens com.h1r0kuu.gameoflife.service.grid to javafx.fxml;
    exports com.h1r0kuu.gameoflife.renderer;
    opens com.h1r0kuu.gameoflife.renderer to javafx.fxml;
}