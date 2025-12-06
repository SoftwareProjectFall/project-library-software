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
     * @param name     full name
     * @param username login username
     * @param password login password
     * @param isAdmin  {@code true} if the user is an administrator
     * @param email    email address for notifications
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
     *
     * @param amount amount to pay
     * @return {@code true} if the payment was accepted, {@code false} otherwise
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
     * Checks whether the provided credentials match this user's credentials.
     *
     * @param username username to check
     * @param password password to check
     * @return {@code true} if credentials match, {@code false} otherwise
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Attempts to log in the user with the given credentials.
     * Prints basic feedback to the console.
     *
     * @param username username entered by the user
     * @param password password entered by the user
     * @return {@code true} if login is successful, {@code false} otherwise
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
     * Logs the user out if they are currently logged in.
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
     * @return {@code true} if logged in, {@code false} otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    // ---------------------------------------------------------
    // toString
    // ---------------------------------------------------------

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
