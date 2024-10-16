module com.example.javaproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens nl.inholland.javaproject to javafx.fxml;
    exports nl.inholland.javaproject;
}