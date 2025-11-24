package edu.univ.lms;

/**
 * Fine strategy for DVDs.
 * Fine = 20 NIS per day.
 */
public class DvdFine implements FineStrategy {

    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * 20.0;
    }
}
