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
    private TableColumn<Sale, String> movieTimeColumn;  // Declare movieTimeColumn


    private final SalesDatabase salesDatabase;

    public ViewSalesHistoryController() {
        // Instantiate SalesDatabase using the singleton pattern
        salesDatabase = SalesDatabase.getInstance();
    }

    @FXML
    public void initialize() {
        // Initialize the table columns and load sales history
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        showingTitleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShowingTitle()));
        saleTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSaleTime()));
        movieTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovieTime())); // Ensure this is correct
        numberOfTicketsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumberOfTickets())));

        // Set the items of the table view to reflect current sales history
        salesTable.setItems(salesDatabase.getSales());
    }

}
