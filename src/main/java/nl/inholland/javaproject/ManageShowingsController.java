package nl.inholland.javaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageShowingsController {

    @FXML
    private TableView<Showing> showingsTable;

    @FXML
    private TableColumn<Showing, String> startColumn;

    @FXML
    private TableColumn<Showing, String> endColumn;

    @FXML
    private TableColumn<Showing, String> titleColumn;

    @FXML
    private TableColumn<Showing, String> seatsLeftColumn;

    private final ShowingDatabase showingDatabase;

    public ManageShowingsController() {
        // Get the singleton instance of ShowingDatabase
        showingDatabase = ShowingDatabase.getInstance();
    }

    @FXML
    public void initialize() {
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsLeftColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeatsLeftText()));

        showingsTable.setItems(showingDatabase.getShowings());
    }

    @FXML
    protected void onAddShowing() {
        // Logic to add a new showing
        ShowingDialog dialog = new ShowingDialog();
        Showing newShowing = dialog.showAndWait().orElse(null);
        if (newShowing != null) {
            showingDatabase.addShowing(newShowing);
            showingsTable.refresh();
        }
    }

    @FXML
    protected void onEditShowing() {
        Showing selectedShowing = showingsTable.getSelectionModel().getSelectedItem();
        if (selectedShowing != null) {
            ShowingDialog dialog = new ShowingDialog(selectedShowing);
            Showing updatedShowing = dialog.showAndWait().orElse(null);
            if (updatedShowing != null) {
                showingDatabase.updateShowing(selectedShowing, updatedShowing);
                showingsTable.refresh();
            }
        } else {
            showAlert("Please select a showing to edit.");
        }
    }

    @FXML
    protected void onDeleteShowing() {
        Showing selectedShowing = showingsTable.getSelectionModel().getSelectedItem();
        if (selectedShowing != null) {
            if (showingDatabase.canDelete(selectedShowing)) {
                showingDatabase.deleteShowing(selectedShowing);
                showingsTable.refresh();
            } else {
                showAlert("Cannot delete showing as tickets have been sold.");
            }
        } else {
            showAlert("Please select a showing to delete.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
