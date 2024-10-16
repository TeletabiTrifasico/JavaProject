package nl.inholland.javaproject;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class Showing {
    private final SimpleStringProperty title;
    private final SimpleStringProperty startTime;
    private final SimpleStringProperty endTime;
    private final SimpleIntegerProperty availableSeats;
    private final ObservableList<String> soldSeats;

    public Showing(String title, String startTime, String endTime, int totalSeats) {
        this.title = new SimpleStringProperty(title);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
        this.availableSeats = new SimpleIntegerProperty(totalSeats);
        this.soldSeats = FXCollections.observableArrayList();
    }

    public String getTitle() {
        return title.get();
    }

    public String getStartTime() {
        return startTime.get();
    }

    public String getEndTime() {
        return endTime.get();
    }

    public int getAvailableSeats() {
        return availableSeats.get();
    }

    public ObservableList<String> getSoldSeats() {
        return soldSeats;
    }

    public void addSoldSeats(List<String> seats) {
        // Ensure that seats are added only once
        soldSeats.addAll(seats);
        availableSeats.set(availableSeats.get() - seats.size());
    }

    public String getSeatsLeftText() {
        return availableSeats.get() + "/72";
    }
}
