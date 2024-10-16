package nl.inholland.javaproject;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, String> roles = new HashMap<>();

    public UserDatabase() {
        // Admin user
        users.put("Jorge", "adminpass");
        roles.put("Jorge", "admin");

        // Sales user
        users.put("Raquel", "salespass");
        roles.put("Raquel", "sales");
    }

    public boolean validateUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public String getUserRole(String username) {
        return roles.get(username);
    }
}
