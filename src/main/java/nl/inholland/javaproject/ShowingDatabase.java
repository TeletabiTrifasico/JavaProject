package nl.inholland.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ShowingDatabase {
    private static ShowingDatabase instance;
    private final ObservableList<Showing> showings;

    private ShowingDatabase() {
        showings = FXCollections.observableArrayList();
        // Hardcoded data
        showings.add(new Showing("Interstellar", "2024-10-30 18:00", "2024-10-15 20:00", 72));
        showings.add(new Showing("Corpse Bride", "2024-10-31 19:00", "2024-10-16 21:00", 72));
    }

    public static ShowingDatabase getInstance() {
        if (instance == null) {
            instance = new ShowingDatabase();
        }
        return instance;
    }

    public ObservableList<Showing> getShowings() {
        return showings;
    }

    public void addShowing(Showing showing) {
        showings.add(showing);
    }

    public void updateShowing(Showing oldShowing, Showing newShowing) {
        int index = showings.indexOf(oldShowing);
        if (index != -1) {
            showings.set(index, newShowing);
        }
    }

    public void deleteShowing(Showing showing) {
        showings.remove(showing);
    }

    public boolean canDelete(Showing showing) {
        // Checks if tickets have been sold for this showing
        return showing.getSoldSeats().isEmpty();
    }

    public void updateShowingWithSoldSeats(String showingTitle, List<String> soldSeats) {
        for (Showing showing : showings) {
            if (showing.getTitle().equals(showingTitle)) {
                showing.addSoldSeats(soldSeats);
                break;
            }
        }
    }
}
