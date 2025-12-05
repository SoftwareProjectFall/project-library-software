package edu.univ.lms.repository;

import edu.univ.lms.model.User;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for User data persistence.
 * Handles saving and loading users from JSON files.
 */
public class UserRepository {

    private static final String USERS_FILE = "users.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Saves a list of users to JSON file.
     */
    public void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("Users saved.");
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Loads users from JSON file.
     * @return List of users, or empty list if file doesn't exist
     */
    public List<User> loadUsers() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            User[] array = gson.fromJson(reader, User[].class);

            List<User> list = new ArrayList<User>();

            if (array != null) {
                for (User u : array) {
                    list.add(u);
                }
            }
            return list;

        } catch (FileNotFoundException e) {
            System.out.println("users.json not found, starting empty.");
            return new ArrayList<User>();
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
            return new ArrayList<User>();
        }
    }
}

