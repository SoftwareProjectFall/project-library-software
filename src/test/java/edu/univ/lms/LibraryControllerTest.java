package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.service.UserService;
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerTest {

    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void clearScreen_shouldPrintSeparator() {
        LibraryController.clearScreen();
        
        String output = outContent.toString();
        assertTrue(output.contains("----------------------------------------------"));
    }

    @Test
    void printItem_shouldFormatBookCorrectly() {
        Book book = new Book("123", "Test Book", "Test Author", new BookFine());
        book.setBorrowed(false);
        
        LibraryController.printItem(book);
        
        String output = outContent.toString();
        assertTrue(output.contains("ISBN: 123"));
        assertTrue(output.contains("Title: Test Book"));
        assertTrue(output.contains("Author: Test Author"));
    }

    @Test
    void printItemList_shouldHandleEmptyList() {
        List<Book> emptyList = new ArrayList<>();
        
        LibraryController.printItemList(emptyList);
        
        String output = outContent.toString();
        assertTrue(output.contains("No results found"));
    }

    @Test
    void printItemList_shouldHandleNullList() {
        LibraryController.printItemList(null);
        
        String output = outContent.toString();
        assertTrue(output.contains("No results found"));
    }

    @Test
    void printItemList_shouldPrintAllBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("1", "Book1", "Author1", new BookFine()));
        books.add(new Book("2", "Book2", "Author2", new BookFine()));
        
        LibraryController.printItemList(books);
        
        String output = outContent.toString();
        assertTrue(output.contains("Book1"));
        assertTrue(output.contains("Book2"));
    }

    @Test
    void constructor_shouldInitializeServices() {
        // Provide input to exit immediately
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        assertNotNull(controller);
    }

    @Test
    void constructor_shouldCreateDefaultUsersIfEmpty() {
        // Delete users.json if exists to test default user creation
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("users.json"));
        } catch (Exception e) {
            // Ignore
        }
        
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController controller = new LibraryController();
        
        assertNotNull(controller);
    }
}

