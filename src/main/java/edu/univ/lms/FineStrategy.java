package edu.univ.lms;

/**
 * Strategy interface for calculating overdue fines.
 * Each item type (Book, DVD, Journal) implements its own fine rules.
 */
public interface FineStrategy {
    double calculateFine(long overdueDays);
}
