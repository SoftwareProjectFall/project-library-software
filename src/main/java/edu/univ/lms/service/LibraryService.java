package edu.univ.lms.service;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service layer for all library-related business logic.
 * <p>
 * This class manages:
 * <ul>
 *     <li>Administrative operations (add, update, remove items)</li>
 *     <li>User borrowing and returning rules</li>
 *     <li>Overdue detection</li>
 *     <li>Fine calculation (Strategy Pattern handled inside {@link Book})</li>
 *     <li>Search utilities</li>
 * </ul>
 * <p>
 * The service does not handle user authentication or persistence,
 * which is delegated to {@code UserService} and repository classes.
 */
public class LibraryService {

    /** Standard message used when an item cannot be located. */
    private static final String ITEM_NOT_FOUND = "Item not found.";

    /** Internal list of books currently in the library system. */
    private List<Book> books = new ArrayList<>();

    /** Maximum number of items a regular user may borrow. */
    private int maxBorrowPerUser = 3;

    /** Counter used to auto-generate new ISBN values. */
    private int isbnCounter = 100;

    /**
     * Default constructor for creating a new library service.
     * Initializes an empty book list and default borrowing rules.
     */
    public LibraryService() {}

    /**
     * Updates the borrowing limit allowed per user.
     *
     * @param max maximum number of items a user may borrow
     */
    public void setMaxBorrowPerUser(int max) {
        this.maxBorrowPerUser = max;
    }

    /**
     * Generates the next ISBN number for a new library item.
     * <p>
     * ISBNs are simple auto-incrementing numbers for demonstration.
     *
     * @return generated ISBN as a string
     */
    private String generateIsbn() {
        isbnCounter++;
        return String.valueOf(isbnCounter);
    }

    /**
     * Loads and replaces the internal list of books using data restored from storage.
     *
     * @param loadedBooks list of books loaded from the repository
     */
    public void setItems(List<Book> loadedBooks) {
        this.books = (loadedBooks != null) ? loadedBooks : new ArrayList<>();
    }

    /**
     * Recomputes the ISBN counter based on the highest stored ISBN.
     * Must be called once after loading items from persistence.
     */
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

    // =========================================================
    // Admin Actions
    // =========================================================

    /**
     * Adds a new item to the library collection.
     * <p>
     * Only administrators are permitted to perform this action.
     * An auto-generated ISBN is assigned to the new item.
     *
     * @param user the admin performing the operation
     * @param book the book to add
     * @return true if the addition is successful, false otherwise
     */
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

    /**
     * Removes a book from the library system.
     * <p>
     * Validation includes:
     * <ul>
     *     <li>User must be an admin</li>
     *     <li>Item must exist</li>
     *     <li>Item must not be currently borrowed</li>
     * </ul>
     *
     * @param user admin performing the action
     * @param isbn ISBN of the item to remove
     * @return true if removed, false otherwise
     */
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

        System.out.println(ITEM_NOT_FOUND);
        return false;
    }

    /**
     * Updates the title or author of a stored item.
     * Only admins may perform the update.
     *
     * @param user      admin performing the update
     * @param isbn      ISBN of the target item
     * @param newTitle  new title (optional)
     * @param newAuthor new author (optional)
     * @return true if update is successful
     */
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

        System.out.println(ITEM_NOT_FOUND);
        return false;
    }

    // =========================================================
    // Borrowing / Returning
    // =========================================================

    /**
     * Allows a user to borrow an item if all borrowing conditions are met:
     * <ul>
     *     <li>User is logged in</li>
     *     <li>User is not an admin</li>
     *     <li>User has no overdue items</li>
     *     <li>User has no outstanding fines</li>
     *     <li>User has not exceeded the borrowing limit</li>
     *     <li>Item exists and is not already borrowed</li>
     * </ul>
     *
     * @param user the borrower
     * @param isbn ISBN of the item
     * @return true if the borrow operation succeeds
     */
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
            System.out.println("Borrowing denied. You have unpaid fines: " +
                    user.getFineBalance() + " NIS");
            return false;
        }

        Book book = searchBookByIsbn(isbn);
        if (book == null) {
            System.out.println(ITEM_NOT_FOUND);
            return false;
        }

        if (book.isBorrowed()) {
            System.out.println("Item is already borrowed.");
            return false;
        }

        LocalDate today = LocalDate.now();

        // Borrow duration: DVD = 7 days, Book/Journal = 28 days
        switch (book.getItemType()) {
            case "DVD":
                book.setDueDate(today.plusDays(7));
                break;
            case "Journal":
            case "Book":
            default:
                book.setDueDate(today.plusDays(28));
                break;
        }

        book.setBorrowed(true);
        book.setBorrowedByUserId(user.getUserId());
        book.setBorrowDate(today);

        System.out.println(user.getName() + " borrowed \"" +
                book.getTitle() + "\". Due: " + book.getDueDate());

        return true;
    }

    /**
     * Processes the return of a borrowed item.
     * <p>
     * This method:
     * <ul>
     *     <li>Validates ownership</li>
     *     <li>Checks for late returns</li>
     *     <li>Calculates overdue fines via Strategy Pattern</li>
     *     <li>Resets the borrowed state</li>
     * </ul>
     *
     * @param user returning user
     * @param isbn ISBN of the item
     * @return true if return is successful
     */
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
            System.out.println(ITEM_NOT_FOUND);
            return false;
        }

        if (!book.isBorrowed() ||
                !user.getUserId().equals(book.getBorrowedByUserId())) {

            System.out.println("This item was not borrowed by you.");
            return false;
        }

        LocalDate today = LocalDate.now();

        // Late return logic
        if (today.isAfter(book.getDueDate())) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS
                    .between(book.getDueDate(), today);

            double fine = book.calculateFine(overdueDays);
            user.addFine(fine);

            System.out.println("Late return! Overdue by " + overdueDays +
                    " days. Fine: " + fine + " NIS");
        }

        book.setBorrowed(false);
        book.setBorrowedByUserId(null);

        System.out.println("Item returned successfully.");
        return true;
    }

    // =========================================================
    // Search Utilities
    // =========================================================

    /**
     * Searches for an item by its exact ISBN.
     *
     * @param isbn ISBN string to search for
     * @return matching {@link Book}, or null if not found
     */
    public Book searchBookByIsbn(String isbn) {
        for (Book b : books) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) return b;
        }
        return null;
    }

    /**
     * Searches for books whose title contains the given keyword.
     * Search is case-insensitive and partial-match.
     *
     * @param title keyword to search for
     * @return list of matching books
     */
    public List<Book> searchBooksByTitle(String title) {
        List<Book> list = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(title.toLowerCase())) {
                list.add(b);
            }
        }
        return list;
    }

    /**
     * Searches for books whose author's name contains the given keyword.
     * Case-insensitive and partial-match.
     *
     * @param author keyword to search for
     * @return list of matching books
     */
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> list = new ArrayList<>();
        for (Book b : books) {
            if (b.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                list.add(b);
            }
        }
        return list;
    }

    // =========================================================
    // Display Methods
    // =========================================================

    /**
     * Displays all items in the library with basic status information,
     * including whether they are available, borrowed, or overdue.
     */
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

            if (b.isBorrowed() && b.getDueDate().isBefore(today)) {
                status += " (OVERDUE)";
            }

            System.out.println(
                    "ISBN: " + b.getIsbn() +
                    " | Title: " + b.getTitle() +
                    " | Author: " + b.getAuthor() +
                    " | Type: " + b.getItemType() +
                    " | " + status
            );
        }
    }

    /**
     * Displays all items currently borrowed by a specific user,
     * including titles and due dates.
     *
     * @param user the user reviewing their borrowed items
     */
    public void showBorrowedBooks(User user) {
        System.out.println("--- Your Items ---");
        for (Book b : books) {
            if (b.isBorrowed() &&
                    user.getUserId().equals(b.getBorrowedByUserId())) {

                System.out.println(
                        "ISBN: " + b.getIsbn() +
                        " | Title: " + b.getTitle() +
                        " | Due: " + b.getDueDate()
                );
            }
        }
    }

    /**
     * Displays all overdue items currently in the system.
     */
    public void showOverdueBooks() {
        LocalDate today = LocalDate.now();
        System.out.println("--- Overdue Items ---");

        for (Book b : books) {
            if (b.isBorrowed() && b.getDueDate().isBefore(today)) {
                System.out.println(b);
            }
        }
    }

    // =========================================================
    // Utility Methods
    // =========================================================

    /**
     * Determines whether a user has at least one overdue borrowed item.
     *
     * @param user user to check
     * @return true if the user has overdue items
     */
    public boolean hasOverdueBooks(User user) {
        LocalDate today = LocalDate.now();
        for (Book b : books) {
            if (b.isBorrowed() &&
                    user.getUserId().equals(b.getBorrowedByUserId()) &&
                    b.getDueDate().isBefore(today)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of books currently borrowed by the given user.
     *
     * @param user user whose borrowed items should be counted
     * @return number of books borrowed
     */
    public int countBorrowedBooksByUser(User user) {
        int count = 0;
        for (Book b : books) {
            if (b.isBorrowed() &&
                    user.getUserId().equals(b.getBorrowedByUserId())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Attempts to unregister a user from the system.
     * Validation ensures:
     * <ul>
     *     <li>Only admins can perform this action</li>
     *     <li>Admins cannot be unregistered</li>
     *     <li>Users with outstanding fines cannot be removed</li>
     *     <li>Users with borrowed items cannot be removed</li>
     * </ul>
     *
     * @param admin    administrator requesting removal
     * @param target   user to be removed
     * @param allUsers list of all registered users
     * @return true if the user is successfully removed
     */
    public boolean unregisterUser(User admin, User target, List<User> allUsers) {
        if (!admin.isAdmin()) return false;
        if (target.isAdmin()) return false;
        if (target.getFineBalance() > 0) return false;

        for (Book b : books) {
            if (b.isBorrowed() &&
                    b.getBorrowedByUserId().equals(target.getUserId())) {
                return false;
            }
        }

        return allUsers.remove(target);
    }

    /**
     * Returns an unmodifiable view of all books currently stored in the system.
     *
     * @return unmodifiable list of books
     */
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
}
