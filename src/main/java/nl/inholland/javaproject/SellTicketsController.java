package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SellTicketsController {

    @FXML
    private ComboBox<Showing> showingsComboBox;
    @FXML
    private Label availableSeatsLabel;
    @FXML
    private Label selectedShowingLabel;
    @FXML
    private GridPane seatsGrid;
    @FXML
    private ListView<String> selectedSeatsList;
    @FXML
    private TextField customerNameField;
    @FXML
    private Button sellTicketsButton;

    private List<Seat> allSeats;
    private ObservableList<String> selectedSeats;
    private ShowingDatabase showingDatabase;
    private SalesDatabase salesDatabase;

    public SellTicketsController() {
    }

    public void setDatabases(SalesDatabase salesDatabase, ShowingDatabase showingDatabase) {
        this.salesDatabase = salesDatabase;
        this.showingDatabase = showingDatabase;
        initializeAfterDatabaseSet();  // Call initialization after databases are set
    }

    @FXML
    public void initialize() {
        allSeats = new ArrayList<>();
        selectedSeats = FXCollections.observableArrayList();
        selectedSeatsList.setItems(selectedSeats);
        setupSeatsGrid();
        seatsGrid.setDisable(true); // Initially disable the grid until a showing is selected
    }


    private void initializeAfterDatabaseSet() {
        setupShowingComboBox();
    }

    private void setupShowingComboBox() {
        ObservableList<Showing> sortedShowings = getFutureSortedShowings();
        showingsComboBox.setItems(sortedShowings);
        setupShowingComboBoxConverter();
        showingsComboBox.setOnAction(event -> handleShowingSelection());
    }

    private ObservableList<Showing> getFutureSortedShowings() {
        List<Showing> futureShowings = new ArrayList<>(showingDatabase.getShowings().filtered(showing -> {
            LocalDateTime showingStartTime = LocalDateTime.parse(showing.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return showingStartTime.isAfter(LocalDateTime.now());
        }));
        futureShowings.sort(Comparator.comparing(showing -> LocalDateTime.parse(showing.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        return FXCollections.observableArrayList(futureShowings);
    }

    private void setupShowingComboBoxConverter() {
        showingsComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Showing showing) {
                return showing.getTitle() + " (" + showing.getStartTime() + ")";
            }

            @Override
            public Showing fromString(String string) {
                return null;
            }
        });
    }

    private void setupSeatsGrid() {
        int rows = 6;
        int cols = 12;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button seatButton = new Button(String.valueOf(col + 1));
                seatButton.setMinSize(30, 30);
                seatButton.setStyle("-fx-background-color: grey;");

                Seat seat = new Seat(row, col, seatButton);
                allSeats.add(seat);

                seatButton.setOnAction(event -> handleSeatSelection(seat));
                seatsGrid.add(seatButton, col, row);
            }
        }
    }

    private void handleShowingSelection() {
        Showing selectedShowing = showingsComboBox.getValue();

        if (selectedShowing != null) {
            clearSeatGrid();

            availableSeatsLabel.setText("Available seats: " + selectedShowing.getAvailableSeats() + "/72");
            availableSeatsLabel.setVisible(true);
            selectedShowingLabel.setText("Selected showing: " + selectedShowing.getTitle() + " - " + selectedShowing.getStartTime());
            selectedShowingLabel.setVisible(true);
            seatsGrid.setDisable(false);
            sellTicketsButton.setDisable(false);

            updateSeatAvailability(selectedShowing);
        } else {
            clearShowingSelection();
        }
    }

    private void clearShowingSelection() {
        clearSeatGrid();
        availableSeatsLabel.setText("");
        availableSeatsLabel.setVisible(false);
        selectedShowingLabel.setText("");
        selectedShowingLabel.setVisible(false);
        seatsGrid.setDisable(true);
        sellTicketsButton.setDisable(true);
        selectedSeats.clear();
        customerNameField.clear();
    }

    private void clearSeatGrid() {
        for (Seat seat : allSeats) {
            seat.getButton().setStyle("-fx-background-color: grey;");
            seat.getButton().setDisable(false);
            seat.setSelected(false);
        }
    }

    private void handleSeatSelection(Seat seat) {
        String seatLabel = "Row " + (seat.getRow() + 1) + " / Seat " + (seat.getColumn() + 1);
        if (seat.isSelected()) {
            seat.setSelected(false);
            seat.getButton().setStyle("-fx-background-color: grey;");
            selectedSeats.remove(seatLabel);
        } else {
            seat.setSelected(true);
            seat.getButton().setStyle("-fx-background-color: green;");
            selectedSeats.add(seatLabel);
        }
    }

    @FXML
    protected void onSellTicketsClick() {
        String customerName = customerNameField.getText();
        if (customerName.isEmpty()) {
            createModalDialog("Input Error", "Customer name is required.");
            return;
        }
        if (selectedSeats.isEmpty()) {
            createModalDialog("Selection Error", "Please select at least one seat.");
            return;
        }
        Showing selectedShowing = showingsComboBox.getValue();
        int numSeats = selectedSeats.size();
        showConfirmationAlert(customerName, numSeats, selectedShowing);
    }

    private void showConfirmationAlert(String customerName, int numSeats, Showing showing) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Ticket Sale");
        confirmationAlert.setHeaderText("Sale Confirmation");
        confirmationAlert.setContentText(
                "Customer: " + customerName + "\n" +
                        "Number of tickets: " + numSeats + "\n" +
                        "Showing: " + showing.getTitle() + " (" + showing.getStartTime() + ")\n\n" +
                        "Are you sure you want to proceed with the sale?"
        );

        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                processTicketSale(customerName, numSeats, showing);
            }
        });
    }

    private void processTicketSale(String customerName, int numSeats, Showing showing) {
        LocalDateTime saleDateTime = LocalDateTime.now();
        String movieTime = showing.getStartTime();

        Sale sale = new Sale(customerName, showing.getTitle(), saleDateTime, movieTime, numSeats);
        salesDatabase.addSale(sale);

        showing.addSoldSeats(new ArrayList<>(selectedSeats));

        showingDatabase.saveToFile();
        resetAfterSale();
        showingsComboBox.setValue(null);
        clearShowingSelection();
    }

    private void updateSeatAvailability(Showing showing) {
        availableSeatsLabel.setText("Available seats: " + showing.getAvailableSeats() + "/72");

        clearSeatGrid();

        for (Seat seat : allSeats) {
            String seatLabel = "Row " + (seat.getRow() + 1) + " / Seat " + (seat.getColumn() + 1);
            if (showing.getSoldSeats().contains(seatLabel)) {
                seat.getButton().setStyle("-fx-background-color: red;");
                seat.getButton().setDisable(true);
            }
        }
    }

    private void resetAfterSale() {
        for (Seat seat : allSeats) {
            seat.setSelected(false);
            seat.getButton().setStyle("-fx-background-color: grey;");
        }
        selectedSeats.clear();
    }

    private void createModalDialog(String title, String message) {
        Dialog<Void> errorDialog = new Dialog<>();
        errorDialog.setTitle(title);

        Label messageLabel = new Label(message);
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        errorDialog.getDialogPane().getButtonTypes().addAll(closeButton);
        errorDialog.getDialogPane().setContent(messageLabel);
        errorDialog.showAndWait();
    }

    private static class Seat {
        private final int row;
        private final int column;
        private final Button button;
        private boolean selected;

        public Seat(int row, int column, Button button) {
            this.row = row;
            this.column = column;
            this.button = button;
            this.selected = false;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public Button getButton() {
            return button;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
