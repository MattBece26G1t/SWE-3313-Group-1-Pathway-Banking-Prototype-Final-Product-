/**
 * Launcher is the simple entry point used to start the JavaFX application.
 *
 * This class does not contain any banking logic or screen logic itself.
 * Its only purpose is to call Application.launch and hand control over
 * to MainApp, which is the real application class.
 *
 * Keeping this file separate is useful because it gives the program a very
 * small and clean starting point.
 */

package org.example.pathwayver1;

import javafx.application.Application;

public class Launcher {
    /**
     * Starts the JavaFX application by launching MainApp.
     * Once this runs, JavaFX takes over and MainApp.start() becomes the
     * main setup method for the program.
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
