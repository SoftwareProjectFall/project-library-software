package edu.univ.lms;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // ------------------- Utility UI -------------------
    public static void clearScreen() {
        System.out.println("\n----------------------------------------------\n");
    }

    public static void pressEnterToContinue(Scanner input) {
        System.out.println("\nPress Enter to continue...");
        input.nextLine();
    }

    public static void printItem(Book b) {
        System.out.println(
                "ISBN: " + b.getIsbn() +
                " | Title: " + b.getTitle() +
                " | Author: " + b.getAuthor() +
                " | Type: " + b.getItemType() +
                " | Borrowed: " + b.isBorrowed()
        );
    }

    public static void printItemList(List<Book> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        for (Book b : list) {
            printItem(b);
        }
    }

    // ------------------- Register User -------------------
    public static void registerUser(List<User> users, Scanner input) {

        System.out.println("\n========== REGISTER NEW USER ==========");

        // Name
        String name;
        do {
            System.out.print("Full name: ");
            name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            }
        } while (name.isEmpty());

        // Username (must be unique)
        String username;
        while (true) {
            System.out.print("Username: ");
            username = input.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
                continue;
            }

            boolean exists = false;
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
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

        // Password
        String password;
        do {
            System.out.print("Password: ");
            password = input.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty.");
            }
        } while (password.isEmpty());

        // Email
        String email;
        while (true) {
            System.out.print("Email: ");
            email = input.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty.");
                continue;
            }
            if (email.indexOf('@') == -1) {
                System.out.println("Invalid email format. Must contain '@'.");
                continue;
            }
            break;
        }

        // userId = size+1 (simple demo)
        String userId = String.valueOf(users.size() + 1);
        User newUser = new User(userId, name, username, password, false, email);
        users.add(newUser);

        // Save after registration
        SaveLoadManager.saveUsers(users);

        System.out.println("User registered successfully. You can now login.");
    }

    // ------------------- MAIN -------------------
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        Library library = new Library();
        ReminderService reminderService = new ReminderService();
        reminderService.addObserver(new EmailNotifier());

        // ------------ LOAD DATA ON STARTUP ------------
        List<User> users = SaveLoadManager.loadUsers();
        List<Book> items = SaveLoadManager.loadItems();
        library.setItems(items);
        library.restoreIsbnCounter();

        // If no users, create default admin + demo user
        if (users.isEmpty()) {
            User admin = new User("1", "Admin User", "admin", "1234", true, "admin@gmail.com");
            User demo = new User("2", "Hamza", "hamza", "pass", false, "hamza@example.com");
            users.add(admin);
            users.add(demo);
            SaveLoadManager.saveUsers(users);
        }

        System.out.println("===== Welcome to the Library Management System =====");

        // ------------------- MAIN LOOP -------------------
        while (true) {

            clearScreen();
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose (1-3): ");

            String choice = input.nextLine().trim();

            if ("3".equals(choice)) {
                System.out.println("Saving data...");
                SaveLoadManager.saveItems(library.getAllBooks());
                SaveLoadManager.saveUsers(users);
                System.out.println("Goodbye!");
                break;
            }

            if ("2".equals(choice)) {
                registerUser(users, input);
                pressEnterToContinue(input);
                continue;
            }

            if (!"1".equals(choice)) {
                System.out.println("Invalid choice. Please select 1, 2 or 3.");
                pressEnterToContinue(input);
                continue;
            }

            // ------------------- LOGIN -------------------
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

            User logged = null;
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.authenticate(username, password)) {
                    logged = u;
                    break;
                }
            }

            if (logged == null) {
                System.out.println("Invalid credentials!");
                pressEnterToContinue(input);
                continue;
            }

            logged.login(username, password);

            // ===================== ADMIN MENU =====================
            if (logged.isAdmin()) {

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

                    if ("1".equals(adminChoice)) {
                        // Add Item
                        String title;
                        do {
                            System.out.print("Title: ");
                            title = input.nextLine().trim();
                            if (title.isEmpty()) {
                                System.out.println("Title cannot be empty.");
                            }
                        } while (title.isEmpty());

                        String author;
                        do {
                            System.out.print("Author: ");
                            author = input.nextLine().trim();
                            if (author.isEmpty()) {
                                System.out.println("Author cannot be empty.");
                            }
                        } while (author.isEmpty());

                        String type;
                        do {
                            System.out.println("Choose item type:");
                            System.out.println("1. Book");
                            System.out.println("2. DVD");
                            System.out.println("3. Journal");
                            System.out.print("Select (1-3): ");
                            type = input.nextLine().trim();
                            if (!("1".equals(type) || "2".equals(type) || "3".equals(type))) {
                                System.out.println("Invalid choice. Please enter 1, 2 or 3.");
                            }
                        } while (!("1".equals(type) || "2".equals(type) || "3".equals(type)));

                        FineStrategy fs;
                        if ("2".equals(type)) {
                            fs = new DvdFine();
                        } else if ("3".equals(type)) {
                            fs = new JournalFine();
                        } else {
                            fs = new BookFine();
                        }

                        Book newItem = new Book("", title, author, fs);
                        library.addBook(logged, newItem);

                        // Auto save items after adding
                        SaveLoadManager.saveItems(library.getAllBooks());

                        pressEnterToContinue(input);
                    }
                    else if ("2".equals(adminChoice)) {
                        // Remove Item
                        System.out.print("ISBN to remove: ");
                        String remIsbn = input.nextLine().trim();

                        library.removeBook(logged, remIsbn);
                        SaveLoadManager.saveItems(library.getAllBooks());

                        pressEnterToContinue(input);
                    }
                    else if ("3".equals(adminChoice)) {
                        // Update Item
                        System.out.print("ISBN to update: ");
                        String updIsbn = input.nextLine().trim();

                        System.out.print("New title (leave empty to keep same): ");
                        String newTitle = input.nextLine();

                        System.out.print("New author (leave empty to keep same): ");
                        String newAuthor = input.nextLine();

                        library.updateBook(logged, updIsbn, newTitle, newAuthor);
                        SaveLoadManager.saveItems(library.getAllBooks());

                        pressEnterToContinue(input);
                    }
                    else if ("4".equals(adminChoice)) {
                        library.showAllBooks();
                        pressEnterToContinue(input);
                    }
                    else if ("5".equals(adminChoice)) {
                        library.showOverdueBooks();
                        pressEnterToContinue(input);
                    }
                    else if ("6".equals(adminChoice)) {
                        reminderService.sendOverdueReminders(library, users);
                        pressEnterToContinue(input);
                    }
                    else if ("7".equals(adminChoice)) {
                        System.out.print("Enter userId to unregister: ");
                        String uid = input.nextLine().trim();

                        User target = null;
                        for (int i = 0; i < users.size(); i++) {
                            User u = users.get(i);
                            if (u.getUserId().equals(uid)) {
                                target = u;
                                break;
                            }
                        }

                        if (target != null) {
                            library.unregisterUser(logged, target, users);
                            SaveLoadManager.saveUsers(users);
                        } else {
                            System.out.println("User not found.");
                        }

                        pressEnterToContinue(input);
                    }
                    else if ("8".equals(adminChoice)) {
                        logged.logout();
                    }
                    else {
                        System.out.println("Invalid choice. Please select from 1 to 8.");
                        pressEnterToContinue(input);
                    }
                }
            }

            // ===================== USER MENU =====================
            else {

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

                    if ("1".equals(userChoice)) {
                        // Borrow
                        System.out.print("Enter ISBN: ");
                        String borrowIsbn = input.nextLine().trim();
                        library.borrowBook(logged, borrowIsbn);
                        SaveLoadManager.saveItems(library.getAllBooks());
                        pressEnterToContinue(input);
                    }
                    else if ("2".equals(userChoice)) {
                        // Return
                        System.out.print("Enter ISBN to return: ");
                        String returnIsbn = input.nextLine().trim();
                        library.returnBook(logged, returnIsbn);
                        SaveLoadManager.saveItems(library.getAllBooks());
                        pressEnterToContinue(input);
                    }
                    else if ("3".equals(userChoice)) {
                        System.out.println("Your fines: " + logged.getFineBalance() + " NIS");
                        pressEnterToContinue(input);
                    }
                    else if ("4".equals(userChoice)) {
                        double amount = -1;
                        while (amount <= 0) {
                            try {
                                System.out.print("Amount to pay: ");
                                String line = input.nextLine().trim();
                                amount = Double.parseDouble(line);
                                if (amount <= 0) {
                                    System.out.println("Amount must be positive.");
                                }
                            } catch (Exception e) {
                                System.out.println("Invalid number. Try again.");
                                amount = -1;
                            }
                        }
                        logged.payFine(amount);
                        SaveLoadManager.saveUsers(users);
                        pressEnterToContinue(input);
                    }
                    else if ("5".equals(userChoice)) {
                        library.showAllBooks();
                        pressEnterToContinue(input);
                    }
                    else if ("6".equals(userChoice)) {
                        library.showBorrowedBooks(logged);
                        pressEnterToContinue(input);
                    }
                    else if ("7".equals(userChoice)) {
                        // Search Items
                        System.out.println("\n============== SEARCH ITEMS ==============");
                        System.out.println("1. By Title");
                        System.out.println("2. By Author");
                        System.out.println("3. By ISBN");
                        System.out.print("Choose (1-3): ");
                        String s = input.nextLine().trim();

                        if ("1".equals(s)) {
                            System.out.print("Enter title: ");
                            String t = input.nextLine();
                            List<Book> byTitle = library.searchBooksByTitle(t);
                            printItemList(byTitle);
                        } else if ("2".equals(s)) {
                            System.out.print("Enter author: ");
                            String a = input.nextLine();
                            List<Book> byAuthor = library.searchBooksByAuthor(a);
                            printItemList(byAuthor);
                        } else if ("3".equals(s)) {
                            System.out.print("Enter ISBN: ");
                            String is = input.nextLine();
                            Book found = library.searchBookByIsbn(is);
                            if (found != null) {
                                printItem(found);
                            } else {
                                System.out.println("No item found.");
                            }
                        } else {
                            System.out.println("Invalid search option.");
                        }

                        pressEnterToContinue(input);
                    }
                    else if ("8".equals(userChoice)) {
                        logged.logout();
                    }
                    else {
                        System.out.println("Invalid choice. Please select from 1 to 8.");
                        pressEnterToContinue(input);
                    }
                }
            }
        }

        input.close();
    }
}
