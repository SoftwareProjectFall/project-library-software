package edu.univ.lms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library {

    private List<Book> books;
    private int maxBorrowPerUser = 3; // max books a user can borrow at a time

    // Constructor
    public Library() {
        books = new ArrayList<>();
    }

    // Optional: set borrow limit
    public void setMaxBorrowPerUser(int max) {
        this.maxBorrowPerUser = max;
    }

    // --------------- Admin actions ----------------
    public boolean addBook(User user, Book book) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can add books.");
            return false;
        }

        if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()) {
            return false;
        }

        for (Book b : books) {
            if (book.getIsbn().equalsIgnoreCase(b.getIsbn())) {
                System.out.println("A book with this ISBN already exists.");
                return false;
            }
        }

        books.add(book);
        System.out.println("Book \"" + book.getTitle() + "\" added successfully.");
        return true;
    }

    public boolean removeBook(User user, String isbn) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can remove books.");
            return false;
        }

        if (isbn == null || isbn.isBlank()) return false;

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                if (b.isBorrowed()) {
                    System.out.println("Cannot remove a borrowed book.");
                    return false;
                }
                books.remove(b);
                System.out.println("Book \"" + b.getTitle() + "\" removed successfully.");
                return true;
            }
        }

        System.out.println("Book not found.");
        return false;
    }

    public boolean updateBook(User user, String isbn, String newTitle, String newAuthor) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can update books.");
            return false;
        }

        if (isbn == null || isbn.isBlank()) return false;

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                if (newTitle != null && !newTitle.isBlank()) b.setTitle(newTitle);
                if (newAuthor != null && !newAuthor.isBlank()) b.setAuthor(newAuthor);
                System.out.println("Book \"" + b.getTitle() + "\" updated successfully.");
                return true;
            }
        }

        System.out.println("Book not found.");
        return false;
    }

    // --------------- User actions ----------------
    public boolean borrowBook(User user, String isbn) {
        if (!user.isLoggedIn()) {
            System.out.println("You must be logged in to borrow books.");
            return false;
        }

        if (user.isAdmin()) {
            System.out.println("Admins cannot borrow books.");
            return false;
        }

        if (countBorrowedBooksByUser(user) >= maxBorrowPerUser) {
            System.out.println(user.getName() + " has reached the borrow limit (" + maxBorrowPerUser + ").");
            return false;
        }

        Book book = searchBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        if (book.isBorrowed()) {
            System.out.println("Book is already borrowed.");
            return false;
        }

        LocalDate today = LocalDate.now();
        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());
        book.setBorrowDate(today);
        book.setDueDate(today.plusDays(28));

        System.out.println(user.getName() + " borrowed \"" + book.getTitle() + "\" on " + today +
                ". Due date: " + book.getDueDate());
        return true;
    }


    public boolean returnBook(User user, String isbn) {
        if (!user.isLoggedIn()) {
            System.out.println("You must be logged in to return books.");
            return false;
        }

        if (user.isAdmin()) {
            System.out.println("Admins cannot return books.");
            return false;
        }

        Book book = searchBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found.");
            return false;
        }
        if (!book.isBorrowed() || !user.getUserId().equals(book.getBorrowedByUserId())) {
            System.out.println("This book was not borrowed by " + user.getName());
            return false;
        }

        book.setBorrowed(false);
        book.setBorrowedByUserId(null);
        System.out.println(user.getName() + " returned \"" + book.getTitle() + "\".");
        if (LocalDate.now().isAfter(book.getDueDate())) {
            System.out.println("Returned late! Due date was: " + book.getDueDate());
        }

        return true;
    }


    // --------------- Searches ----------------
    public Book searchBookByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return null;
        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) return b;
        }
        return null;
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        if (title == null || title.isBlank()) return result;

        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())) result.add(b);
        }
        return result;
    }

    public List<Book> searchBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        if (author == null || author.isBlank()) return result;

        for (Book b : books) {
            if (b.getAuthor().toLowerCase().contains(author.toLowerCase())) result.add(b);
        }
        return result;
    }

    // --------------- Display ----------------
    public void showAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }

        LocalDate today = LocalDate.now();
        System.out.println("\n--- List of Books ---");
        for (Book b : books) {
            String status;
            if (b.isBorrowed()) {
                status = "Borrowed by: " + b.getBorrowedByUserId() + ", due: " + b.getDueDate();
                if (b.getDueDate().isBefore(today)) {
                    status += " (OVERDUE!)"; // highlight overdue
                }
            } else {
                status = "Available";
            }
            System.out.println(b + " | Status: " + status);
        }
    }


    public void showOverdueBooks() {
        LocalDate today = LocalDate.now();
        boolean hasOverdue = false;

        System.out.println("\n--- Overdue Books ---");
        for (Book b : books) {
            if (b.isBorrowed() && b.getDueDate().isBefore(today)) {
                hasOverdue = true;
                System.out.println(b + " | Borrowed by: " + b.getBorrowedByUserId() 
                                   + " | Due date: " + b.getDueDate());
            }
        }

        if (!hasOverdue) {
            System.out.println("No overdue books.");
        }
    }

    // --------------- Utilities ----------------
    public int countBorrowedBooksByUser(User user) {
        int count = 0;
        for (Book b : books) {
            if (b.isBorrowed() && user.getUserId().equals(b.getBorrowedByUserId())) {
                count++;
            }
        }
        return count;
    }

    public int size() {
        return books.size();
    }

    public boolean containsIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return false;
        for (Book b : books) {
            if (isbn.equalsIgnoreCase(b.getIsbn())) return true;
        }
        return false;
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
}

