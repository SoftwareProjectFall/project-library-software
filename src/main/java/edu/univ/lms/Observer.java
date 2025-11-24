package edu.univ.lms;

/**
 * Observer interface for the Observer Pattern used by ReminderService.
 * Any notifier (Email, SMS, etc.) must implement this interface.
 */
public interface Observer {
    void notify(User user, String message);
}
