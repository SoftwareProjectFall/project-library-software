package edu.univ.lms;

/**
  Represents a user of the library system.
 Handles login, logout, fine payments, and authentication.
 */
public class User {

    // Fields
    private String userId;        // Unique ID
    private String name;          // Full name
    private String username;      // Login username
    private String password;      // Login password (plain here; hashed in real systems)
    private boolean isAdmin;      // Admin flag
    private boolean loggedIn = false;
    private double fineBalance = 0.0;  // unpaid fines
    private String email;         // email for notifications

    // Constructors

    // Main constructor
    public User(String userId, String name, String username, String password, boolean isAdmin, String email) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.email = email;
    }

    // Needed for JSON serialization/deserialization
    public User() {}

    // Getters & Setters
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

    // Fine management

    public void addFine(double amount) {
        if (amount > 0) {
            fineBalance += amount;
        }
    }

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

    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public boolean login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login failed. Username and password cannot be empty.");
            return false;
        }

        if (authenticate(username, password)) {
            loggedIn = true;

            if (isAdmin)
                System.out.println("Login successful. Welcome, Admin " + name + "!");
            else
                System.out.println("Login successful. Welcome, " + name + "!");

            return true;
        }

        System.out.println("Login failed. Invalid username or password.");
        return false;
    }

    public void logout() {
        if (loggedIn) {
            loggedIn = false;
            System.out.println(name + " has logged out.");
        } else {
            System.out.println(name + " is not logged in.");
        }
    }

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
