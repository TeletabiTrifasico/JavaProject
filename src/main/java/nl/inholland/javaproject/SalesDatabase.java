package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SalesDatabase {
    private final ObservableList<Sale> sales;
    private static final String FILE_PATH = "sales.dat";  // File path for sales database

    public SalesDatabase() {
        sales = FXCollections.observableArrayList();
        loadFromFile();
    }

    public ObservableList<Sale> getSales() {
        return sales;
    }

    public void addSale(Sale sale) {
        sales.add(sale);
        saveToFile();  // Save changes immediately after adding
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(new ArrayList<>(sales));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Sale> loadedSales = (List<Sale>) ois.readObject();
                sales.addAll(loadedSales);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
