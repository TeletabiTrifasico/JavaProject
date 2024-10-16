package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class SalesDatabase {
    private static SalesDatabase instance;
    private ObservableList<Sale> sales;

    private SalesDatabase() {
        sales = FXCollections.observableArrayList();
    }

    public static SalesDatabase getInstance() {
        if (instance == null) {
            instance = new SalesDatabase();
        }
        return instance;
    }

    public ObservableList<Sale> getSales() {
        return sales;
    }

    public void addSale(Sale sale) {
        sales.add(sale);
        // Update the showing to mark these seats as sold
        ShowingDatabase.getInstance().updateShowingWithSoldSeats(sale.getShowingTitle(), sale.getSoldSeats());
    }
}
