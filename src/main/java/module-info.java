module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.common;
    requires org.jetbrains.annotations;
    requires junit;

    opens org.openjfx to javafx.fxml;
    opens org.openjfx.controllers to javafx.fxml;
    exports org.openjfx;
    exports nl.liacs.watch.protocol.server;
}
