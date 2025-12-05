package edu.univ.lms.repository;

import edu.univ.lms.model.Book;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Book data persistence.
 * Handles saving and loading books from JSON files.
 */
public class BookRepository {

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

    /**
     * Saves a list of books to JSON file.
     */
    public void saveBooks(List<Book> books) {
        try (Writer writer = new FileWriter(ITEMS_FILE)) {
            gson.toJson(books, writer);
            System.out.println("Items saved.");
        } catch (Exception e) {
            System.out.println("Error saving items: " + e.getMessage());
        }
    }

    /**
     * Loads books from JSON file.
     * @return List of books, or empty list if file doesn't exist
     */
    public List<Book> loadBooks() {
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

