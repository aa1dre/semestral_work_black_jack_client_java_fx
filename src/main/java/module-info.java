module com.aakhramchuk.clientfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.configuration2;
    requires org.apache.logging.log4j;
    requires java.desktop;

    opens com.aakhramchuk.clientfx to javafx.fxml;
    exports com.aakhramchuk.clientfx;
    exports com.aakhramchuk.clientfx.controllers;
    opens com.aakhramchuk.clientfx.controllers to javafx.fxml;
    exports com.aakhramchuk.clientfx.utils;
    opens com.aakhramchuk.clientfx.utils to javafx.fxml;
}