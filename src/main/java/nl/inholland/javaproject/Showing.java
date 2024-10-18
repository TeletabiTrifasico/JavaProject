package nl.inholland.javaproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Showing implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String startTime;
    private final String endTime;
    private int availableSeats;
    private final List<String> soldSeats;

    public Showing(String title, String startTime, String endTime, int totalSeats) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availableSeats = totalSeats;
        this.soldSeats = new ArrayList<>();
    }

    // Getters and Setters
    public String getTitle() {
        return title;
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