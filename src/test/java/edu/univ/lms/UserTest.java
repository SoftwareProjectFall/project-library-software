package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.model.User;

public class UserTest {

    // Simple helper to create a normal user
    private User createNormalUser() {
        return new User("1", "Mahmoud", "mahmoud", "1234", false, "mahmoud@test.com");
    }

    // ===== Getter / Setter tests =====

    @Test
    void gettersShouldReturnConstructorValues() {
        User user = new User("1", "Mahmoud", "mahmoud", "1234", false, "mahmoud@test.com");

        assertEquals("1", user.getUserId());
        assertEquals("Mahmoud", user.getName());
        assertEquals("mahmoud", user.getUsername());
        assertEquals("1234", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals("mahmoud@test.com", user.getEmail());
    }

    @Test
    void settersShouldUpdateAllFields() {
        User user = createNormalUser();

        user.setUserId("99");
        user.setName("Ahmad");
        user.setUsername("ahmad");
        user.setPassword("9999");
        user.setAdmin(true);
        user.setEmail("ahmad@test.com");

        assertEquals("99", user.getUserId());
        assertEquals("Ahmad", user.getName());
        assertEquals("ahmad", user.getUsername());
        assertEquals("9999", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals("ahmad@test.com", user.getEmail());
    }

    // ===== addFine & payFine =====

    @Test
    void addFine_shouldIncreaseBalanceForPositiveAmount() {
        User user = createNormalUser();
        user.addFine(10.0);
        user.addFine(5.5);

        assertEquals(15.5, user.getFineBalance());
    }

    @Test
    void addFine_shouldIgnoreNonPositiveAmounts() {
        User user = createNormalUser();
        user.addFine(0);
        user.addFine(-3);

        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void payFine_shouldFailWhenAmountNotPositive() {
        User user = createNormalUser();
        user.addFine(20.0);

        boolean result = user.payFine(0);
        assertFalse(result);
        assertEquals(20.0, user.getFineBalance());
    }

    @Test
    void payFine_shouldFailWhenNoOutstandingFines() {
        User user = createNormalUser();

        boolean result = user.payFine(10.0);
        assertFalse(result);
        assertEquals(0.0, user.getFineBalance());
    }

    @Test
    void payFine_shouldReduceFineBalanceNormally() {
        User user = createNormalUser();
        user.addFine(30.0);

        boolean result = user.payFine(10.0);

        assertTrue(result);
        assertEquals(20.0, user.getFineBalance());
    }

    @Test
    void payFine_shouldSetBalanceToZeroWhenOverpaying() {
        User user = createNormalUser();
        user.addFine(15.0);

        boolean result = user.payFine(30.0);

        assertTrue(result);
        assertEquals(0.0, user.getFineBalance());
    }

    // ===== authenticate / login / logout =====

    @Test
    void authenticate_shouldReturnTrueForCorrectCredentials() {
        User user = createNormalUser();
        assertTrue(user.authenticate("mahmoud", "1234"));
    }

    @Test
    void authenticate_shouldReturnFalseForWrongCredentials() {
        User user = createNormalUser();
        assertFalse(user.authenticate("wrong", "1234"));
        assertFalse(user.authenticate("mahmoud", "wrong"));
    }

    @Test
    void login_shouldSucceedWithCorrectCredentials() {
        User user = createNormalUser();
        boolean result = user.login("mahmoud", "1234");

        assertTrue(result);
        assertTrue(user.isLoggedIn());
    }

    @Test
    void login_shouldFailWithWrongCredentials() {
        User user = createNormalUser();
        boolean result = user.login("wrong", "1234");

        assertFalse(result);
        assertFalse(user.isLoggedIn());
    }

    @Test
    void login_shouldFailWhenUsernameOrPasswordEmpty() {
        User user = createNormalUser();

        assertFalse(user.login("", "1234"));
        assertFalse(user.login("mahmoud", ""));
        assertFalse(user.isLoggedIn());
    }

    @Test
    void logout_shouldSetLoggedInFalseIfUserWasLoggedIn() {
        User user = createNormalUser();
        user.login("mahmoud", "1234");
        assertTrue(user.isLoggedIn());

        user.logout();
        assertFalse(user.isLoggedIn());
    }

    @Test
    void logout_shouldNotCrashIfUserNotLoggedIn() {
        User user = createNormalUser();
        assertFalse(user.isLoggedIn());

        user.logout(); // should not change anything
        assertFalse(user.isLoggedIn());
    }

    // ===== toString =====

    @Test
    void toString_shouldContainBasicInfo() {
        User user = createNormalUser();
        user.addFine(10.0);
        user.login("mahmoud", "1234");

        String text = user.toString();

        assertTrue(text.contains("userId='1'"));
        assertTrue(text.contains("name='Mahmoud'"));
        assertTrue(text.contains("username='mahmoud'"));
        assertTrue(text.contains("email='mahmoud@test.com'"));
        assertTrue(text.contains("fineBalance=10.0"));
    }
}
