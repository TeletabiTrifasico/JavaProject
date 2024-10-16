package nl.inholland.javaproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    private final UserDatabase userDatabase = new UserDatabase();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userDatabase.validateUser(username, password)) {
            String role = userDatabase.getUserRole(username);

            // Close login window
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

            // Load main window with username and role
            MainWindowLoader.loadMainWindow(username, role);
        } else {
            errorMessage.setText("Invalid username or password!");
        }
    }
}
