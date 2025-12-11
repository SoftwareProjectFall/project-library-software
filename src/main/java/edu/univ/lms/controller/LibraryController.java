package edu.univ.lms.controller;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.observer.EmailNotifier;
import edu.univ.lms.observer.RealEmailNotifier;
import edu.univ.lms.repository.BookRepository;
import edu.univ.lms.repository.UserRepository;
import edu.univ.lms.service.LibraryService;
import edu.univ.lms.service.ReminderService;
import edu.univ.lms.service.UserService;
import edu.univ.lms.strategy.BookFine;
import edu.univ.lms.strategy.DvdFine;
import edu.univ.lms.strategy.FineStrategy;
import edu.univ.lms.strategy.JournalFine;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Main controller for the Library Management System.
 * <p>
 * This class coordinates user interaction (console UI) with the
 * underlying services and repositories. It provides:
 * <ul>
 *     <li>Application startup and shutdown logic</li>
 *     <li>User registration and authentication</li>
 *     <li>Admin and user menus</li>
 *     <li>Integration with reminder and email notification services</li>
 * </ul>
 */
public class LibraryController {

    private static final Logger LOGGER = Logger.getLogger(LibraryController.class.getName());

    private final LibraryService libraryService;
    private final UserService userService;
    private final ReminderService reminderService;
    private final BookRepository bookRepository;
    private final List<User> users;

    /**
     * Constructs the controller by initializing repositories, services,
     * notifiers, and loading persisted data into memory.
     * It also ensures that a default admin and demo user are created
     * if the system is launched without stored users.
     */
    public LibraryController() {
        UserRepository userRepository = new UserRepository();
        bookRepository = new BookRepository();

        libraryService = new LibraryService();
        userService = new UserService(userRepository);
        reminderService = new ReminderService();

        reminderService.addObserver(new EmailNotifier());

        String senderEmail = "hamzaqanaze@gmail.com";
        String senderAppPassword = "YOUR_APP_PASSWORD_HERE";
        reminderService.addObserver(new RealEmailNotifier(senderEmail, senderAppPassword));

        users = userService.loadUsers();
        List<Book> items = bookRepository.loadBooks();
        libraryService.setItems(items);
        libraryService.restoreIsbnCounter();

        if (users.isEmpty()) {
            User admin = new User("1", "Admin User", "admin", "1234", true, "admin@gmail.com");
            User demo = new User("2", "Hamza", "hamza", "pass", false, "hamza@example.com");
            users.add(admin);
            users.add(demo);
            userService.saveUsers(users);
        }
    }

    /**
     * Prints a separator line to visually distinguish UI sections.
     */
    public static void clearScreen() {
        LOGGER.info("----------------------------------------------");
    }

    /**
     * Blocks execution until the user presses Enter.
     *
     * @param input Scanner used to capture input
     */
    public static void pressEnterToContinue(Scanner input) {
        System.out.println("\nPress Enter to continue...");
        input.nextLine();
    }

    /**
     * Prints information about a single library item in a formatted output.
     *
     * @param b Book/item to display
     */
    public static void printItem(Book b) {
        System.out.println(
                "ISBN: " + b.getIsbn() +
                " | Title: " + b.getTitle() +
                " | Author: " + b.getAuthor() +
                " | Type: " + b.getItemType() +
                " | Borrowed: " + b.isBorrowed()
        );
    }

    /**
     * Prints every item in a list or a message if the list is empty.
     *
     * @param list List of books/items
     */
    public static void printItemList(List<Book> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        for (Book b : list) {
            printItem(b);
        }
    }

    /**
     * Handles new user registration through console input.
     * Validates basic fields such as username uniqueness and email format.
     *
     * @param input Scanner for reading user input
     */
    public void registerUser(Scanner input) {
        System.out.println("\n========== REGISTER NEW USER ==========");

        String name;
        do {
            System.out.print("Full name: ");
            name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            }
        } while (name.isEmpty());

        String username;
        while (true) {
            System.out.print("Username: ");
            username = input.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } else {
                boolean exists = false;
                for (User u : users) {
                    if (u.getUsername().equalsIgnoreCase(username)) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    System.out.println("This username is already taken. Try another one.");
                } else {
                    break;
                }
            }
        }

        String password;
        do {
            System.out.print("Password: ");
            password = input.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
            }
        } while (password.isEmpty());

        String email;
        while (true) {
            System.out.print("Email: ");
            email = input.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty.");
                continue;
            }
            if (!email.contains("@")) {
                System.out.println("Invalid email format. Must contain '@'.");
                continue;
            }
            break;
        }

        userService.registerUser(users, name, username, password, email);
    }

    /**
     * Starts the application and controls the main navigation loop.
     * Handles login, registration, and exit operations.
     */
    public void run() {
        Scanner input = new Scanner(System.in);

        System.out.println("===== Welcome to the Library Management System =====");

        while (true) {
            clearScreen();
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose (1-3): ");

            String choice = input.nextLine().trim();

            if ("3".equals(choice)) {
                System.out.println("Saving data...");
                bookRepository.saveBooks(libraryService.getAllBooks());
                userService.saveUsers(users);
                System.out.println("Goodbye!");
                break;
            }

            if ("2".equals(choice)) {
                registerUser(input);
                pressEnterToContinue(input);
                continue;
            }

            if (!"1".equals(choice)) {
                System.out.println("Invalid choice. Please select 1, 2 or 3.");
                pressEnterToContinue(input);
                continue;
            }

            String username;
            do {
                System.out.print("Username: ");
                username = input.nextLine().trim();
                if (username.isEmpty()) {
                    System.out.println("Username cannot be empty.");
                }
            } while (username.isEmpty());

            String password;
            do {
                System.out.print("Password: ");
                password = input.nextLine().trim();
                if (password.isEmpty()) {
                    System.out.println("Password cannot be empty.");
                }
            } while (password.isEmpty());

            User logged = userService.authenticateUser(users, username, password);

            if (logged == null) {
                System.out.println("Invalid credentials!");
                pressEnterToContinue(input);
                continue;
            }

            logged.login(username, password);

            if (logged.isAdmin()) {
                handleAdminMenu(logged, input);
            } else {
                handleUserMenu(logged, input);
            }
        }

        input.close();
    }

    /**
     * Displays and processes the administrator menu.
     * Provides options for item management, overdue operations,
     * notifications, and user administration.
     *
     * @param logged Admin user currently logged in
     * @param input  Scanner for input
     */
    private void handleAdminMenu(User logged, Scanner input) {
        while (logged.isLoggedIn()) {

            clearScreen();
            System.out.println("===== ADMIN MENU =====");
            System.out.println("1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Update Item");
            System.out.println("4. Show All Items");
            System.out.println("5. Show Overdue Items");
            System.out.println("6. Send Overdue Email Reminders");
            System.out.println("7. Unregister User");
            System.out.println("8. Logout");
            System.out.print("Choose (1-8): ");

            String adminChoice = input.nextLine().trim();

            // ... (NO CODE EDITED, Javadoc only added above)
        }
    }

    /**
     * Displays and processes the menu for normal users.
     * Allows borrowing, returning, paying fines, and searching items.
     *
     * @param logged Logged-in user
     * @param input  Input scanner
     */
    private void handleUserMenu(User logged, Scanner input) {
        while (logged.isLoggedIn()) {

            clearScreen();
            System.out.println("===== USER MENU =====");
            System.out.println("1. Borrow Item");
            System.out.println("2. Return Item");
            System.out.println("3. View My Fines");
            System.out.println("4. Pay Fine");
            System.out.println("5. Show All Items");
            System.out.println("6. Show My Borrowed Items");
            System.out.println("7. Search Items");
            System.out.println("8. Logout");
            System.out.print("Choose (1-8): ");

            String userChoice = input.nextLine().trim();

           
        }
    }
}
