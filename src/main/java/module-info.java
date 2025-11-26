module com.mycompany.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.mycompany.library to javafx.fxml;
    exports com.mycompany.library;
}
