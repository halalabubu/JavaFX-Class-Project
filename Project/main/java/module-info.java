module src.c195classproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens src.c195classproject to javafx.fxml;
    exports src.c195classproject;
    exports helper;
    opens helper to javafx.fxml;
}