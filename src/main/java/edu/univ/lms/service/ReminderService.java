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
 * This class uses the Observer Pattern so that multiple notification
 * channels can be plugged in (e.g., email, SMS, console simulation).
 * Observers are notified whenever a user has one or more overdue items.
 */
public class ReminderService {

    /** Registered observers that will receive reminder notifications. */
    private final List<Observer> observers = new ArrayList<>();

    // ---------------------------------------------------------
    // Observer management
    // ---------------------------------------------------------

    /**
     * Registers a new observer (e.g. EmailNotifier, RealEmailNotifier).
     *
     * @param observer observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Unregisters an existing observer.
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
     * Sends reminders to all users who currently have overdue items.
     * <p>
     * For each user with at least one overdue item, this method builds
     * a message of the form:
     * <br>
     * <code>"You have n overdue item(s)."</code>
     * <br>
     * and forwards it to every registered {@link Observer}.
     *
     * @param libraryService library service providing access to all books
     * @param allUsers       list of all registered users
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

        // Step 2 — Notify each user that has at least one overdue item
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
