package nl.inholland.javaproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowLoader {

    public static void loadMainWindow(String username, String role) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindowLoader.class.getResource("main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 780, 550);

            MainController controller = fxmlLoader.getController();
            controller.setUserInfo(username, role);

            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
