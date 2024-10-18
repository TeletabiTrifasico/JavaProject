package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShowingDatabase {
    private static final String FILE_PATH = "showings.dat";  // File to store showings
    private final ObservableList<Showing> showings;  // ObservableList for UI binding

    public ShowingDatabase() {
        showings = FXCollections.observableArrayList();
        loadFromFile();  // Load from file

        // Add some initial data if the database is empty
        if (showings.isEmpty()) {
            showings.add(new Showing("Avengers", "2024-12-01 14:00", "2024-12-01 16:30", 72));
            showings.add(new Showing("Inception", "2024-12-01 17:00", "2024-12-01 19:30", 72));
            showings.add(new Showing("Titanic", "2024-12-01 20:00", "2024-12-01 23:00", 72));
        }
    }

    // Get the list of showings
    public ObservableList<Showing> getShowings() {
        return showings;
    }

    // Add a new showing and save the updated list to the file
    public void addShowing(Showing showing) {
        showings.add(showing);
        saveToFile();  // Save immediately after adding
    }

    // Update an existing showing
    public void updateShowing(Showing oldShowing, Showing newShowing) {
        int index = showings.indexOf(oldShowing);
        if (index != -1) {
            showings.set(index, newShowing);
            saveToFile();  // Save after updating
        }
    }

    // Delete a showing and save the updated list
    public void deleteShowing(Showing showing) {
        showings.remove(showing);
        saveToFile();  // Save after deletion
    }

    // Check if a showing can be deleted
    public boolean canDelete(Showing showing) {
        return showing.getSoldSeats().isEmpty();  // Can delete if no tickets have been sold
    }

    // Save the list of showings to a file
    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            List<Showing> serializableList = new ArrayList<>(showings);
            oos.writeObject(serializableList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the list of showings from the file
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Showing> loadedShowings = (List<Showing>) ois.readObject();
                showings.clear();  // Clear current list
                showings.addAll(loadedShowings);  // Add loaded data to ObservableList
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
