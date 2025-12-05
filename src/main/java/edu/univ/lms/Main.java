package edu.univ.lms;

import edu.univ.lms.controller.LibraryController;

/**
 * Main entry point for the Library Management System.
 * Uses layered architecture with Controller, Service, Repository, and Model layers.
 */
public class Main {

    public static void main(String[] args) {
        LibraryController controller = new LibraryController();
        controller.run();
    }
}
