package edu.univ.lms.repository;

import edu.univ.lms.model.User;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for persisting and loading {@link User} data.
 * <p>
 * All users are stored in <code>data/users.json</code> in a readable
 * JSON format using GSON. The repository ensures the data directory
 * exists and provides safe load/save operations.
 */
public class UserRepository {

    /** Path to the JSON file storing all user accounts. */
    private static final String USERS_FILE = "data/users.json";

    /** GSON instance used for serialization and deserialization. */
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Constructs a new {@code UserRepository} and ensures the data directory exists.
     */
    public UserRepository() {
        ensureDataFolder();
    }

    /**
     * Ensures that the <code>data/</code> directory exists before any file access.
     * <p>
     * Creates the directory if it does not already exist.
     * Prints feedback indicating whether creation succeeded or failed.
     */
    private void ensureDataFolder() {
        File dir = new File("data");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Created data folder.");
            } else {
                System.out.println("Could not create data folder.");
            }
        }
    }

    // ---------------------------------------------------------
    // Save Users
    // ---------------------------------------------------------

    /**
     * Saves the provided list of users to a JSON file.
     * <p>
     * This operation overwrites any existing user file. In case of errors,
     * the exception message is printed, and the application continues running.
     *
     * @param users list of {@link User} objects to be serialized
     */
    public void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("Users saved to: " + new File(USERS_FILE).getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // Load Users
    // ---------------------------------------------------------

    /**
     * Loads all user accounts stored in the JSON file.
     * <p>
     * This method performs several safety checks:
     * <ul>
     *     <li>If the file does not exist, an empty list is returned.</li>
     *     <li>If deserialization fails, an empty list is returned.</li>
     * </ul>
     * <p>
     * The method prints diagnostic information, including the absolute
     * file path being accessed, which helps with debugging.
     *
     * @return list of restored {@link User} objects; never {@code null}
     */
    public List<User> loadUsers() {
        File file = new File(USERS_FILE);
        System.out.println("Trying to load users from: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("data/users.json not found, starting empty.");
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {

            User[] array = gson.fromJson(reader, User[].class);
            List<User> list = new ArrayList<>();

            if (array != null) {
                for (User user : array) {
                    list.add(user);
                }
            }

            return list;

        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
