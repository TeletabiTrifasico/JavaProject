package nl.inholland.javaproject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String customerName;
    private final String showingTitle;
    private final String saleTime;
    private final String movieTime;
    private final int numberOfTickets;

    public Sale(String customerName, String showingTitle, LocalDateTime saleDateTime, String movieTime, int numberOfTickets) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.saleTime = saleDateTime.format(formatter);
        this.customerName = customerName;
        this.showingTitle = showingTitle;
        this.movieTime = movieTime;
        this.numberOfTickets = numberOfTickets;
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
        return movieTime;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

}
