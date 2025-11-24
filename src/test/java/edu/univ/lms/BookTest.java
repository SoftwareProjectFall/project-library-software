package edu.univ.lms;

import java.time.LocalDate;

public class BookTest {

    public static void main(String[] args) {
        System.out.println("=== BOOK TEST START ===\n");

        // 1. Create a book
        Book book = new Book("ISBN001", "Java Programming", "Author A");
        System.out.println("Created book: " + book);

        // 2. Test getters
        System.out.println("\n--- Test Getters ---");
        System.out.println("ISBN: " + book.getIsbn());          // ISBN001
        System.out.println("Title: " + book.getTitle());        // Java Programming
        System.out.println("Author: " + book.getAuthor());      // Author A
        System.out.println("Is Borrowed: " + book.isBorrowed()); // false
        System.out.println("Borrowed By: " + book.getBorrowedByUserId()); // null
        System.out.println("Borrow Date: " + book.getBorrowDate()); // null
        System.out.println("Due Date: " + book.getDueDate());       // null

        // 3. Test setters
        System.out.println("\n--- Test Setters ---");
        book.setTitle("Advanced Java");
        book.setAuthor("Author B");
        book.setBorrowed(true);
        book.setBorrowedByUserId("U001");
        LocalDate borrowDate = LocalDate.of(2025, 10, 24);
        LocalDate dueDate = borrowDate.plusDays(28);
        book.setBorrowDate(borrowDate);
        book.setDueDate(dueDate);

        // 4. Check updated values
        System.out.println("Updated Title: " + book.getTitle());    // Advanced Java
        System.out.println("Updated Author: " + book.getAuthor());  // Author B
        System.out.println("Is Borrowed: " + book.isBorrowed());    // true
        System.out.println("Borrowed By: " + book.getBorrowedByUserId()); // U001
        System.out.println("Borrow Date: " + book.getBorrowDate()); // 2025-10-24
        System.out.println("Due Date: " + book.getDueDate());       // 2025-11-21

        // 5. Test toString
        System.out.println("\n--- Test toString ---");
        System.out.println(book);

        // 6. Reset borrowed status
        book.setBorrowed(false);
        book.setBorrowedByUserId(null);
        book.setBorrowDate(null);
        book.setDueDate(null);

        System.out.println("\nAfter resetting borrow status:");
        System.out.println(book);

        // 7. Edge case: set blank title/author
        book.setTitle("");
        book.setAuthor("");
        System.out.println("\nAfter setting empty title/author:");
        System.out.println(book);

        System.out.println("\n=== BOOK TEST END ===");
    }
}
