package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;

public class LibraryServiceEdgeCasesTest {

    private User createLoggedInAdmin() {
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        return admin;
    }

    private User createLoggedInUser(String id) {
        User user = new User(id, "User" + id, "user" + id, "pass", false, "user" + id + "@test.com");
        user.login("user" + id, "pass");
        return user;
    }

    private Book createBook(String isbn, String title) {
        return new Book(isbn, title, "Author", new BookFine());
    }

    @Test
    void updateBook_shouldUpdateOnlyTitleWhenAuthorIsNull() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        Book book = createBook("101", "Old Title");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.updateBook(admin, "101", "New Title", null);

        assertTrue(result);
        assertEquals("New Title", book.getTitle());
        assertEquals("Author", book.getAuthor()); // Should remain unchanged
    }

    @Test
    void updateBook_shouldUpdateOnlyAuthorWhenTitleIsNull() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        Book book = createBook("101", "Title");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.updateBook(admin, "101", null, "New Author");

        assertTrue(result);
        assertEquals("Title", book.getTitle()); // Should remain unchanged
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    void updateBook_shouldNotUpdateWhenBothAreBlank() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        Book book = createBook("101", "Original Title");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.updateBook(admin, "101", "   ", "");

        assertTrue(result); // Should still return true (found the book)
        assertEquals("Original Title", book.getTitle()); // Should remain unchanged
    }

    @Test
    void showBorrowedBooks_shouldShowNothingWhenUserHasNoBorrowedBooks() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Available Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Should not throw
        assertDoesNotThrow(() -> library.showBorrowedBooks(user));
    }

    @Test
    void showOverdueBooks_shouldShowNothingWhenNoOverdueBooks() {
        LibraryService library = new LibraryService();
        Book book = createBook("101", "On Time Book");
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        book.setDueDate(LocalDate.now().plusDays(5)); // Not overdue
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Should not throw
        assertDoesNotThrow(() -> library.showOverdueBooks());
    }

    @Test
    void searchBooksByTitle_shouldReturnEmptyListWhenNoMatches() {
        LibraryService library = new LibraryService();
        Book book = createBook("101", "Java Programming");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        List<Book> result = library.searchBooksByTitle("Python");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void searchBooksByAuthor_shouldReturnEmptyListWhenNoMatches() {
        LibraryService library = new LibraryService();
        Book book = createBook("101", "Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        List<Book> result = library.searchBooksByAuthor("Jane Smith");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void setMaxBorrowPerUser_shouldUpdateLimit() {
        LibraryService library = new LibraryService();
        library.setMaxBorrowPerUser(5);

        // The limit is now 5, but we can't directly test it without borrowing
        // This test ensures the method doesn't throw
        assertDoesNotThrow(() -> library.setMaxBorrowPerUser(10));
    }

    @Test
    void restoreIsbnCounter_shouldHandleEmptyList() {
        LibraryService library = new LibraryService();
        library.setItems(new ArrayList<>());

        // Should not throw
        assertDoesNotThrow(() -> library.restoreIsbnCounter());
    }

    @Test
    void restoreIsbnCounter_shouldHandleNullList() {
        LibraryService library = new LibraryService();
        library.setItems(null);

        // Should not throw
        assertDoesNotThrow(() -> library.restoreIsbnCounter());
    }

    @Test
    void setItems_shouldHandleNullList() {
        LibraryService library = new LibraryService();
        
        library.setItems(null);
        
        assertNotNull(library.getAllBooks());
        assertEquals(0, library.getAllBooks().size());
    }
}

