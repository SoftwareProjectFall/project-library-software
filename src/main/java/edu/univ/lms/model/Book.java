package edu.univ.lms.model;

import java.time.LocalDate;
import edu.univ.lms.strategy.FineStrategy;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.JournalFine;

/**
 * Represents a library item (Book, DVD, Journal) using the Strategy Pattern
 * for fine calculation.
 */
public class Book {

    // ---------------------------------------------------------
    // Fields
    // ---------------------------------------------------------
    private String isbn;
    private String title;
    private String author;

    private boolean borrowed = false;
    private String borrowedByUserId;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    // Strategy Pattern for different fine rules
    private FineStrategy fineStrategy;

    // Needed for JSON loading (to rebuild fineStrategy after restore)
    private String fineType;  // "BOOK", "DVD", "JOURNAL"

    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    // Basic constructor
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.fineStrategy = new BookFine();
        this.fineType = "BOOK";
    }

    // Constructor with strategy
    public Book(String isbn, String title, String author, FineStrategy fineStrategy) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.fineStrategy = fineStrategy;

        if (fineStrategy instanceof BookFine) fineType = "BOOK";
        else if (fineStrategy instanceof DvdFine) fineType = "DVD";
        else if (fineStrategy instanceof JournalFine) fineType = "JOURNAL";
    }

    // Needed for JSON serialization/deserialization
    public Book() {}

    // ---------------------------------------------------------
    // Getters
    // ---------------------------------------------------------
    public String getFineType() {
        return fineType;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public String getBorrowedByUserId() {
        return borrowedByUserId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    // ---------------------------------------------------------
    // Setters
    // ---------------------------------------------------------
    public void setFineType(String fineType) {
        this.fineType = fineType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public void setBorrowedByUserId(String borrowedByUserId) {
        this.borrowedByUserId = borrowedByUserId;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // ---------------------------------------------------------
    // Logic
    // ---------------------------------------------------------

    /**
     * Calculates the fine using the Strategy Pattern.
     */
    public double calculateFine(long overdueDays) {
        if (fineStrategy == null) {
            return overdueDays * 1.0; // Fallback to default book fine
        }
        return fineStrategy.calculateFine(overdueDays);
    }

    /**
     * Returns item type as a clean label.
     */
    public String getItemType() {
        if (fineStrategy instanceof DvdFine) return "DVD";
        if (fineStrategy instanceof JournalFine) return "JOURNAL";
        return "BOOK";
    }

    /**
     * After loading JSON, rebuild the actual fine strategy.
     */
    public void rebuildFineStrategy() {
        switch (fineType) {
            case "DVD": fineStrategy = new DvdFine(); break;
            case "JOURNAL": fineStrategy = new JournalFine(); break;
            default: fineStrategy = new BookFine(); break;
        }
    }

    // ---------------------------------------------------------
    // toString
    // ---------------------------------------------------------
    @Override
    public String toString() {
        String status = borrowed
                ? "Borrowed by UserID: " + borrowedByUserId + ", Due: " + dueDate
                : "Available";

        return "Item{" +
                "ISBN='" + isbn + '\'' +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Type='" + getItemType() + '\'' +
                ", Status=" + status +
                '}';
    }
}

