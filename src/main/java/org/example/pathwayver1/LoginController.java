/**
 * LoginController manages the application's login screen.
 *
 * This controller is responsible for handling the first step of the user's
 * interaction with the program. It reads the entered credentials, checks them
 * through the shared UserManager, responds to failed login attempts, and
 * navigates the user to the correct next screen.
 *
 * In addition to normal login behavior, this class also:
 * - limits repeated failed attempts with a temporary lockout
 * - opens the registration screen
 * - opens the recover user ID screen
 * - opens the reset password screen
 * - closes the program when the user selects Exit
 *
 * Because multiple screens need access to the same registered users, this class
 * keeps a shared static UserManager instance that other controllers can use.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField userIDField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private int numOfAttempts = 0;  // Tracks how many failed login attempts happened in a row
    private int maxAttempts = 5; // After this many failed attempts, the login button is temporarily disabled
    @FXML private Button loginButton;

    // Shared UserManager instance — holds all registered accounts
    private static UserManager userManager = new UserManager();

    /**
     * Returns the shared UserManager instance.
     * This lets other controllers work with the same loaded user data
     * instead of creating separate managers with separate state.
     */
    public static UserManager getUserManager() {
        return userManager;
    }

    /**
     * Runs when the login screen is first loaded.
     * The error label starts hidden so the page looks clean until
     * the user actually does something wrong.
     */
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Handles the login button.
     *
     * Main flow here:
     * 1. read the entered user ID and password
     * 2. reject empty fields right away
     * 3. ask UserManager to authenticate the login
     * 4. if successful, switch to Dashboard and start background timers
     * 5. if unsuccessful, increase the failed attempt counter and possibly lock the button
     */
    @FXML
    private void handleLogin() {
        String id = userIDField.getText().trim();
        String pass = passwordField.getText();

        // Basic validation before even trying to authenticate
        if (id.isEmpty() || pass.isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Please enter both UserID and password.");
            return;
        }

        try {
            // Ask UserManager for a matching user account
            UserAccount user = userManager.login(id, pass);
            numOfAttempts = 0; // Successful login resets the failed attempt counter

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("DashboardView.fxml")
                );
                Stage stage = (Stage) userIDField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                // Pass the authenticated user into the dashboard
                DashboardController dashController = loader.getController();
                dashController.loadUserData(user);

                // Tell MainApp which user is currently active
                // Then start the repeating background checks tied to that user
                MainApp.setActiveUser(user);
                MainApp.startFeeChecker();
                MainApp.startMandatoryChecker();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        catch (IllegalArgumentException e) {
            numOfAttempts++;
            errorLabel.setVisible(true);

            // After too many failed attempts, temporarily lock the login button
            if (numOfAttempts >= maxAttempts) {
                errorLabel.setText("Too many failed attempts. Please try again in 30 seconds.");
                loginButton.setDisable(true);

                // Re-enable after 30 seconds
                Timeline lockout = new Timeline(
                        new KeyFrame(Duration.seconds(30), event -> {
                            loginButton.setDisable(false);
                            numOfAttempts = 0;
                            errorLabel.setText("You may try again.");
                        })
                );
                lockout.play();
            }
            else { // Otherwise just show the authentication error message
                errorLabel.setText(e.getMessage());
            }
        }
    }

    // Opens the registration screen
    @FXML
    private void handleRegister() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("RegistrationView.fxml")
        );
        Stage stage = (Stage) userIDField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    // Opens the user ID recovery screen.
    @FXML
    private void handleForgotUserID() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("RecoverIDView.fxml")
        );
        Stage stage = (Stage) userIDField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    // Opens the password reset screen.
    @FXML
    private void handleResetPassword() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("ResetPassView.fxml")
        );
        Stage stage = (Stage) userIDField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }

    /**
     * Fills the login fields with a user ID and password.
     * This is mainly used after registration so the new user can be sent
     * back to the login screen with their credentials already entered.
     */
    public void prefillCredentials(String id, String pass) {
        userIDField.setText(id);
        passwordField.setText(pass);
    }

    /**
     * Handles the Exit button.
     * The app only closes if the user confirms the exit popup.
     */
    @FXML
    private void handleExit() {
        if (MainApp.showExitConfirmation()) {
            Stage stage = (Stage) userIDField.getScene().getWindow();
            stage.close();
        }
    }

}