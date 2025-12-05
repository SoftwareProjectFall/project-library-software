package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

/**
  Unit tests for the LibraryService class.
  Covers:
   - Admin actions (add / remove / update)
   - Borrow / return logic (books & DVDs, fines, restrictions)
   - Search helpers (by title / author / ISBN)
   - Display methods (showAllBooks / showBorrowedBooks / showOverdueBooks)
   - Unregister user rules
   - restoreIsbnCounter logic
 */
public class LibraryTest {

    // Helper methods

    // Create an admin user who is already logged in
    private User createLoggedInAdmin() {
        User admin = new User("1", "Admin", "admin", "1234", true, "admin@test.com");
        admin.login("admin", "1234");
        return admin;
    }

    // Create a normal (non-admin) user who is already logged in
    private User createLoggedInUser(String id) {
        User user = new User(id, "User" + id, "user" + id, "pass", false, "user" + id + "@test.com");
        user.login("user" + id, "pass");
        return user;
    }

    // Create a normal book (uses BookFine strategy)
    private Book createBook(String isbn, String title) {
        return new Book(isbn, title, "Author", new BookFine());
    }

    // Create a DVD (uses DvdFine strategy)
    private Book createDvd(String isbn, String title) {
        return new Book(isbn, title, "Director", new DvdFine());
    }

    // Admin actions: add / remove / update

    @Test
    void addBook_shouldAllowLoggedInAdminAndAssignIsbn() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = new Book("", "Clean Code", "Robert Martin", new BookFine());

        boolean result = library.addBook(admin, book);

        assertTrue(result);
        assertNotNull(book.getIsbn());
        assertFalse(book.getIsbn().isBlank());
        assertEquals(1, library.getAllBooks().size());
    }

    @Test
    void addBook_shouldRejectNonAdmin() {
        LibraryService library = new LibraryService();
        User normalUser = createLoggedInUser("2");
        Book book = new Book("", "Clean Code", "Robert Martin", new BookFine());

        boolean result = library.addBook(normalUser, book);

        assertFalse(result);
        assertEquals(0, library.getAllBooks().size());
    }

    @Test
    void removeBook_shouldRemoveIfNotBorrowed() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Java");
        List<Book> list = new ArrayList<>();
        list.add(book);
        library.setItems(list);

        boolean result = library.removeBook(admin, "101");

        assertTrue(result);
        assertEquals(0, library.getAllBooks().size());
    }

    // NEW: user not admin / not logged in → should fail
    @Test
    void removeBook_shouldRejectWhenUserIsNotAdminOrNotLoggedIn() {
        LibraryService library = new LibraryService();

        // normal user (logged in but not admin)
        User normalUser = createLoggedInUser("2");

        Book book = createBook("101", "Java");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.removeBook(normalUser, "101");

        assertFalse(result);
        // book must stay in the list
        assertEquals(1, library.getAllBooks().size());
    }

    // NEW: isbn null or blank → guard if (isbn == null || isbn.isBlank())
    @Test
    void removeBook_shouldRejectWhenIsbnIsNullOrBlank() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Java");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean resultNull = library.removeBook(admin, null);
        boolean resultBlank = library.removeBook(admin, "   ");

        assertFalse(resultNull);
        assertFalse(resultBlank);
        // still not removed
        assertEquals(1, library.getAllBooks().size());
    }

    // NEW: item not found → reach "Item not found." branch
    @Test
    void removeBook_shouldReturnFalseWhenItemNotFound() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Java");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.removeBook(admin, "999"); // different ISBN

        assertFalse(result);
        assertEquals(1, library.getAllBooks().size());
    }

    @Test
    void returnBook_shouldRejectWhenUserNotLoggedIn() {
        LibraryService library = new LibraryService();

        // user created but NOT logged in
        User user = new User("2", "User2", "user2", "pass", false, "user2@test.com");

        Book book = createBook("101", "Java Book");
        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.returnBook(user, "101");

        assertFalse(result);
        // book should stay borrowed because return failed
        assertTrue(book.isBorrowed());
    }

    @Test
    void returnBook_shouldRejectWhenAdminTriesToReturn() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Java Book");
        book.setBorrowed(true);
        book.setBorrowedByUserId("2");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.returnBook(admin, "101");

        assertFalse(result);
        assertTrue(book.isBorrowed());
    }

    @Test
    void returnBook_shouldRejectWhenItemNotFound() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        // library has one book with ISBN 101
        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // try to return different ISBN
        boolean result = library.returnBook(user, "999");

        assertFalse(result);
        // original book still available (not borrowed)
        assertFalse(book.isBorrowed());
    }

    @Test
    void borrowBook_shouldRejectWhenUserNotLoggedIn() {
        LibraryService library = new LibraryService();

        // user is NOT logged in (no login call)
        User user = new User("2", "User2", "user2", "pass", false, "user2@test.com");

        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.borrowBook(user, "101");

        assertFalse(result);
        assertFalse(book.isBorrowed());
    }

    @Test
    void borrowBook_shouldRejectWhenUserIsAdmin() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Admin Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.borrowBook(admin, "101");

        assertFalse(result);
        assertFalse(book.isBorrowed());
    }

    @Test
    void borrowBook_shouldRejectWhenBorrowLimitReached() {
        LibraryService library = new LibraryService();
        library.setMaxBorrowPerUser(1);

        User user = createLoggedInUser("2");

        Book alreadyBorrowed = createBook("101", "First");
        alreadyBorrowed.setBorrowed(true);
        alreadyBorrowed.setBorrowedByUserId(user.getUserId());

        Book newBook = createBook("102", "Second");

        List<Book> list = new ArrayList<>();
        list.add(alreadyBorrowed);
        list.add(newBook);
        library.setItems(list);

        boolean result = library.borrowBook(user, "102");

        assertFalse(result);
        assertFalse(newBook.isBorrowed());
    }

    @Test
    void borrowBook_shouldRejectWhenItemNotFound() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        // library has book 101 only
        Book book = createBook("101", "Java");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.borrowBook(user, "999"); // not existing ISBN

        assertFalse(result);
        assertFalse(book.isBorrowed());
    }

    @Test
    void borrowBook_shouldRejectWhenBookAlreadyBorrowed() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        Book book = createBook("101", "Java");
        book.setBorrowed(true);
        book.setBorrowedByUserId("OTHER"); // borrowed by someone else
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.borrowBook(user, "101");

        assertFalse(result);
        assertTrue(book.isBorrowed());
        assertEquals("OTHER", book.getBorrowedByUserId());
    }

    @Test
    void returnBook_shouldRejectWhenBookNotBorrowedByThisUser() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        // book borrowed by another user
        Book book = createBook("101", "Java Book");
        book.setBorrowed(true);
        book.setBorrowedByUserId("OTHER");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.returnBook(user, "101");

        assertFalse(result);
        // still borrowed by OTHER
        assertTrue(book.isBorrowed());
        assertEquals("OTHER", book.getBorrowedByUserId());
    }

    @Test
    void updateBook_shouldUpdateTitleAndAuthor() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        Book book = createBook("101", "Old");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.updateBook(admin, "101", "New Title", "New Author");

        assertTrue(result);
        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    void updateBook_shouldReturnFalseWhenNotFound() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        boolean result = library.updateBook(admin, "999", "X", "Y");

        assertFalse(result);
    }

    // Borrow logic

    @Test
    void borrowBook_shouldBorrowNormalBookFor28Days() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        // Fix today's date using Mockito (for deterministic due date)
        LocalDate fixedToday = LocalDate.of(2025, 1, 1);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);

            boolean result = library.borrowBook(user, "101");

            assertTrue(result);
            assertTrue(book.isBorrowed());
            assertEquals(user.getUserId(), book.getBorrowedByUserId());
            assertEquals(fixedToday, book.getBorrowDate());
            // Normal book = 28 days
            assertEquals(fixedToday.plusDays(28), book.getDueDate());
        }
    }

    @Test
    void borrowBook_shouldBorrowDvdFor7Days() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book dvd = createDvd("201", "Some Movie");
        library.setItems(new ArrayList<>(Arrays.asList(dvd)));

        LocalDate fixedToday = LocalDate.of(2025, 1, 1);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);

            boolean result = library.borrowBook(user, "201");

            assertTrue(result);
            assertTrue(dvd.isBorrowed());
            // DVD = 7 days
            assertEquals(fixedToday.plusDays(7), dvd.getDueDate());
        }
    }

    @Test
    void borrowBook_shouldRejectWhenUserHasOverdueItems() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        // Existing overdue book for this user
        Book borrowed = createBook("101", "Old Book");
        borrowed.setBorrowed(true);
        borrowed.setBorrowedByUserId(user.getUserId());
        borrowed.setDueDate(LocalDate.of(2024, 12, 1)); // before "today"

        Book newBook = createBook("102", "New Book");
        List<Book> list = new ArrayList<>();
        list.add(borrowed);
        list.add(newBook);
        library.setItems(list);

        LocalDate fixedToday = LocalDate.of(2025, 1, 1);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);

            boolean result = library.borrowBook(user, "102");

            // Should be blocked because of overdue item
            assertFalse(result);
            assertFalse(newBook.isBorrowed());
        }
    }

    @Test
    void borrowBook_shouldRejectWhenUserHasUnpaidFines() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        user.addFine(10.0); // unpaid fine

        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        boolean result = library.borrowBook(user, "101");

        assertFalse(result);
        assertFalse(book.isBorrowed());
    }

    // Return logic

    @Test
    void returnBook_onTimeShouldNotAddFine() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            // Day of borrowing
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");

            // Now "today" = due date (not late)
            mocked.when(LocalDate::now).thenReturn(dueDay);

            boolean result = library.returnBook(user, "101");

            assertTrue(result);
            assertEquals(0.0, user.getFineBalance());
            assertFalse(book.isBorrowed());
        }
    }

    @Test
    void returnBook_lateShouldAddFine() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Java Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);
        LocalDate lateDay = dueDay.plusDays(5); // 5 days late

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");

            mocked.when(LocalDate::now).thenReturn(lateDay);
            boolean result = library.returnBook(user, "101");

            assertTrue(result);
            // BookFine = 1 NIS per day → 5 NIS
            assertEquals(5.0, user.getFineBalance());
            assertFalse(book.isBorrowed());
        }
    }

    // Search + helper methods

    @Test
    void searchBooksByTitle_shouldReturnMatchingBooks() {
        LibraryService library = new LibraryService();
        Book b1 = createBook("1", "Java Programming");
        Book b2 = createBook("2", "Python Guide");
        Book b3 = createBook("3", "Advanced Java");
        library.setItems(new ArrayList<>(Arrays.asList(b1, b2, b3)));

        List<Book> result = library.searchBooksByTitle("java");

        assertEquals(2, result.size());
        assertTrue(result.contains(b1));
        assertTrue(result.contains(b3));
    }

    @Test
    void searchBooksByAuthor_shouldReturnMatchingBooks() {
        LibraryService library = new LibraryService();
        Book b1 = new Book("1", "Book A", "Mahmoud", new BookFine());
        Book b2 = new Book("2", "Book B", "Other", new BookFine());
        Book b3 = new Book("3", "Book C", "Mahmoud Ali", new BookFine());
        library.setItems(new ArrayList<>(Arrays.asList(b1, b2, b3)));

        List<Book> result = library.searchBooksByAuthor("mahmoud");

        assertEquals(2, result.size());
        assertTrue(result.contains(b1));
        assertTrue(result.contains(b3));
    }

    @Test
    void searchBookByIsbn_shouldReturnCorrectBookOrNull() {
        LibraryService library = new LibraryService();
        Book b1 = createBook("10", "A");
        Book b2 = createBook("20", "B");
        library.setItems(new ArrayList<>(Arrays.asList(b1, b2)));

        assertEquals(b1, library.searchBookByIsbn("10"));
        assertNull(library.searchBookByIsbn("999"));
    }

    @Test
    void hasOverdueBooks_andCountBorrowedBooks_shouldWorkTogether() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");

        LocalDate today = LocalDate.now();
        LocalDate oldDue = today.minusDays(3);   // overdue
        LocalDate futureDue = today.plusDays(5); // still on time

        Book overdue = createBook("1", "Overdue");
        overdue.setBorrowed(true);
        overdue.setBorrowedByUserId(user.getUserId());
        overdue.setDueDate(oldDue);

        Book ok = createBook("2", "On time");
        ok.setBorrowed(true);
        ok.setBorrowedByUserId(user.getUserId());
        ok.setDueDate(futureDue);

        library.setItems(new ArrayList<>(Arrays.asList(overdue, ok)));

        assertEquals(2, library.countBorrowedBooksByUser(user));
        assertTrue(library.hasOverdueBooks(user));
    }

    // Display methods (showAllBooks / showBorrowedBooks / showOverdueBooks)

    @Test
    void showAllBooks_shouldPrintAvailableAndOverdueItems() {
        LibraryService library = new LibraryService();

        // Capture System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try {
            // Case 1: empty library
            library.showAllBooks();

            // Case 2: library with available + overdue items
            Book available = new Book("101", "Available Book", "Author", new BookFine());

            Book overdue = new Book("102", "Overdue Book", "Author", new BookFine());
            overdue.setBorrowed(true);
            overdue.setBorrowedByUserId("U1");
            overdue.setDueDate(LocalDate.now().minusDays(1)); // yesterday → overdue

            List<Book> list = new ArrayList<>();
            list.add(available);
            list.add(overdue);
            library.setItems(list);

            library.showAllBooks();
        } finally {
            System.setOut(original);
        }

        String console = out.toString();

        assertTrue(console.contains("No items in the library."),
                "Empty library message should be printed");
        assertTrue(console.contains("--- ALL ITEMS ---"),
                "Header for listing items should be printed");
        assertTrue(console.contains("Available"),
                "Available item status should be printed");
        assertTrue(console.contains("OVERDUE"),
                "Overdue status should be printed for overdue items");
    }

    @Test
    void showOverdueBooks_shouldPrintOnlyOverdueItems() {
        LibraryService library = new LibraryService();

        LocalDate fixedToday = LocalDate.of(2025, 1, 10);

        Book overdue = new Book("101", "Overdue Book", "Author", new BookFine());
        overdue.setBorrowed(true);
        overdue.setBorrowedByUserId("U1");
        overdue.setDueDate(fixedToday.minusDays(3));

        Book notOverdue = new Book("102", "Future Book", "Author", new BookFine());
        notOverdue.setBorrowed(true);
        notOverdue.setBorrowedByUserId("U2");
        notOverdue.setDueDate(fixedToday.plusDays(5));

        library.setItems(new ArrayList<>(Arrays.asList(overdue, notOverdue)));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            mocked.when(LocalDate::now).thenReturn(fixedToday);

            library.showOverdueBooks();
        } finally {
            System.setOut(original);
        }

        String console = out.toString();

        assertTrue(console.contains("--- Overdue Items ---"),
                "Header must be printed");
        assertTrue(console.contains("Overdue Book"),
                "Overdue item must be printed");
        assertFalse(console.contains("Future Book"),
                "Non-overdue items must NOT be printed");
    }

    @Test
    void showBorrowedBooks_shouldPrintOnlyCurrentUserItems() {
        LibraryService library = new LibraryService();

        User user1 = createLoggedInUser("1");
        User user2 = createLoggedInUser("2");

        Book bookForUser1 = new Book("201", "Book U1", "Auth", new BookFine());
        bookForUser1.setBorrowed(true);
        bookForUser1.setBorrowedByUserId(user1.getUserId());
        bookForUser1.setDueDate(LocalDate.now().plusDays(5));

        Book bookForUser2 = new Book("202", "Book U2", "Auth", new BookFine());
        bookForUser2.setBorrowed(true);
        bookForUser2.setBorrowedByUserId(user2.getUserId());
        bookForUser2.setDueDate(LocalDate.now().plusDays(5));

        List<Book> list = new ArrayList<>();
        list.add(bookForUser1);
        list.add(bookForUser2);
        library.setItems(list);

        // Capture System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try {
            library.showBorrowedBooks(user1);
        } finally {
            System.setOut(original);
        }

        String console = out.toString();

        assertTrue(console.contains("Book U1"),
                "Borrowed book for user1 should be shown");
        assertFalse(console.contains("Book U2"),
                "Borrowed book for another user must NOT be shown");
    }

    // restoreIsbnCounter + unregister user logic

    @Test
    void restoreIsbnCounter_shouldSetCounterToMaxExistingIsbn() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();

        // Book with numeric ISBN (valid)
        Book b1 = createBook("150", "Old A");

        // Book with NON-numeric ISBN → this should exercise the catch block
        Book b2 = createBook("xyz", "Invalid ISBN");

        // Book with highest valid ISBN
        Book b3 = createBook("200", "Old B");

        // Load items into the library
        library.setItems(new ArrayList<>(Arrays.asList(b1, b2, b3)));

        // Rebuild internal counter based on existing ISBNs
        library.restoreIsbnCounter();

        // Now we add a new book → ISBN should become 201 (max + 1)
        Book newBook = createBook("", "New Book");
        boolean result = library.addBook(admin, newBook);

        assertTrue(result, "Admin should be able to add the new book");
        assertEquals("201", newBook.getIsbn(),
                "ISBN counter should move to max existing ISBN + 1");
    }

    @Test
    void unregisterUser_shouldAllowAdminWhenNoLoansAndNoFines() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        User target = createLoggedInUser("2");

        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(target);

        boolean result = library.unregisterUser(admin, target, users);

        assertTrue(result);
        assertFalse(users.contains(target));
    }

    @Test
    void unregisterUser_shouldRejectIfTargetHasLoans() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        User target = createLoggedInUser("2");

        Book borrowed = createBook("101", "Loaned");
        borrowed.setBorrowed(true);
        borrowed.setBorrowedByUserId(target.getUserId());
        library.setItems(new ArrayList<>(Arrays.asList(borrowed)));

        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(target);

        boolean result = library.unregisterUser(admin, target, users);

        assertFalse(result);
        assertTrue(users.contains(target));
    }

    @Test
    void returnBook_forceFullLateBranchCoverage() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("9");

        Book book = createBook("500", "Late Testing Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);
        LocalDate lateDay = dueDay.plusDays(4); // 4 days late

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {

            mocked.when(LocalDate::now).thenReturn(borrowDay);
            assertTrue(library.borrowBook(user, "500"));

            mocked.when(LocalDate::now).thenReturn(lateDay);
            boolean result = library.returnBook(user, "500");
            assertTrue(result);

        } finally {
            System.setOut(originalOut);
        }

        String console = out.toString();
        assertTrue(console.contains("Late return! Overdue by 4 days. Fine: 4.0 NIS"));
        assertEquals(4.0, user.getFineBalance());
    }

    @Test
    void returnBook_lateShouldCalculateFineAndPrintMessage() {
        LibraryService library = new LibraryService();
        User user = createLoggedInUser("2");
        Book book = createBook("101", "Late Book");
        library.setItems(new ArrayList<>(Arrays.asList(book)));

        LocalDate borrowDay = LocalDate.of(2025, 1, 1);
        LocalDate dueDay = borrowDay.plusDays(28);
        LocalDate lateDay = dueDay.plusDays(3); // 3 days late

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {
            // borrow on borrowDay
            mocked.when(LocalDate::now).thenReturn(borrowDay);
            library.borrowBook(user, "101");

            // return on lateDay (after due date)
            mocked.when(LocalDate::now).thenReturn(lateDay);
            boolean result = library.returnBook(user, "101");

            assertTrue(result);
        } finally {
            System.setOut(original);
        }

        String console = out.toString();

        // 3 days overdue → fine = 3.0 with BookFine (1 NIS per day)
        assertEquals(3.0, user.getFineBalance());
        assertTrue(
                console.contains("Late return! Overdue by 3 days. Fine: 3.0 NIS"),
                "Late-return message must be printed with days and fine"
        );
    }

    @Test
    void unregisterUser_shouldRejectIfTargetHasFines() {
        LibraryService library = new LibraryService();
        User admin = createLoggedInAdmin();
        User target = createLoggedInUser("2");
        target.addFine(10.0); // has unpaid fine

        List<User> users = new ArrayList<>();
        users.add(admin);
        users.add(target);

        boolean result = library.unregisterUser(admin, target, users);

        assertFalse(result);
        assertTrue(users.contains(target));
    }
}
