package nl.inholland.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CinemaApplication extends Application {
    private SalesDatabase salesDatabase;
    private ShowingDatabase showingDatabase;

    @Override
    public void start(Stage stage) throws IOException {
        salesDatabase = new SalesDatabase();
        showingDatabase = new ShowingDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(CinemaApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        LoginController loginController = fxmlLoader.getController();
        loginController.setDatabases(salesDatabase, showingDatabase);  // Pass the databases to the login controller

        stage.setOnCloseRequest(event -> {
            showingDatabase.saveToFile();
            salesDatabase.saveToFile();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
