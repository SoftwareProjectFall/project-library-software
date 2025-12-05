package edu.univ.lms.observer;

import edu.univ.lms.model.User;

/**
 * Simple notifier that simulates sending an email.
 * Good for testing without real email servers.
 */
public class EmailNotifier implements Observer {

    @Override
    public void notify(User user, String message) {
        System.out.println("[EMAIL SIMULATION] To: " + user.getEmail());
        System.out.println("Message: " + message);
        System.out.println("--------------------------------------");
    }
}

