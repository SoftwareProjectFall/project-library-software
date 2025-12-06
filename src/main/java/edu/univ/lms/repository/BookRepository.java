package edu.univ.lms.repository;

import edu.univ.lms.model.Book;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for the persistence of {@link Book} objects.
 * <p>
 * Books are stored in a JSON file located under the <code>data/</code> directory.
 * This class handles:
 * <ul>
 *     <li>Creating the data folder if it does not exist</li>
 *     <li>Serializing books to JSON</li>
 *     <li>Deserializing books from JSON</li>
 *     <li>Restoring fine strategy objects after loading</li>
 * </ul>
 */
public class BookRepository {

    /** Path to the JSON file storing all book entries. */
    private static final String ITEMS_FILE = "data/items.json";

    // ---------------------------------------------------------
    // GSON Type Adapters for LocalDate
    // ---------------------------------------------------------

    /** Serializer for converting LocalDate → JSON String. */
    private static final JsonSerializer<LocalDate> localDateSerializer =
            (date, type, context) -> new JsonPrimitive(date.toString());

    /** Deserializer for converting JSON String → LocalDate. */
    private static final JsonDeserializer<LocalDate> localDateDeserializer =
            (json, type, context) -> LocalDate.parse(json.getAsString());

    /** GSON instance configured with LocalDate support. */
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, localDateSerializer)
            .registerTypeAdapter(LocalDate.class, localDateDeserializer)
            .create();

    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------

    /**
     * Creates a new repository and ensures the data folder exists.
     */
    public BookRepository() {
        ensureDataFolder();
    }

    /**
     * Ensures that the <code>data/</code> directory exists.
     * This method is invoked once at initialization.
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
    // Save
    // ---------------------------------------------------------

    /**
     * Saves the list of books into a formatted JSON file.
     *
     * @param books the list of {@link Book} objects to save
     */
    public void saveBooks(List<Book> books) {
        try (Writer writer = new FileWriter(ITEMS_FILE)) {
            gson.toJson(books, writer);
            System.out.println("Items saved.");
        } catch (Exception e) {
            System.out.println("Error saving items: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // Load
    // ---------------------------------------------------------

    /**
     * Loads the list of books from the JSON file.
     * <p>
     * If the file does not exist or if an error occurs,
     * an empty list is returned.
     * <p>
     * After loading, each book's fine strategy is reconstructed
     * using {@link Book#rebuildFineStrategy()}.
     *
     * @return a list of books restored from storage
     */
    public List<Book> loadBooks() {
        try (Reader reader = new FileReader(ITEMS_FILE)) {

            Book[] array = gson.fromJson(reader, Book[].class);
            List<Book> list = new ArrayList<>();

            if (array != null) {
                for (Book book : array) {
                    // Rebuild strategy object after JSON load
                    book.rebuildFineStrategy();
                    list.add(book);
                }
            }

            return list;

        } catch (FileNotFoundException e) {
            System.out.println("data/items.json not found, starting empty.");
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Error loading items: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
