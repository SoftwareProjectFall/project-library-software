package edu.univ.lms;

import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaveLoadManager {

    private static final String USERS_FILE = "users.json";
    private static final String ITEMS_FILE = "items.json";

    // ------- LocalDate Serializer -------
    private static final JsonSerializer<LocalDate> localDateSerializer = new JsonSerializer<LocalDate>() {
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString()); // "2025-01-01"
        }
    };

    // ------- LocalDate Deserializer -------
    private static final JsonDeserializer<LocalDate> localDateDeserializer = new JsonDeserializer<LocalDate>() {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString());
        }
    };

    // ------- GSON with LocalDate support -------
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, localDateSerializer)
            .registerTypeAdapter(LocalDate.class, localDateDeserializer)
            .create();


    // =============================== SAVE ===============================

    public static void saveUsers(List<User> users) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("Users saved.");
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static void saveItems(List<Book> books) {
        try (Writer writer = new FileWriter(ITEMS_FILE)) {
            gson.toJson(books, writer);
            System.out.println("Items saved.");
        } catch (Exception e) {
            System.out.println("Error saving items: " + e.getMessage());
        }
    }


    // =============================== LOAD ===============================

    public static List<User> loadUsers() {
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


    public static List<Book> loadItems() {
        try (Reader reader = new FileReader(ITEMS_FILE)) {

            // Load as array (safer than TypeToken)
            Book[] array = gson.fromJson(reader, Book[].class);

            List<Book> list = new ArrayList<Book>();

            if (array != null) {
                for (Book b : array) {

                    // Rebuild strategy because fineStrategy:{} cannot be reconstructed
                    b.rebuildFineStrategy();

                    list.add(b);
                }
            }

            return list;

        } catch (FileNotFoundException e) {
            System.out.println("items.json not found, starting empty.");
            return new ArrayList<Book>();
        } catch (Exception e) {
            System.out.println("Error loading items: " + e.getMessage());
            return new ArrayList<Book>();
        }
    }
}
