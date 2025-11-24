package edu.univ.lms;

/**
 * Fine strategy for normal books.
 * Fine = 1 NIS per day.
 */
public class BookFine implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 1.0;
    }
}
