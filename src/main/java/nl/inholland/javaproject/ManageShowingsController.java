package nl.inholland.javaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    @FXML
    private Label errorMessageLabel;

    private ShowingDatabase showingDatabase;

    public void setDatabases(ShowingDatabase showingDatabase) {
        this.showingDatabase = showingDatabase;
        initializeAfterDatabaseSet();  // Initialize only after database is set
    }

    @FXML
    public void initialize() {
        // Set up column factories (these don’t depend on the database)
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsLeftColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeatsLeftText()));
    }

    private void initializeAfterDatabaseSet() {
        // Logic that depends on the showingDatabase
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        seatsLeftColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeatsLeftText()));

        showingsTable.setItems(showingDatabase.getShowings());
    }

    @FXML
    protected void onAddShowing() {
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

    @FXML
    protected void onExportShowings() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Showings to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Show save dialog
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            exportShowingsToCSV(file);
        }
    }

    private void exportShowingsToCSV(File file) {
        List<Showing> showings = showingDatabase.getShowings();

        try (FileWriter writer = new FileWriter(file)) {
            // Write the CSV headers
            writer.write("start datetime,end datetime,movie title,seats left\n");

            // Write the data for each showing
            for (Showing showing : showings) {
                writer.write(String.format("%s,%s,%s,%d\n",
                        showing.getStartTime(),
                        showing.getEndTime(),
                        showing.getTitle(),
                        showing.getAvailableSeats()));
            }

            System.out.println("Showings exported successfully to CSV.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to export showings to CSV.");
        }
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    private void showAlert(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }
}
