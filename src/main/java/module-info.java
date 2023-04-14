module com.h1r0kuu.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.apache.logging.log4j;

    opens com.h1r0kuu.gameoflife to javafx.fxml;
    exports com.h1r0kuu.gameoflife;
    exports com.h1r0kuu.gameoflife.theme;
    opens com.h1r0kuu.gameoflife.theme to javafx.fxml;
    exports com.h1r0kuu.gameoflife.manages;
    opens com.h1r0kuu.gameoflife.manages to javafx.fxml;
    exports com.h1r0kuu.gameoflife.entity;
    opens com.h1r0kuu.gameoflife.entity to javafx.fxml;
    exports com.h1r0kuu.gameoflife.components;
    opens com.h1r0kuu.gameoflife.components to javafx.fxml;
    exports com.h1r0kuu.gameoflife.handlers;
    opens com.h1r0kuu.gameoflife.handlers to javafx.fxml;
}