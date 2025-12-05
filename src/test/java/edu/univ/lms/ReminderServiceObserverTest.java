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

public class ReminderServiceObserverTest {

    @Test
    void removeObserver_shouldRemoveObserverFromList() {
        ReminderService reminderService = new ReminderService();
        
        Observer observer1 = mock(Observer.class);
        Observer observer2 = mock(Observer.class);
        
        reminderService.addObserver(observer1);
        reminderService.addObserver(observer2);
        
        reminderService.removeObserver(observer1);
        
        // Now only observer2 should be notified
        LibraryService libraryService = new LibraryService();
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        Book book = new Book("1", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        book.setDueDate(LocalDate.now().minusDays(1));
        
        libraryService.setItems(new ArrayList<>(Arrays.asList(book)));
        List<User> users = new ArrayList<>(Arrays.asList(user));
        
        reminderService.sendOverdueReminders(libraryService, users);
        
        verify(observer1, never()).notify(any(), anyString());
        verify(observer2, times(1)).notify(user, "You have 1 overdue item(s).");
    }

    @Test
    void addObserver_shouldAddMultipleObservers() {
        ReminderService reminderService = new ReminderService();
        
        Observer observer1 = mock(Observer.class);
        Observer observer2 = mock(Observer.class);
        
        reminderService.addObserver(observer1);
        reminderService.addObserver(observer2);
        
        LibraryService libraryService = new LibraryService();
        User user = new User("1", "Test", "test", "pass", false, "test@test.com");
        Book book = new Book("1", "Book", "Author", new BookFine());
        book.setBorrowed(true);
        book.setBorrowedByUserId("1");
        book.setDueDate(LocalDate.now().minusDays(1));
        
        libraryService.setItems(new ArrayList<>(Arrays.asList(book)));
        List<User> users = new ArrayList<>(Arrays.asList(user));
        
        reminderService.sendOverdueReminders(libraryService, users);
        
        verify(observer1, times(1)).notify(user, "You have 1 overdue item(s).");
        verify(observer2, times(1)).notify(user, "You have 1 overdue item(s).");
    }
}

