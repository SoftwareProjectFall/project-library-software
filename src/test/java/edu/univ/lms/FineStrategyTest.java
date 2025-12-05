package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.Book;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.FineStrategy;
import edu.univ.lms.strategy.JournalFine;

/**
 * Tests for fine calculation strategy classes:
 * - BookFine
 * - DvdFine
 * - JournalFine
 */
public class FineStrategyTest {

    @Test
    void bookFine_shouldBe1NisPerDay() {
        FineStrategy strategy = new BookFine();
        // 5 days overdue → 5 * 1.0 = 5.0
        double fine = strategy.calculateFine(5);
        assertEquals(5.0, fine);
    }

    @Test
    void dvdFine_shouldBe20NisPerDay() {
        FineStrategy strategy = new DvdFine();
        // 2 days overdue → 2 * 20.0 = 40.0
        double fine = strategy.calculateFine(2);
        assertEquals(40.0, fine);
    }

    @Test
    void journalFine_shouldBe0Point5NisPerDay() {
        FineStrategy strategy = new JournalFine();
        // 4 days overdue → 4 * 0.5 = 2.0
        double fine = strategy.calculateFine(4);
        assertEquals(2.0, fine);
    }

    @Test
    void book_calculateFine_shouldFallbackToDefaultWhenStrategyNull() {
        // Book created with normal constructor → default strategy is BookFine
        Book book = new Book("111", "Test", "Tester");
        double fine = book.calculateFine(3);
        assertEquals(3.0, fine); // 3 days * 1 NIS
    }
}
