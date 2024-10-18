package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.IntStream;

public class ShowingDialog extends Dialog<Showing> {
    private TextField titleField;
    private DatePicker startDatePicker;
    private ComboBox<String> startTimeComboBox;
    private DatePicker endDatePicker;
    private ComboBox<String> endTimeComboBox;

    private ButtonType saveButtonType;  // Declare saveButtonType

    public ShowingDialog() {
        this(null);
    }

    public ShowingDialog(Showing showing) {
        initializeFields();
        // Set items for time pickers directly from generateTimeOptions
        startTimeComboBox.setItems(FXCollections.observableArrayList(generateTimeOptions()));
        endTimeComboBox.setItems(FXCollections.observableArrayList(generateTimeOptions()));

        if (showing != null) {
            populateFieldsForEditing(showing);
        }

        setupDialogLayout();
        setupSaveButton();
        setupAutoEndTimeFeature();  // New method to autofill end date/time
    }

    // Generate time options in 30-minute intervals for the time pickers
    private static java.util.List<String> generateTimeOptions() {
        return IntStream.range(0, 24 * 2)  // 24 hours in 30-minute intervals
                .mapToObj(i -> String.format("%02d:%02d", i / 2, (i % 2) * 30))
                .toList();
    }

    // Initialize fields
    private void initializeFields() {
        titleField = new TextField();
        startDatePicker = new DatePicker();
        endDatePicker = new DatePicker();
        startTimeComboBox = new ComboBox<>();
        endTimeComboBox = new ComboBox<>();

        // Set prompt text for date and time fields
        startDatePicker.setPromptText("Date: DD-MM-YY");
        endDatePicker.setPromptText("Date: DD-MM-YY");
        startTimeComboBox.setPromptText("Time: HH:MM");
        endTimeComboBox.setPromptText("Time: HH:MM");
    }

    // Populate fields for editing
    private void populateFieldsForEditing(Showing showing) {
        titleField.setText(showing.getTitle());
        startDatePicker.setValue(LocalDate.parse(showing.getStartTime().split(" ")[0]));
        startTimeComboBox.setValue(showing.getStartTime().split(" ")[1]);
        endDatePicker.setValue(LocalDate.parse(showing.getEndTime().split(" ")[0]));
        endTimeComboBox.setValue(showing.getEndTime().split(" ")[1]);
    }

    // Set up the dialog layout
    private void setupDialogLayout() {
        VBox vbox = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Start Date:"), startDatePicker,
                new Label("Start Time:"), startTimeComboBox,
                new Label("End Date:"), endDatePicker,
                new Label("End Time:"), endTimeComboBox);
        getDialogPane().setContent(vbox);

        // Ensure the saveButtonType is added here before using it in lookupButton()
        saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);  // Assign to class field
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    }

    // Set up the save button behavior and validation
    private void setupSaveButton() {
        final Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);  // Use saveButtonType

        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!isFormValid()) {
                event.consume(); // Prevent the dialog from closing if validation fails
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return createShowingFromInput();
            }
            return null;
        });
    }

    // Create Showing object from input data
    private Showing createShowingFromInput() {
        String startTime = formatDateTime(startDatePicker.getValue(), startTimeComboBox.getValue());
        String endTime = formatDateTime(endDatePicker.getValue(), endTimeComboBox.getValue());

        return new Showing(
                titleField.getText(),
                startTime,
                endTime,
                72  // Default seats count
        );
    }

    // Format date and time into a string
    private String formatDateTime(LocalDate date, String time) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + time;
    }

    // Validate the form fields
    private boolean isFormValid() {
        if (titleField.getText().isEmpty() || startDatePicker.getValue() == null || startTimeComboBox.getValue() == null || endDatePicker.getValue() == null || endTimeComboBox.getValue() == null) {
            showErrorAlert("All fields must be filled.");
            return false;
        }

        // Validate date and time formats
        if (!isValidDate(startDatePicker.getValue()) || !isValidDate(endDatePicker.getValue())) {
            showErrorAlert("Date must be in the format DD-MM-YY.");
            return false;
        }
        if (!isValidTime(startTimeComboBox.getValue()) || !isValidTime(endTimeComboBox.getValue())) {
            showErrorAlert("Time must be in the format HH:MM.");
            return false;
        }

        return true;
    }

    // Validate that the date is in the correct format
    private boolean isValidDate(LocalDate date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
            date.format(formatter);  // Try formatting to check validity
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Validate that the time is in the correct format
    private boolean isValidTime(String time) {
        return time != null && time.matches("\\d{2}:\\d{2}");  // Ensure time is HH:MM format
    }

    // Autofill end date and time
    private void setupAutoEndTimeFeature() {
        // Listen for changes in the start date picker and update the end date to match
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                endDatePicker.setValue(newValue);  // Autofill the end date with the same date as start date
            }
        });

        // Listen for changes in the start time picker and update the end time
        startTimeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String[] timeParts = newValue.split(":");
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);

                // Add 2 hours to the selected start time (the standard duration of a movie)
                LocalTime newEndTime = LocalTime.of(hours, minutes).plusHours(2);

                // Ensure the end time picker shows the updated time
                endTimeComboBox.setValue(String.format("%02d:%02d", newEndTime.getHour(), newEndTime.getMinute()));
            }
        });
    }

    // Show an error alert for validation failures
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}