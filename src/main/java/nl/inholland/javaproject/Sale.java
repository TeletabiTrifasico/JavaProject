package nl.inholland.javaproject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class Sale {
    private final String customerName;
    private final String showingTitle;
    private final String saleTime; // Current time when sale was made
    private final String movieTime; // Date and time of the showing
    private final int numberOfTickets;
    private final List<String> soldSeats;

    public Sale(String customerName, String showingTitle, LocalDateTime saleDateTime, String movieTime, int numberOfTickets, List<String> soldSeats) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.saleTime = saleDateTime.format(formatter);  // Store current date/time
        this.customerName = customerName;
        this.showingTitle = showingTitle;
        this.movieTime = movieTime; // Make sure this is set correctly
        this.numberOfTickets = numberOfTickets;
        this.soldSeats = soldSeats;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getShowingTitle() {
        return showingTitle;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public String getMovieTime() {
        return movieTime; // Ensure this is properly returned
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public List<String> getSoldSeats() {
        return soldSeats;
    }
}
