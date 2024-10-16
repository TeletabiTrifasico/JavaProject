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

public class SellTicketsController extends MainController {

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
    @FXML

    private List<Seat> allSeats;
    private ObservableList<String> selectedSeats;
    private final ShowingDatabase showingDatabase;
    private final SalesDatabase salesDatabase;

    public SellTicketsController() {
        showingDatabase = ShowingDatabase.getInstance();
        salesDatabase = SalesDatabase.getInstance();
    }

    @FXML
    public void initialize() {
        List<Showing> futureShowings = new ArrayList<>(showingDatabase.getShowings().filtered(showing -> {
            LocalDateTime showingStartTime = LocalDateTime.parse(showing.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return showingStartTime.isAfter(LocalDateTime.now());
        }));
        futureShowings.sort(Comparator.comparing(showing -> LocalDateTime.parse(showing.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        ObservableList<Showing> sortedShowings = FXCollections.observableArrayList(futureShowings);
        showingsComboBox.setItems(sortedShowings);

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

        allSeats = new ArrayList<>();
        selectedSeats = FXCollections.observableArrayList();
        selectedSeatsList.setItems(selectedSeats);
        setupSeatsGrid();
        seatsGrid.setDisable(true);
        showingsComboBox.setOnAction(event -> handleShowingSelection()); // Trigger showing selection or deselection
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
            // A showing is selected, so we update seat availability
            availableSeatsLabel.setText("Available seats: " + selectedShowing.getAvailableSeats() + "/72");
            selectedShowingLabel.setText("Selected showing: " + selectedShowing.getTitle() + " - " + selectedShowing.getStartTime());
            selectedShowingLabel.setVisible(true);
            seatsGrid.setDisable(false);
            sellTicketsButton.setDisable(false);

            // Update the seat grid to reflect sold seats for the selected movie
            updateSeatAvailability(selectedShowing);
        } else {
            // No movie is selected, reset everything
            clearShowingSelection();
        }
    }

    private void clearShowingSelection() {
        // Fully reset the seat grid to its default state (all grey, all clickable)
        clearSeatGrid();

        // Clear labels and disable buttons
        availableSeatsLabel.setText("");  // Clear available seats label
        selectedShowingLabel.setText("");  // Clear the selected showing label
        selectedShowingLabel.setVisible(false);  // Hide the selected showing label
        seatsGrid.setDisable(true);  // Disable the seat grid until a movie is selected
        sellTicketsButton.setDisable(true);  // Disable the sell tickets button

        // Clear selected seats and customer input fields
        selectedSeats.clear();  // Clear any selected seats
        customerNameField.clear();  // Clear the customer name field
    }



    private void clearSeatGrid() {
        // Reset all seats to grey and make them clickable
        for (Seat seat : allSeats) {
            seat.getButton().setStyle("-fx-background-color: grey;");  // Set seat color to grey
            seat.getButton().setDisable(false);  // Make all seats clickable by default
            seat.setSelected(false);  // Ensure the internal state of the seat is cleared
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
        // Create a custom confirmation dialog with "Confirm" and "Cancel" buttons
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Ticket Sale");
        confirmationAlert.setHeaderText("Sale Confirmation");
        confirmationAlert.setContentText(
                "Customer: " + customerName + "\n" +
                        "Number of tickets: " + numSeats + "\n" +
                        "Showing: " + showing.getTitle() + " (" + showing.getStartTime() + ")\n\n" +
                        "Are you sure you want to proceed with the sale?"
        );

        // Add Confirm and Cancel buttons to the dialog
        ButtonType confirmButton = new ButtonType("Confirm");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Wait for the user's response
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                // Proceed with ticket sale
                processTicketSale(customerName, numSeats, showing);
            } else {
                // If "Cancel" is clicked, do nothing (no sale, no reset)
            }
        });
    }

    private void processTicketSale(String customerName, int numSeats, Showing showing) {
        // Create the ticket sale and add it to the database
        LocalDateTime saleDateTime = LocalDateTime.now();
        String movieTime = showing.getStartTime();

        // Ensure that the sale is only added once
        Sale sale = new Sale(customerName, showing.getTitle(), saleDateTime, movieTime, numSeats, new ArrayList<>(selectedSeats));
        salesDatabase.addSale(sale);

        // Reset form after successful sale
        resetAfterSale();

        // After resetting the form, make sure the movie has to be selected again
        showingsComboBox.setValue(null);  // Clear the movie selection ComboBox
        clearShowingSelection();  // Ensure all form elements are reset to initial state
    }


    private void updateSeatAvailability(Showing showing) {
        // Update the label to reflect the correct number of available seats
        availableSeatsLabel.setText("Available seats: " + showing.getAvailableSeats() + "/72");

        // Reset all seats to grey and clickable
        clearSeatGrid();

        // Mark the sold seats in the seat grid based on the current showing's soldSeats list
        for (Seat seat : allSeats) {
            String seatLabel = "Row " + (seat.getRow() + 1) + " / Seat " + (seat.getColumn() + 1);
            if (showing.getSoldSeats().contains(seatLabel)) {
                seat.getButton().setStyle("-fx-background-color: red;");  // Mark sold seats as red
                seat.getButton().setDisable(true);  // Disable sold seats (make them unclickable)
            }
        }
    }


    @FXML
    protected void onCancelClick() {  // Add new handler for Cancel button
        resetAfterSale();
    }

    private void resetAfterSale() {
        for (Seat seat : allSeats) {
            seat.setSelected(false);
            seat.getButton().setStyle("-fx-background-color: grey;");  // Reset all seats to grey
        }
        selectedSeats.clear();  // Clear selected seats
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