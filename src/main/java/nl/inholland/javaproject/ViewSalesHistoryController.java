package nl.inholland.javaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ViewSalesHistoryController {
    @FXML
    private TableView<Sale> salesTable;

    @FXML
    private TableColumn<Sale, String> customerNameColumn;

    @FXML
    private TableColumn<Sale, String> showingTitleColumn;

    @FXML
    private TableColumn<Sale, String> saleTimeColumn;

    @FXML
    private TableColumn<Sale, String> numberOfTicketsColumn;

    @FXML
    private TableColumn<Sale, String> movieTimeColumn;

    private SalesDatabase salesDatabase;

    public void setDatabases(SalesDatabase salesDatabase) {
        this.salesDatabase = salesDatabase;
        initializeAfterDatabaseSet();  // Initialize only after database is set
    }

    @FXML
    public void initialize() {
        // Set up column factories for the sales table (this doesn’t depend on the database)
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        showingTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShowingTitle()));
        saleTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSaleTime()));
        movieTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovieTime()));
        numberOfTicketsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumberOfTickets())));
    }


    private void initializeAfterDatabaseSet() {
        // Logic that depends on the salesDatabase
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        showingTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShowingTitle()));
        saleTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSaleTime()));
        movieTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovieTime()));
        numberOfTicketsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumberOfTickets())));

        salesTable.setItems(salesDatabase.getSales());
    }
}
