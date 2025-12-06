package edu.univ.lms.model;

import java.time.LocalDate;

import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.FineStrategy;
import edu.univ.lms.strategy.JournalFine;

/**
 * Represents a library item (book, DVD, or journal) and uses the Strategy
 * pattern to calculate fines based on the item type.
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

    /**
     * Strategy used to calculate fines for this item.
     * Marked transient so it is not serialized; it is rebuilt using {@code fineType}.
     */
    private transient FineStrategy fineStrategy;

    /**
     * Logical type of the item, used to rebuild {@link #fineStrategy} after
     * JSON deserialization. Expected values: {@code "BOOK"}, {@code "DVD"}, {@code "JOURNAL"}.
     */
    private String fineType;

    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    /**
     * Creates a new book item with a default book fine strategy.
     *
     * @param isbn   ISBN or internal identifier
     * @param title  item title
     * @param author item author or creator
     */
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.fineStrategy = new BookFine();
        this.fineType = "BOOK";
    }

    /**
     * Creates a new item with the given fine strategy.
     *
     * @param isbn         ISBN or internal identifier
     * @param title        item title
     * @param author       item author or creator
     * @param fineStrategy strategy used to calculate fines
     */
    public Book(String isbn, String title, String author, FineStrategy fineStrategy) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.fineStrategy = fineStrategy;

        if (fineStrategy instanceof BookFine) {
            fineType = "BOOK";
        } else if (fineStrategy instanceof DvdFine) {
            fineType = "DVD";
        } else if (fineStrategy instanceof JournalFine) {
            fineType = "JOURNAL";
        }
    }

    /**
     * No-arg constructor required for JSON deserialization.
     */
    public Book() {
    }

    // ---------------------------------------------------------
    // Getters
    // ---------------------------------------------------------

    /**
     * Returns the logical type used to rebuild the fine strategy.
     *
     * @return fine type label (e.g. "BOOK", "DVD", "JOURNAL")
     */
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
     * Calculates the fine for this item using its configured fine strategy.
     *
     * @param overdueDays number of days the item is overdue
     * @return calculated fine amount
     */
    public double calculateFine(long overdueDays) {
        if (fineStrategy == null) {
            // Fallback: simple default fine calculation
            return overdueDays * 1.0;
        }
        return fineStrategy.calculateFine(overdueDays);
    }

    /**
     * Returns the item type as a short label based on the fine strategy.
     *
     * @return "BOOK", "DVD", or "JOURNAL"
     */
    public String getItemType() {
        if (fineStrategy instanceof DvdFine) {
            return "DVD";
        }
        if (fineStrategy instanceof JournalFine) {
            return "JOURNAL";
        }
        return "BOOK";
    }

    /**
     * Rebuilds the fine strategy after JSON deserialization
     * using the stored {@link #fineType} value.
     */
    public void rebuildFineStrategy() {
        switch (fineType) {
            case "DVD":
                fineStrategy = new DvdFine();
                break;
            case "JOURNAL":
                fineStrategy = new JournalFine();
                break;
            default:
                fineStrategy = new BookFine();
                break;
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
