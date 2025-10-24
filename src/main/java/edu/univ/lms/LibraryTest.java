package edu.univ.lms;

import java.time.LocalDate;

public class LibraryTest {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== LIBRARY TEST START ===\n");

        // Create users
        User admin = new User("U001", "Alice Admin", "alice", "admin123", true);
        User user1 = new User("U002", "Bob User", "bob", "user123", false);
        User user2 = new User("U003", "Charlie User", "charlie", "pass123", false);

        // Login users
        admin.login("alice", "admin123");
        user1.login("bob", "user123");
        user2.login("charlie", "pass123");

        // Create library
        Library library = new Library();

        // Admin adds books
        System.out.println("\n--- Admin Add Books ---");
        Book book1 = new Book("ISBN001", "Java Programming", "Author A");
        Book book2 = new Book("ISBN002", "Python Programming", "Author B");
        Book book3 = new Book("ISBN003", "C++ Programming", "Author C");

        library.addBook(admin, book1);
        library.addBook(admin, book2);
        library.addBook(admin, book3);

        // Attempt to add duplicate ISBN
        Book duplicateBook = new Book("ISBN001", "Duplicate Book", "Author X");
        library.addBook(admin, duplicateBook); // should fail

        // Normal user attempts to add book
        Book userBook = new Book("ISBN004", "User Book", "Author U");
        library.addBook(user1, userBook); // should fail

        // Show all books
        System.out.println("\n--- Show All Books ---");
        library.showAllBooks();

        // Update book
        System.out.println("\n--- Update Book ---");
        library.updateBook(admin, "ISBN002", "Python 101", null);
        library.updateBook(admin, "ISBN999", "Non-existent", null); // fail
        library.updateBook(user1, "ISBN003", "C++ Basics", null); // fail for non-admin

        // Remove book
        System.out.println("\n--- Remove Book ---");
        library.removeBook(admin, "ISBN003"); // remove C++ book
        library.removeBook(admin, "ISBN999"); // fail non-existent
        library.removeBook(user1, "ISBN001"); // fail non-admin

        // Show all books after update/remove
        System.out.println("\n--- Show All Books After Update/Remove ---");
        library.showAllBooks();

        // Borrow books
        System.out.println("\n--- Borrow Books ---");
        library.borrowBook(user1, "ISBN001"); // succeed
        library.borrowBook(user1, "ISBN002"); // succeed
        library.borrowBook(user1, "ISBN002"); // fail, already borrowed
        library.borrowBook(user2, "ISBN001"); // fail, already borrowed
        library.borrowBook(admin, "ISBN002"); // fail, admin cannot borrow

        // Borrow limit test
        Book book4 = new Book("ISBN004", "JavaScript Basics", "Author D");
        library.addBook(admin, book4);
        Book book5 = new Book("ISBN005", "HTML & CSS", "Author E");
        library.addBook(admin, book5);

        library.borrowBook(user1, "ISBN004"); // 3rd book, max
        library.borrowBook(user1, "ISBN005"); // exceed limit

        // Show all books with status
        System.out.println("\n--- Show All Books After Borrowing ---");
        library.showAllBooks();

        // Return books
        System.out.println("\n--- Return Books ---");
        library.returnBook(user1, "ISBN001"); // on-time
        library.returnBook(user1, "ISBN003"); // fail, removed
        library.returnBook(admin, "ISBN002"); // fail, admin cannot return
        library.returnBook(user2, "ISBN002"); // fail, not borrowed by user2

        // Artificially make a book overdue
        System.out.println("\n--- Overdue Book Test ---");
        book2.setDueDate(LocalDate.now().minusDays(5)); // overdue
        library.showAllBooks();
        library.showOverdueBooks();

        // Search tests
        System.out.println("\n--- Search Books ---");
        System.out.println("Search by ISBN 'ISBN002': " + library.searchBookByIsbn("ISBN002"));
        System.out.println("Search by Title 'Python': " + library.searchBooksByTitle("Python"));
        System.out.println("Search by Author 'Author A': " + library.searchBooksByAuthor("Author A"));

        // Count borrowed books
        System.out.println("\n--- Borrow Count ---");
        System.out.println(user1.getName() + " has borrowed " + library.countBorrowedBooksByUser(user1) + " books.");

        // Logout all users
        System.out.println("\n--- Logout Users ---");
        admin.logout();
        user1.logout();
        user2.logout();

        System.out.println("\n=== LIBRARY TEST END ===");
    }
}
