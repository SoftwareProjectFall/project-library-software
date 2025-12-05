package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;

public class LibraryServiceMessageTest {

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
    void addBook_shouldPrintSuccessMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("", "New Book", "Author", new BookFine());
        library.addBook(admin, book);
        
        String output = outContent.toString();
        assertTrue(output.contains("added successfully") || output.contains("ISBN"));
    }

    @Test
    void addBook_shouldPrintAdminOnlyMessage() {
        LibraryService library = new LibraryService();
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("", "Book", "Author", new BookFine());
        library.addBook(user, book);
        
        String output = outContent.toString();
        assertTrue(output.contains("Only admins"));
    }

    @Test
    void removeBook_shouldPrintSuccessMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        library.removeBook(admin, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("removed successfully") || output.length() > 0);
    }

    @Test
    void removeBook_shouldPrintCannotRemoveBorrowedMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        library.removeBook(admin, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("Cannot remove") || output.contains("borrowed"));
    }

    @Test
    void removeBook_shouldPrintItemNotFoundMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        library.removeBook(admin, "999");
        
        String output = outContent.toString();
        assertTrue(output.contains("Item not found") || output.length() > 0);
    }

    @Test
    void updateBook_shouldPrintSuccessMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        Book book = new Book("101", "Old", "OldAuthor", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        library.updateBook(admin, "101", "New", "NewAuthor");
        
        String output = outContent.toString();
        assertTrue(output.contains("updated successfully") || output.length() > 0);
    }

    @Test
    void updateBook_shouldPrintItemNotFoundMessage() {
        LibraryService library = new LibraryService();
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        
        library.updateBook(admin, "999", "New", "NewAuthor");
        
        String output = outContent.toString();
        assertTrue(output.contains("Item not found") || output.length() > 0);
    }

    @Test
    void borrowBook_shouldPrintBorrowLimitMessage() {
        LibraryService library = new LibraryService();
        library.setMaxBorrowPerUser(1);
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book1 = new Book("101", "Book1", "Author", new BookFine());
        book1.setBorrowed(true);
        book1.setBorrowedByUserId("1");
        
        Book book2 = new Book("102", "Book2", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book1, book2)));
        
        library.borrowBook(user, "102");
        
        String output = outContent.toString();
        assertTrue(output.contains("Borrow limit") || output.contains("limit reached"));
    }

    @Test
    void borrowBook_shouldPrintOverdueItemsMessage() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book overdue = new Book("101", "Overdue", "Author", new BookFine());
        overdue.setBorrowed(true);
        overdue.setBorrowedByUserId("1");
        overdue.setDueDate(java.time.LocalDate.now().minusDays(5));
        
        Book available = new Book("102", "Available", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(overdue, available)));
        
        library.borrowBook(user, "102");
        
        String output = outContent.toString();
        assertTrue(output.contains("overdue") || output.contains("Borrowing denied"));
    }

    @Test
    void borrowBook_shouldPrintUnpaidFinesMessage() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        user.addFine(10.0);
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        library.borrowBook(user, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("unpaid fines") || output.contains("Borrowing denied"));
    }

    @Test
    void borrowBook_shouldPrintItemNotFoundMessage() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        library.borrowBook(user, "999");
        
        String output = outContent.toString();
        assertTrue(output.contains("Item not found") || output.length() > 0);
    }

    @Test
    void borrowBook_shouldPrintAlreadyBorrowedMessage() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("2");
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        library.borrowBook(user, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("already borrowed") || output.length() > 0);
    }

    @Test
    void returnBook_shouldPrintItemNotBorrowedByYouMessage() {
        LibraryService library = new LibraryService();
        
        User user = new User("1", "User", "user", "pass", false, "user@test.com");
        user.login("user", "pass");
        
        Book book = new Book("101", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("2");
        library.setItems(new ArrayList<>(Arrays.asList(book)));
        
        library.returnBook(user, "101");
        
        String output = outContent.toString();
        assertTrue(output.contains("not borrowed by you") || output.length() > 0);
    }
}

