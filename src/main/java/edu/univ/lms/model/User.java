package edu.univ.lms.model;

/**
 * Represents a user of the library system.
 * <p>
 * A user can be either a regular member or an administrator.
 * This class stores login credentials, role information, fines,
 * and email used for notifications.
 */
public class User {

    // ---------------------------------------------------------
    // Fields
    // ---------------------------------------------------------

    /** Unique identifier for the user. */
    private String userId;

    /** Full name of the user. */
    private String name;

    /** Username used for login. */
    private String username;

    /**
     * Password used for login.
     * <p>
     * Note: stored in plain text here for simplicity. In a real system,
     * passwords must be hashed and never stored in plain form.
     */
    private String password;

    /** Indicates whether this user has administrator privileges. */
    private boolean isAdmin;

    /** Indicates whether the user is currently logged in. */
    private boolean loggedIn = false;

    /** Outstanding fine balance for this user. */
    private double fineBalance = 0.0;

    /** Email address used for notifications and reminders. */
    private String email;

    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    /**
     * Creates a new user with the given attributes.
     *
     * @param userId   unique user identifier
     * @param name     full name of the user
     * @param username username used for login
     * @param password password used for login (plain text)
     * @param isAdmin  true if the user is an administrator
     * @param email    email used for notifications
     */
    public User(String userId, String name, String username, String password, boolean isAdmin, String email) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.email = email;
    }

    /**
     * No-arg constructor required for JSON serialization/deserialization.
     */
    public User() {
    }

    // ---------------------------------------------------------
    // Getters & Setters
    // ---------------------------------------------------------

    /**
     * Returns the unique ID of the user.
     *
     * @return user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Updates the unique identifier of the user.
     *
     * @param userId new user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the full name of the user.
     *
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the user's full name.
     *
     * @param name new full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the username used for login.
     *
     * @return login username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the username used for login.
     *
     * @param username new login username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the stored password.
     * <p>
     * Note: visible only for demonstration; real systems must hide or hash passwords.
     *
     * @return password in plain text
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * <p>
     * Note: in a real system this should store a hashed password instead.
     *
     * @param password new password in plain text
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks whether this user has administrative privileges.
     *
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Updates the user's administrative role.
     *
     * @param admin true to make user an admin
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Returns the email associated with the user.
     *
     * @return user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the email address used for notifications.
     *
     * @param email new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's outstanding fine balance.
     *
     * @return fine balance in NIS
     */
    public double getFineBalance() {
        return fineBalance;
    }

    // ---------------------------------------------------------
    // Fine management
    // ---------------------------------------------------------

    /**
     * Adds a fine amount to the user's outstanding balance.
     *
     * @param amount amount to add (must be positive)
     */
    public void addFine(double amount) {
        if (amount > 0) {
            fineBalance += amount;
        }
    }

    /**
     * Attempts to pay part or all of the user's fine balance.
     * Provides user feedback through console messages.
     *
     * @param amount amount to pay
     * @return true if payment was processed, false otherwise
     */
    public boolean payFine(double amount) {
        if (amount <= 0) {
            System.out.println("Payment must be positive.");
            return false;
        }

        if (fineBalance == 0) {
            System.out.println("You have no outstanding fines.");
            return false;
        }

        if (amount > fineBalance) {
            System.out.println("Payment exceeds fine balance. Paying full amount instead.");
            fineBalance = 0;
        } else {
            fineBalance -= amount;
        }

        System.out.println(name + " paid " + amount + " NIS. Remaining fines: " + fineBalance + " NIS");
        return true;
    }

    // ---------------------------------------------------------
    // Login / Logout
    // ---------------------------------------------------------

    /**
     * Compares the provided credentials with this user's stored credentials.
     *
     * @param username username to check
     * @param password password to check
     * @return true if credentials match, false otherwise
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Attempts to log in the user with given credentials.
     * Displays feedback indicating the login result.
     *
     * @param username username entered
     * @param password password entered
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login failed. Username and password cannot be empty.");
            return false;
        }

        if (authenticate(username, password)) {
            loggedIn = true;

            if (isAdmin) {
                System.out.println("Login successful. Welcome, Admin " + name + "!");
            } else {
                System.out.println("Login successful. Welcome, " + name + "!");
            }

            return true;
        }

        System.out.println("Login failed. Invalid username or password.");
        return false;
    }

    /**
     * Logs the user out and prints a confirmation message.
     */
    public void logout() {
        if (loggedIn) {
            loggedIn = false;
            System.out.println(name + " has logged out.");
        } else {
            System.out.println(name + " is not logged in.");
        }
    }

    /**
     * Indicates whether the user is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    // ---------------------------------------------------------
    // toString
    // ---------------------------------------------------------

    /**
     * Returns a text representation of the user, including identifiers,
     * login status, role, and fine balance.
     *
     * @return string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", isAdmin=" + isAdmin +
                ", email='" + email + '\'' +
                ", loggedIn=" + loggedIn +
                ", fineBalance=" + fineBalance +
                '}';
    }
}
