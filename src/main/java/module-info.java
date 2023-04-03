module com.h1r0kuu.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.h1r0kuu.gameoflife to javafx.fxml;
    exports com.h1r0kuu.gameoflife;
}