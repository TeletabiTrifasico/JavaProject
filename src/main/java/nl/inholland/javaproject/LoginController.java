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
    private SalesDatabase salesDatabase;
    private ShowingDatabase showingDatabase;

    public void setDatabases(SalesDatabase salesDatabase, ShowingDatabase showingDatabase) {
        this.salesDatabase = salesDatabase;
        this.showingDatabase = showingDatabase;
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (userDatabase.validateUser(username, password)) {
            String role = userDatabase.getUserRole(username);

            // Close login window
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();

            // Load main window with username, role, and databases
            MainWindowLoader.loadMainWindow(username, role, salesDatabase, showingDatabase);
        } else {
            errorMessage.setText("Invalid username or password!");
        }
    }
}
