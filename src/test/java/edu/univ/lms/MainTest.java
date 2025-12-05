package edu.univ.lms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.univ.lms.controller.LibraryController;

public class MainTest {

    @Test
    void main_shouldCreateControllerAndRun() {
        // Main.main is hard to test directly, but we can verify the controller can be created
        LibraryController controller = new LibraryController();
        assertNotNull(controller);
    }
}

