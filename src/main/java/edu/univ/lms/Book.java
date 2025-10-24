package edu.univ.lms;

import java.time.LocalDate;

public class Book {

    // -------------------
    // Fields
    // -------------------
    private String isbn;
    private String title;
    private String author;

    private boolean borrowed = false;          // Default: not borrowed
    private String borrowedByUserId;           // Optional: tracks who borrowed
    private LocalDate borrowDate;
    private LocalDate dueDate;

    // -------------------
    // Constructor
    // -------------------
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    // -------------------
    // Getters
    // -------------------
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

    // -------------------
    // Setters
    // -------------------
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

    // -------------------
    // toString
    // -------------------
    @Override
    public String toString() {
        String status = borrowed
                ? "Borrowed by UserID: " + borrowedByUserId + ", Due: " + dueDate
                : "Available";

        return "Book{" +
                "ISBN='" + isbn + '\'' +
                ", Title='" + title + '\'' +
                ", Author='" + author + '\'' +
                ", Status=" + status +
                '}';
    }
}
