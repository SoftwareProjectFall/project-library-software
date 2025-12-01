package edu.univ.lms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 Represents the Library system.
 Handles user actions, admin actions, item persistence, borrowing rules,
 overdue detection, and strategy-based fines.
 */
public class Library {

    private List<Book> books = new ArrayList<>();
    private int maxBorrowPerUser = 3;

    private int isbnCounter = 100;

    public Library() {}

    public void setMaxBorrowPerUser(int max) {
        this.maxBorrowPerUser = max;
    }

    private String generateIsbn() {
        isbnCounter++;
        return String.valueOf(isbnCounter);
    }

    public void setItems(List<Book> loadedBooks) {
        this.books = loadedBooks != null ? loadedBooks : new ArrayList<>();
    }

    public void restoreIsbnCounter() {
        int max = 100;
        for (Book b : books) {
            try {
                int num = Integer.parseInt(b.getIsbn());
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        this.isbnCounter = max;
    }

    // ---------------- Admin actions ----------------

    public boolean addBook(User user, Book book) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can add items.");
            return false;
        }

        if (book == null) return false;

        String generatedISBN = generateIsbn();
        book.setIsbn(generatedISBN);

        books.add(book);
        System.out.println("Item added successfully with ISBN: " + generatedISBN);
        return true;
    }

    public boolean removeBook(User user, String isbn) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can remove items.");
            return false;
        }

        if (isbn == null || isbn.isBlank()) return false;

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                if (b.isBorrowed()) {
                    System.out.println("Cannot remove a borrowed item.");
                    return false;
                }
                books.remove(b);
                System.out.println("Item removed successfully.");
                return true;
            }
        }
        System.out.println("Item not found.");
        return false;
    }

    public boolean updateBook(User user, String isbn, String newTitle, String newAuthor) {
        if (!user.isLoggedIn() || !user.isAdmin()) {
            System.out.println("Only admins can update items.");
            return false;
        }

        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                if (newTitle != null && !newTitle.isBlank()) b.setTitle(newTitle);
                if (newAuthor != null && !newAuthor.isBlank()) b.setAuthor(newAuthor);
                System.out.println("Item updated successfully.");
                return true;
            }
        }
        System.out.println("Item not found.");
        return false;
    }

    // ---------------- Borrow / Return ----------------

    public boolean borrowBook(User user, String isbn) {

        if (!user.isLoggedIn()) {
            System.out.println("You must be logged in to borrow items.");
            return false;
        }

        if (user.isAdmin()) {
            System.out.println("Admins cannot borrow items.");
            return false;
        }

        if (countBorrowedBooksByUser(user) >= maxBorrowPerUser) {
            System.out.println("Borrow limit reached.");
            return false;
        }

        if (hasOverdueBooks(user)) {
            System.out.println("Borrowing denied. You have overdue items.");
            return false;
        }

        if (user.getFineBalance() > 0) {
            System.out.println("Borrowing denied. You have unpaid fines: " + user.getFineBalance() + " NIS");
            return false;
        }

        Book book = searchBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Item not found.");
            return false;
        }

        if (book.isBorrowed()) {
            System.out.println("Item is already borrowed.");
            return false;
        }

        LocalDate today = LocalDate.now();

        // ⭐⭐⭐ IMPORTANT CHANGE — DVD = 7 DAYS ⭐⭐⭐
        int borrowDays;
        String type = book.getItemType();

        if (type.equalsIgnoreCase("DVD")) {
            borrowDays = 7;   // DVD only 7 days
        } else {
            borrowDays = 28;  // Book and Journal = 28 days
        }

        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());
        book.setBorrowDate(today);
     // ---------------- Borrow Duration Logic ----------------
        switch (book.getItemType()) {
            case "DVD":
                book.setDueDate(today.plusDays(7));  // DVDs: 7 days
                break;

            case "Journal":
            case "Book":
            default:
                book.setDueDate(today.plusDays(28)); // Books & Journals: 28 days
                break;
        }


        System.out.println(user.getName() + " borrowed \"" + book.getTitle() + "\". Due: " + book.getDueDate());
        return true;
    }

    public boolean returnBook(User user, String isbn) {

        if (!user.isLoggedIn()) {
            System.out.println("You must be logged in to return items.");
            return false;
        }

        if (user.isAdmin()) {
            System.out.println("Admins cannot return items.");
            return false;
        }

        Book book = searchBookByIsbn(isbn);

        if (book == null) {
            System.out.println("Item not found.");
            return false;
        }

        if (!book.isBorrowed() || !user.getUserId().equals(book.getBorrowedByUserId())) {
            System.out.println("This item was not borrowed by you.");
            return false;
        }

        LocalDate today = LocalDate.now();

        if (today.isAfter(book.getDueDate())) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), today);
            double fine = book.calculateFine(overdueDays);
            user.addFine(fine);

            System.out.println("Late return! Overdue by " + overdueDays + " days. Fine: " + fine + " NIS");
        }

        book.setBorrowed(false);
        book.setBorrowedByUserId(null);

        System.out.println("Item returned successfully.");
        return true;
    }

    // ---------------- Searching ----------------

    public Book searchBookByIsbn(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) return b;
        }
        return null;
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> list = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())) list.add(b);
        }
        return list;
    }

    public List<Book> searchBooksByAuthor(String author) {
        List<Book> list = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().toLowerCase().contains(author.toLowerCase())) list.add(b);
        }
        return list;
    }

    // ---------------- Displays ----------------

    public void showAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No items in the library.");
            return;
        }

        LocalDate today = LocalDate.now();
        System.out.println("--- ALL ITEMS ---");

        for (Book b : books) {
            String status = b.isBorrowed()
                    ? "Borrowed | Due: " + b.getDueDate()
                    : "Available";

            if (b.isBorrowed() && b.getDueDate().isBefore(today)) status += " (OVERDUE)";

            System.out.println("ISBN: " + b.getIsbn() +
                               " | Title: " + b.getTitle() +
                               " | Author: " + b.getAuthor() +
                               " | Type: " + b.getItemType() +
                               " | " + status);
        }
    }

    public void showBorrowedBooks(User user) {
        System.out.println("--- Your Items ---");
        for (Book b : books) {
            if (b.isBorrowed() && user.getUserId().equals(b.getBorrowedByUserId())) {
                System.out.println("ISBN: " + b.getIsbn() +
                                   " | Title: " + b.getTitle() +
                                   " | Due: " + b.getDueDate());
            }
        }
    }

    public void showOverdueBooks() {
        LocalDate today = LocalDate.now();
        System.out.println("--- Overdue Items ---");

        for (Book b : books) {
            if (b.isBorrowed() && b.getDueDate().isBefore(today)) {
                System.out.println(b);
            }
        }
    }

    // ---------------- Utilities ----------------

    public boolean hasOverdueBooks(User user) {
        LocalDate today = LocalDate.now();
        for (Book b : books) {
            if (b.isBorrowed() &&
                user.getUserId().equals(b.getBorrowedByUserId()) &&
                b.getDueDate().isBefore(today)) return true;
        }
        return false;
    }

    public int countBorrowedBooksByUser(User user) {
        int count = 0;
        for (Book b : books) {
            if (b.isBorrowed() && user.getUserId().equals(b.getBorrowedByUserId())) count++;
        }
        return count;
    }

    public boolean unregisterUser(User admin, User target, List<User> allUsers) {
        if (!admin.isAdmin()) return false;
        if (target.isAdmin()) return false;
        if (target.getFineBalance() > 0) return false;

        for (Book b : books) {
            if (b.isBorrowed() && b.getBorrowedByUserId().equals(target.getUserId()))
                return false;
        }

        return allUsers.remove(target);
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
}
