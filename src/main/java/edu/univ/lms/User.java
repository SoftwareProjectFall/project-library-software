package edu.univ.lms;

public class User {

	private String userId;     // Unique ID for each user
	private String name;       // User's full name
	private String username;   // For login
	private String password;   // For login (hashed in real systems)
	private boolean isAdmin;   // True for admin accounts
	private boolean loggedIn = false;
	
	//Constructor
	public User(String userId, String name, String username, String password, boolean isAdmin) {
		this.userId=userId;
		this.name=name;
		this.username=username;
		this.password=password;
		this.isAdmin=isAdmin;
	}
	//setters and getters

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
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	

	// login method
	public boolean login(String username, String password) {
	    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
	        System.out.println("Login failed. Username and password cannot be empty.");
	        return false;
	    }

	    // Safe null check: call equals on the stored username/password
	    if (this.username != null && this.username.equals(username) &&
	        this.password != null && this.password.equals(password)) {
	        
	        loggedIn = true; // set session as active

	        if (this.isAdmin) {
	            System.out.println("Login successful. Welcome, Admin " + name + "!");
	        } else {
	            System.out.println("Login successful. Welcome, " + name + "!");
	        }
	        return true;
	    } else {
	        System.out.println("Login failed. Invalid username or password.");
	        return false;
	    }
	}


	// logout method
	public void logout() {
	    if (loggedIn) {
	        loggedIn = false; // close session
	        System.out.println(name + " has logged out.");
	    } else {
	        System.out.println(name + " is not logged in.");
	    }
	}

	// helper to check if the user is logged in
	public boolean isLoggedIn() {
	    return loggedIn;
	}












}
