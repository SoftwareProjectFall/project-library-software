package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

public class BookCalculateFineTest {

    @Test
    void calculateFine_withNullStrategy_shouldUseFallback() throws Exception {
        Book book = new Book("1", "Test", "Author");
        
        // Use reflection to set fineStrategy to null
        java.lang.reflect.Field field = Book.class.getDeclaredField("fineStrategy");
        field.setAccessible(true);
        field.set(book, null);
        
        // Should use fallback calculation
        double fine = book.calculateFine(5);
        assertEquals(5.0, fine); // Fallback = 1.0 per day
    }

    @Test
    void calculateFine_withDvdStrategy_shouldCalculateCorrectly() {
        Book dvd = new Book("1", "Movie", "Director", new DvdFine());
        
        double fine = dvd.calculateFine(3);
        assertEquals(60.0, fine); // 3 * 20 = 60
    }

    @Test
    void calculateFine_withJournalStrategy_shouldCalculateCorrectly() {
        Book journal = new Book("1", "Journal", "Editor", new JournalFine());
        
        double fine = journal.calculateFine(10);
        assertEquals(5.0, fine); // 10 * 0.5 = 5.0
    }

    @Test
    void calculateFine_withLargeNumber_shouldHandleCorrectly() {
        Book book = new Book("1", "Test", "Author", new BookFine());
        
        double fine = book.calculateFine(100);
        assertEquals(100.0, fine);
    }
}

