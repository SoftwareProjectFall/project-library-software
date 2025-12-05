package edu.univ.lms.strategy;

/**
 * Fine strategy for journals.
 * Fine = 0.5 NIS per day.
 */
public class JournalFine implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 0.5;
    }
}

