package nl.inholland.javaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    private Label roleLabel;

    @FXML
    private Label currentDateTimeLabel;

    @FXML
    private VBox contentArea;

    private String role;
    private SalesDatabase salesDatabase;
    private ShowingDatabase showingDatabase;

    public void setUserInfo(String username, String role) {
        welcomeText.setText("Welcome: " + username);
        roleLabel.setText("You are logged in as: " + role);
        this.role = role;

        // Display current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentDateTime = formatter.format(new Date());
        currentDateTimeLabel.setText("Current date and time: " + currentDateTime);
    }

    public void setDatabases(SalesDatabase salesDatabase, ShowingDatabase showingDatabase) {
        this.salesDatabase = salesDatabase;
        this.showingDatabase = showingDatabase;
    }

    @FXML
    protected void onManageShowingsClick() {
        if (!"admin".equals(role)) {
            showAccessDeniedAlert();
        } else {
            loadView("/nl/inholland/javaproject/manage-showings-view.fxml", "Manage Showings");
        }
    }

    @FXML
    protected void onViewSalesHistoryClick() {
        if (!"admin".equals(role)) {
            showAccessDeniedAlert();
        } else {
            loadView("/nl/inholland/javaproject/view-sales-history-view.fxml", "View Sales History");
        }
    }

    @FXML
    protected void onSellTicketsClick() {
        loadView("/nl/inholland/javaproject/sell-tickets-view.fxml", "Sell Tickets");
    }

    private void showAccessDeniedAlert() {
        createModalDialog("Access Denied", "Your access level is insufficient to view this section!");
    }

    private void showAlert(String message) {
        createModalDialog("Error", message);
    }

    private void loadView(String fxmlFile, String viewName) {
        contentArea.getChildren().clear(); // Clear the content area
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node view = fxmlLoader.load();

            // Get the controller instance
            Object controller = fxmlLoader.getController();

            // Pass the necessary databases before the view is displayed
            if (controller instanceof ManageShowingsController) {
                ManageShowingsController manageShowingsController = (ManageShowingsController) controller;
                manageShowingsController.setDatabases(showingDatabase);  // Pass ShowingDatabase
            } else if (controller instanceof SellTicketsController) {
                SellTicketsController sellTicketsController = (SellTicketsController) controller;
                sellTicketsController.setDatabases(salesDatabase, showingDatabase);  // Pass both databases
            } else if (controller instanceof ViewSalesHistoryController) {
                ViewSalesHistoryController viewSalesHistoryController = (ViewSalesHistoryController) controller;
                viewSalesHistoryController.setDatabases(salesDatabase);  // Pass SalesDatabase
            }

            // Add the view to the content area
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load " + viewName);
        }
    }

    public void createModalDialog(String title, String message) {
        Dialog<Void> errorDialog = new Dialog<>();
        errorDialog.setTitle(title);

        Label messageLabel = new Label(message);
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        errorDialog.getDialogPane().getButtonTypes().addAll(closeButton);
        errorDialog.getDialogPane().setContent(messageLabel);
        errorDialog.showAndWait();
    }
}
