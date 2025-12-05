package edu.univ.lms.strategy;

/**
 * Strategy interface for calculating overdue fines.
 * Each item type (Book, DVD, Journal) implements its own fine rules.
 */
public interface FineStrategy {
    double calculateFine(long overdueDays);
}

