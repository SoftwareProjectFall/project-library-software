package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

public class LibraryServiceAdditionalTest {

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
    void addBook_shouldRejectNullBook() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        boolean result = library.addBook(admin, null);

        assertFalse(result);
        assertEquals(0, library.getAllBooks().size());
    }

    @Test
    void removeBook_shouldRejectWhenBookIsBorrowed() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        Book book = createBook("101", "Borrowed Book");
        book.setBorrowed(true);
        book.setBorrowedByUserId("2");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.removeBook(admin, "101");

        assertFalse(result);
        assertEquals(1, library.getAllBooks().size());
    }

    @Test
    void borrowBook_shouldBorrowJournalFor28Days() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book journal = new Book("301", "Journal", "Editor", new JournalFine());
        library.setItems(new ArrayList<>(Arrays.asList(journal)));

        LocalDate fixedToday = LocalDate.of(2025, 1, 1);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);

            boolean result = library.borrowBook(user, "301");

            assertTrue(result);
            assertTrue(journal.isBorrowed());
            // Journal = 28 days
            assertEquals(fixedToday.plusDays(28), journal.getDueDate());
        }
    }

    @Test
    void returnBook_shouldHandleOnTimeReturn() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);
        LocalDate returnDay = dueDay.minusDays(1); // 1 day before due

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");

            mocked.when(LocalDate::now).thenReturn(returnDay);
            boolean result = library.returnBook(user, "101");

            assertTrue(result);
            assertEquals(0.0, user.getFineBalance());
            assertFalse(book.isBorrowed());
        }
    }

    // NOTE:
    // We no longer mock LocalDate.now() here because static mocking was causing
    // LocalDate.now() to become NULL inside LibraryService.returnBook(), which
    // triggered a NullPointerException when calculating overdue days.
    // Instead, we manually simulate a borrowed DVD by setting:
    // - borrowed = true
    // - borrowedByUserId = the correct user
    // - dueDate = today.minusDays(X)
    // This allows returnBook() to calculate the overdue fine normally,
    // without mocking LocalDate.now(), and avoids breaking internal logic.
    @Test
    void returnBook_shouldCalculateFineForDvd() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book dvd = new Book("201", "Movie", "Director", new DvdFine());
        library.setItems(new ArrayList<>(Arrays.asList(dvd)));

        LocalDate today = LocalDate.now();
        // simulate an already-borrowed DVD that is 2 days overdue
        dvd.setBorrowed(true);
        dvd.setBorrowedByUserId(user.getUserId());
        dvd.setDueDate(today.minusDays(2));

        boolean result = library.returnBook(user, "201");

        assertTrue(result);
        // DvdFine = 20 NIS per day → 2 * 20 = 40 NIS
        assertEquals(40.0, user.getFineBalance());
    }

    // NOTE:
    // This test avoids static mocking of LocalDate.now() because mocking the entire
    // LocalDate class was causing returnBook() to receive a NULL date internally.
    // To prevent NPE and still test fine calculation, we manually simulate an
    // overdue journal by setting its due date to (today - X days) and marking it
    // as borrowed by the user. Then returnBook() computes the fine correctly.
    @Test
    void returnBook_shouldCalculateFineForJournal() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book journal = new Book("301", "Journal", "Editor", new JournalFine());
        library.setItems(new ArrayList<>(Arrays.asList(journal)));

        LocalDate today = LocalDate.now();
        // simulate an already-borrowed Journal that is 4 days overdue
        journal.setBorrowed(true);
        journal.setBorrowedByUserId(user.getUserId());
        journal.setDueDate(today.minusDays(4));

        boolean result = library.returnBook(user, "301");

        assertTrue(result);
        // JournalFine = 0.5 NIS per day → 4 * 0.5 = 2.0 NIS
        assertEquals(2.0, user.getFineBalance());
    }

    @Test
    void unregisterUser_shouldRejectIfAdminTriesToUnregisterAdmin() {
        LibraryService library = new LibraryService();
        User admin1 = createLoggedInAdmin();
        User admin2 = new User("2", "Admin2", "admin2", "pass", true, "admin2@test.com");
        admin2.login("admin2", "pass");

        List<User> users = new ArrayList<>();
        users.add(admin1);
        users.add(admin2);

        boolean result = library.unregisterUser(admin1, admin2, users);

        assertFalse(result);
        assertTrue(users.contains(admin2));
    }

    @Test
    void unregisterUser_shouldRejectIfNotAdmin() {
        LibraryService library = new LibraryService();
        User normalUser = createLoggedInUser("1");
        User target = createLoggedInUser("2");

        List<User> users = new ArrayList<>();
        users.add(normalUser);
        users.add(target);

        boolean result = library.unregisterUser(normalUser, target, users);

        assertFalse(result);
        assertTrue(users.contains(target));
    }

    @Test
    void showAllBooks_shouldHandleEmptyLibrary() {
        LibraryService library = new LibraryService();

        // Should not throw
        assertDoesNotThrow(() -> library.showAllBooks());
    }

    @Test
    void showBorrowedBooks_shouldHandleUserWithNoBooks() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Available");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Should not throw
        assertDoesNotThrow(() -> library.showBorrowedBooks(user));
    }
}
