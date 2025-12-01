package edu.univ.lms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  ReminderService sends notifications to users with overdue items.
Uses the Observer Pattern to support different types of notifiers (Email, SMS, etc.)
 */
public class ReminderService {

    // List of observers (email, SMS, etc.)
    private List<Observer> observers = new ArrayList<>();

    // Observer management

    /** Attach a new observer (e.g., EmailNotifier) */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /** Remove an existing observer */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Reminder logic

    /**
     * Sends reminders to users who have overdue items.
     * Message format: "You have n overdue book(s)."
     */
    public void sendOverdueReminders(Library library, List<User> allUsers) {
        LocalDate today = LocalDate.now();

        // Map: <UserID → overdue count>
        Map<String, Integer> overdueCount = new HashMap<>();

        // Step 1 — Count overdue books
        for (Book item : library.getAllBooks()) {
            if (item.isBorrowed() && item.getDueDate().isBefore(today)) {
                overdueCount.put(
                        item.getBorrowedByUserId(),
                        overdueCount.getOrDefault(item.getBorrowedByUserId(), 0) + 1
                );
            }
        }

        // Step 2 — Notify each user with overdue items
        for (User user : allUsers) {
            if (overdueCount.containsKey(user.getUserId())) {

                int count = overdueCount.get(user.getUserId());
                String message = "You have " + count + " overdue item(s).";

                // Notify all observers
                for (Observer observer : observers) {
                    observer.notify(user, message);
                }
            }
        }
    }
}
