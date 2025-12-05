package edu.univ.lms;

import static org.mockito.Mockito.*;

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
import edu.univ.lms.service.ReminderService;
import edu.univ.lms.observer.Observer;
import edu.univ.lms.strategy.BookFine;

/**
  Unit tests for ReminderService using Mockito.
 
 This test verifies:
  1. Overdue items are detected correctly.
  2. Email notifications are sent ONLY to users who have overdue items.
  3. Mockito mocks the email server (Observer).
 4. Mockito mocks LocalDate.now() to simulate a fixed "current date".
 */
public class ReminderServiceTest {

    @Test
    void sendOverdueReminders_notifiesUsersWithCorrectCount() {

        // Arrange (Setup test environment)

        ReminderService reminderService = new ReminderService();

        // Mock observer (email server) so no real emails are sent.
        Observer emailObserver = mock(Observer.class);
        reminderService.addObserver(emailObserver);

        LibraryService libraryService = new LibraryService();

        // We simulate today's fixed date using Mockito.
        LocalDate today = LocalDate.of(2025, 1, 10);

        // Create test users
        User u1 = new User("1", "Mahmoud", "mahmoud", "1234", false, "m@test.com");
        User u2 = new User("2", "Ahmad", "ahmad", "1234", false, "a@test.com");

        List<User> allUsers = new ArrayList<>();
        allUsers.add(u1);
        allUsers.add(u2);

        // Create books borrowed by these users
        // u1 has 2 overdue books and 1 not overdue
        // u2 has 0 overdue books

        Book b1 = new Book("101", "B1", "A", new BookFine());
        b1.setBorrowed(true);
        b1.setBorrowedByUserId("1");
        b1.setDueDate(today.minusDays(3)); // Overdue (3 days late)

        Book b2 = new Book("102", "B2", "A", new BookFine());
        b2.setBorrowed(true);
        b2.setBorrowedByUserId("1");
        b2.setDueDate(today.minusDays(1)); // Overdue (1 day late)

        Book b3 = new Book("103", "B3", "A", new BookFine());
        b3.setBorrowed(true);
        b3.setBorrowedByUserId("1");
        b3.setDueDate(today.plusDays(2)); // NOT overdue

        Book b4 = new Book("104", "B4", "A", new BookFine());
        b4.setBorrowed(true);
        b4.setBorrowedByUserId("2");
        b4.setDueDate(today.plusDays(5)); // NOT overdue

        // Load books into library
        libraryService.setItems(new ArrayList<>(Arrays.asList(b1, b2, b3, b4)));

        // Act (Call function under test)

        // Mock LocalDate.now() to make overdue logic predictable
        try (MockedStatic<LocalDate> mocked = Mockito.mockStatic(LocalDate.class)) {

            // When LocalDate.now() is called, return "today"
            mocked.when(LocalDate::now).thenReturn(today);

            // Run the method
            reminderService.sendOverdueReminders(libraryService, allUsers);

            // Assert (Verify behavior)

            verify(emailObserver).notify(u1, "You have 2 overdue item(s).");

            // u2 has NO overdue items → must NOT receive any message
            verify(emailObserver, never()).notify(eq(u2), anyString());

            // Ensure no unexpected notifications were sent
            verifyNoMoreInteractions(emailObserver);
        }
    }
}
