package edu.univ.lms;


public class UserTest {

    public static void main(String[] args) {
        // Create users
        User admin = new User("U001", "Alice Admin", "alice", "admin123", true);
        User normalUser = new User("U002", "Bob User", "bob", "user123", false);

        System.out.println("=== USER TEST START ===\n");

        // Test 1: Login with correct credentials
        System.out.println("Test 1: Correct login (Admin)");
        boolean adminLogin = admin.login("alice", "admin123"); // should succeed
        System.out.println("Expected: true, Actual: " + adminLogin);
        System.out.println("Is logged in? " + admin.isLoggedIn());
        System.out.println();

        System.out.println("Test 2: Correct login (Normal User)");
        boolean userLogin = normalUser.login("bob", "user123"); // should succeed
        System.out.println("Expected: true, Actual: " + userLogin);
        System.out.println("Is logged in? " + normalUser.isLoggedIn());
        System.out.println();

        // Test 2: Login with incorrect credentials
        System.out.println("Test 3: Incorrect login (Admin)");
        boolean failAdminLogin = admin.login("alice", "wrongpass"); // should fail
        System.out.println("Expected: false, Actual: " + failAdminLogin);
        System.out.println("Is logged in? " + admin.isLoggedIn());
        System.out.println();

        System.out.println("Test 4: Incorrect login (Normal User)");
        boolean failUserLogin = normalUser.login("bob", "wrongpass"); // should fail
        System.out.println("Expected: false, Actual: " + failUserLogin);
        System.out.println("Is logged in? " + normalUser.isLoggedIn());
        System.out.println();

        // Test 3: Logout
        System.out.println("Test 5: Logout (Admin)");
        admin.logout(); // should log out if previously logged in
        System.out.println("Is logged in? " + admin.isLoggedIn());
        System.out.println();

        System.out.println("Test 6: Logout (Normal User)");
        normalUser.logout();
        System.out.println("Is logged in? " + normalUser.isLoggedIn());
        System.out.println();

        // Test 4: Logout when not logged in
        System.out.println("Test 7: Logout without login (Admin)");
        admin.logout(); // already logged out
        System.out.println();

        System.out.println("Test 8: Logout without login (Normal User)");
        normalUser.logout();
        System.out.println();

        // Test 5: Edge cases
        System.out.println("Test 9: Login with empty username/password");
        User testUser = new User("U003", "Test User", "", "", false);
        boolean emptyLogin = testUser.login("", "");
        System.out.println("Expected: false, Actual: " + emptyLogin);
        System.out.println("Is logged in? " + testUser.isLoggedIn());
        System.out.println();

        System.out.println("=== USER TEST END ===");
    }
}

