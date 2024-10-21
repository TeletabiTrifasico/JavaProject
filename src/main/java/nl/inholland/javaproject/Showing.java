package nl.inholland.javaproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Showing implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String startTime;
    private final String endTime;
    private final List<String> soldSeats;
    private int availableSeats;
    private boolean ageCheckRequired;  // New variable for age check

    public Showing(String title, String startTime, String endTime, int totalSeats, boolean ageCheckRequired) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availableSeats = totalSeats;
        this.soldSeats = new ArrayList<>();
        this.ageCheckRequired = ageCheckRequired;  // Initialize in constructor

    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    // Getter and Setter for the age check
    public boolean isAgeCheckRequired() {
        return ageCheckRequired;
    }

    public void setAgeCheckRequired(boolean ageCheckRequired) {
        this.ageCheckRequired = ageCheckRequired;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public List<String> getSoldSeats() {
        return soldSeats;
    }

    // Add sold seats and update available seats count
    public void addSoldSeats(List<String> seats) {
        soldSeats.addAll(seats);
        availableSeats -= seats.size();
    }

    public String getSeatsLeftText() {
        return availableSeats + "/72";
    }
}