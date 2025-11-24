package edu.univ.lms;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class EmailTest {

    public static void main(String[] args) {

        // Create library and users
        Library library = new Library();

        User user1 = new User(
                "2",
                "Hamza",
                "hamza",
                "pass",
                false,
                "s12240246@stu.najah.edu"   // <<--- put your email here
        );

        User admin = new User(
                "1",
                "Admin User",
                "admin",
                "1234",
                true,
                "ADMIN_EMAIL@gmail.com"
        );

        admin.login("admin", "1234");
        user1.login("hamza", "pass");

        // Add a book and force it to be overdue
        library.addBook(admin, new Book("111", "Java Programming", "James"));
        library.borrowBook(user1, "111");

        // Make overdue manually
        Book borrowed = library.searchBookByIsbn("111");
        borrowed.setDueDate(LocalDate.now().minusDays(5));

        // Setup ReminderService
        ReminderService reminderService = new ReminderService();

        // Use REAL email notifier
        reminderService.addObserver(
                new RealEmailNotifier(
                        "hamzaqanaze@gmail.com",     // <<--- sender email
                        "lqdn nkfs vllh urem"         // <<--- gmail app password
                )
        );

        // List of users
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(admin);

        // Send reminders
        reminderService.sendOverdueReminders(library, users);

        System.out.println(">>> TEST COMPLETE. Check your email inbox!");
    }
}
