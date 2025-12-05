package edu.univ.lms;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.service.ReminderService;
import edu.univ.lms.observer.Observer;
import edu.univ.lms.strategy.BookFine;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ReminderServiceEdgeCasesTest {

    @Test
    void sendOverdueReminders_shouldNotNotifyWhenNoObservers() {
        ReminderService reminderService = new ReminderService();
        // No observers added

        LibraryService libraryService = new LibraryService();
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        Book book = new Book("1", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        book.setDueDate(LocalDate.now().minusDays(1));

        libraryService.setItems(new ArrayList<>(Arrays.asList(book)));
        List<User> users = new ArrayList<>(Arrays.asList(user));

        // Should not throw even with no observers
        assertDoesNotThrow(() -> reminderService.sendOverdueReminders(libraryService, users));
    }

    @Test
    void sendOverdueReminders_shouldNotNotifyWhenNoOverdueItems() {
        ReminderService reminderService = new ReminderService();
        Observer observer = mock(Observer.class);
        reminderService.addObserver(observer);

        LibraryService libraryService = new LibraryService();
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        Book book = new Book("1", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        book.setDueDate(LocalDate.now().plusDays(5)); // Not overdue

        libraryService.setItems(new ArrayList<>(Arrays.asList(book)));
        List<User> users = new ArrayList<>(Arrays.asList(user));

        reminderService.sendOverdueReminders(libraryService, users);

        verify(observer, never()).notify(any(), anyString());
    }

    @Test
    void sendOverdueReminders_shouldHandleMultipleOverdueItemsForSameUser() {
        ReminderService reminderService = new ReminderService();
        Observer observer = mock(Observer.class);
        reminderService.addObserver(observer);

        LibraryService libraryService = new LibraryService();
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        
        Book book1 = new Book("1", "Book1", "Author", new BookFine());
        book1.setBorrowed(true);
        book1.setBorrowedByUserId("1");
        book1.setDueDate(LocalDate.now().minusDays(2));

        Book book2 = new Book("2", "Book2", "Author", new BookFine());
        book2.setBorrowed(true);
        book2.setBorrowedByUserId("1");
        book2.setDueDate(LocalDate.now().minusDays(5));

        libraryService.setItems(new ArrayList<>(Arrays.asList(book1, book2)));
        List<User> users = new ArrayList<>(Arrays.asList(user));

        reminderService.sendOverdueReminders(libraryService, users);

        verify(observer, times(1)).notify(user, "You have 2 overdue item(s).");
    }

    @Test
    void sendOverdueReminders_shouldHandleUserNotInList() {
        ReminderService reminderService = new ReminderService();
        Observer observer = mock(Observer.class);
        reminderService.addObserver(observer);

        LibraryService libraryService = new LibraryService();
        Book book = new Book("1", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("999"); // User ID that doesn't exist in users list
        book.setDueDate(LocalDate.now().minusDays(1));

        libraryService.setItems(new ArrayList<>(Arrays.asList(book)));
        List<User> users = new ArrayList<>(); // Empty user list

        reminderService.sendOverdueReminders(libraryService, users);

        verify(observer, never()).notify(any(), anyString());
    }
}

