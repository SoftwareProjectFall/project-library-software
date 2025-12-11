package edu.univ.lms.service;

import edu.univ.lms.model.Book;
import edu.univ.lms.model.User;
import edu.univ.lms.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for sending overdue reminders to users.
 * <p>
 * This class implements the Observer Pattern, allowing multiple notification
 * channels (email, console printing, SMS, etc.) to subscribe and receive
 * automatically generated reminder messages.
 */
public class ReminderService {

    /** Registered observers that will receive reminder notifications. */
    private final List<Observer> observers = new ArrayList<>();

    // ---------------------------------------------------------
    // Observer management
    // ---------------------------------------------------------

    /**
     * Registers a new observer to receive overdue notifications.
     * <p>
     * Observers may include:
     * <ul>
     *     <li>EmailNotifier (console simulation)</li>
     *     <li>RealEmailNotifier (SMTP-based emails)</li>
     *     <li>Any other notification implementation</li>
     * </ul>
     *
     * @param observer observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer observer to remove
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // ---------------------------------------------------------
    // Reminder logic
    // ---------------------------------------------------------

    /**
     * Sends reminder messages to all users who currently have at least one overdue item.
     * <p>
     * Workflow:
     * <ol>
     *     <li>Scan all books in the library.</li>
     *     <li>Identify which items are overdue.</li>
     *     <li>Count overdue items per user.</li>
     *     <li>Generate a user-friendly message.</li>
     *     <li>Notify all registered observers (email, console, etc.).</li>
     * </ol>
     * <p>
     * Example message sent to observers:
     * <br>
     * <code>"You have 2 overdue item(s)."</code>
     *
     * @param libraryService library service providing access to all books
     * @param allUsers       list of all registered users to notify
     */
    public void sendOverdueReminders(LibraryService libraryService, List<User> allUsers) {
        LocalDate today = LocalDate.now();

        // Map: userId → overdue count
        Map<String, Integer> overdueCount = new HashMap<>();

        // Step 1 — Count overdue items per user
        for (Book item : libraryService.getAllBooks()) {
            if (item.isBorrowed() && item.getDueDate().isBefore(today)) {
                String borrowerId = item.getBorrowedByUserId();
                overdueCount.put(
                        borrowerId,
                        overdueCount.getOrDefault(borrowerId, 0) + 1
                );
            }
        }

        // Step 2 — Notify each affected user
        for (User user : allUsers) {
            if (overdueCount.containsKey(user.getUserId())) {
                int count = overdueCount.get(user.getUserId());
                String message = "You have " + count + " overdue item(s).";

                // Notify all observers (email, console, etc.)
                for (Observer observer : observers) {
                    observer.notify(user, message);
                }
            }
        }
    }
}
