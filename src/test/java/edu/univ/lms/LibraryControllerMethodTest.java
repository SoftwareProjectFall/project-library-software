package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;
import edu.univ.lms.model.Book;
import edu.univ.lms.strategy.BookFine;

public class LibraryControllerMethodTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void pressEnterToContinue_shouldWaitForInput() {
        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        
        LibraryController.pressEnterToContinue(scanner);
        
        String output = outContent.toString();
        assertTrue(output.contains("Press Enter to continue"));
    }

    @Test
    void printItem_shouldHandleBorrowedBook() {
        Book book = new Book("123", "Test Book", "Test Author", new BookFine());
        book.setBorrowed(true);
        
        LibraryController.printItem(book);
        
        String output = outContent.toString();
        assertTrue(output.contains("123"));
        assertTrue(output.contains("Test Book"));
        assertTrue(output.contains("true"));
    }

    @Test
    void printItem_shouldHandleAvailableBook() {
        Book book = new Book("456", "Available Book", "Author", new BookFine());
        book.setBorrowed(false);
        
        LibraryController.printItem(book);
        
        String output = outContent.toString();
        assertTrue(output.contains("456"));
        assertTrue(output.contains("Available Book"));
        assertTrue(output.contains("false"));
    }

    @Test
    void printItemList_shouldHandleSingleBook() {
        java.util.List<Book> books = new java.util.ArrayList<>();
        books.add(new Book("1", "Book1", "Author1", new BookFine()));
        
        LibraryController.printItemList(books);
        
        String output = outContent.toString();
        assertTrue(output.contains("Book1"));
    }

    @Test
    void printItemList_shouldHandleMultipleBooks() {
        java.util.List<Book> books = new java.util.ArrayList<>();
        books.add(new Book("1", "Book1", "Author1", new BookFine()));
        books.add(new Book("2", "Book2", "Author2", new BookFine()));
        books.add(new Book("3", "Book3", "Author3", new BookFine()));
        
        LibraryController.printItemList(books);
        
        String output = outContent.toString();
        assertTrue(output.contains("Book1"));
        assertTrue(output.contains("Book2"));
        assertTrue(output.contains("Book3"));
    }
}

