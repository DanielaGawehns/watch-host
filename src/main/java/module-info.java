module org.openjfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.common;
    requires org.jetbrains.annotations;

    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}
