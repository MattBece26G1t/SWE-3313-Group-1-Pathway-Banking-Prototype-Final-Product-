/**
 * ResetPassController manages the password reset workflow.
 *
 * This controller supports two different reset paths:
 * 1. the normal login-side reset flow, where the user verifies email first,
 *    then verifies date of birth, and finally enters a new password
 * 2. the settings-side reset flow, where the current logged-in user is already
 *    known, so the controller skips identity verification and goes straight to
 *    the new password screen
 *
 * The class is responsible for validating each step, controlling which pane is
 * visible, showing short loading animations during verification, and saving the
 * updated password once the reset is complete.
 */

package org.example.pathwayver1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;

public class ResetPassController {

    // === Panes ===
    @FXML private Pane step1Pane;
    @FXML private Pane step2Pane;
    @FXML private Pane dobPane;

    // === Step 1: Email ===
    @FXML private TextField emailField;
    @FXML private Label loadingLabel1;
    @FXML private Label errorLabel1;

    // === Step 1: DOB (inside dobPane) ===
    @FXML private TextField birthMonthField;
    @FXML private TextField birthDayField;
    @FXML private TextField birthYearField;
    @FXML private Label loadingLabel2;
    @FXML private Label errorLabel2;

    // === Step 2: New Password ===
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label credentialHintLabel;
    @FXML private Label errorLabel3;
    @FXML private Label successLabel;

    // Shared manager used by the login-related controllers
    private UserManager userManager = LoginController.getUserManager();

    /*
     * This flag tells the controller which part of the first step the user is on.
     * At first, the Check button verifies email.
     * After a valid email is found, the same Check button verifies date of birth.
     */
    private boolean emailVerified = false;

    /*
     * Once a user is found during verification, that account is stored here so
     * the controller can update its password later.
     */
    private UserAccount foundUser = null;

    /*
     * These fields are used when the password reset page is opened from Settings.
     * In that case, the controller should return to Settings instead of Login,
     * and it already knows which user is being edited.
     */
    private String cameFrom = "login";
    private UserAccount settingsUser = null;

    /**
     * Initializes the reset-password screen.
     *
     * The controller starts on step 1, with the DOB section hidden until the
     * email has been verified. All status labels also begin hidden so the screen
     * stays clean until the user actually interacts with it.
     *
     * This method also sets up hint text for the password fields so the user can
     * see the password requirements at the point where they are needed.
     */
    @FXML
    private void initialize() {
        // Start on the identity verification screen
        // Start on step 1 with DOB hidden
        step1Pane.setVisible(true);
        step2Pane.setVisible(false);
        dobPane.setVisible(false);

        // Hide all labels and messages until needed
        loadingLabel1.setVisible(false);
        loadingLabel2.setVisible(false);
        errorLabel1.setVisible(false);
        errorLabel2.setVisible(false);
        errorLabel3.setVisible(false);
        successLabel.setVisible(false);

        if (credentialHintLabel != null) {
            credentialHintLabel.setVisible(false);
        }

        /*
         * Show password rules when the user focuses the new password field.
         * This matches the registration flow and keeps the requirements visible
         * without permanently cluttering the screen.
         */
        newPasswordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                credentialHintLabel.setVisible(true);
                credentialHintLabel.setText(
                        "Password must:\n" +
                                "• Be 8-30 characters\n" +
                                "• Contain at least one letter\n" +
                                "• Contain at least one number\n" +
                                "• Contain at least one special character\n" +
                                "• Not contain spaces\n" +
                                "• Not match your UserID"
                );
            }
        });

        /*
         * When the confirm field gains focus, the hint changes to a simpler
         * reminder because the user is no longer choosing a password rule set.
         */
        confirmPasswordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                credentialHintLabel.setVisible(true);
                credentialHintLabel.setText(
                        "Re-enter your new password to confirm."
                );
            }
        });
    }

    // === Shared Check Button — behavior changes based on stage ===

    /**
     * Handles the shared Check button on the first screen.
     * The same button is reused for two different checks:
     * - before email verification, it checks the email
     * - after email verification, it checks the date of birth
     * This avoids needing a second button and keeps the workflow on one screen.
     */
    @FXML
    private void handleCheck() {
        if (!emailVerified) {
            checkEmail();
        } else {
            checkDOB();
        }
    }

    // === Email Verification ===

    /**
     * Verifies that the entered email belongs to an existing account.
     * If the email exists, the controller stores the matching user and reveals
     * the DOB fields. If it does not exist, an error message is shown instead.
     * A short loading animation is shown first so the screen does not change
     * instantly and the user can clearly see that a verification step occurred.
     */
    private void checkEmail() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            errorLabel1.setVisible(true);
            errorLabel1.setText("Please enter your email address.");
            return;
        }

        // Hide previous errors and show loading
        errorLabel1.setVisible(false);
        loadingLabel1.setVisible(true);
        loadingLabel1.setText("Retrieving information and verifying...");

        // Loading animation
        Timeline dotAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), e ->
                        loadingLabel1.setText("Retrieving information and verifying.")),
                new KeyFrame(Duration.seconds(0.5), e ->
                        loadingLabel1.setText("Retrieving information and verifying..")),
                new KeyFrame(Duration.seconds(1.0), e ->
                        loadingLabel1.setText("Retrieving information and verifying...")),
                new KeyFrame(Duration.seconds(1.5))
        );
        dotAnimation.setCycleCount(2);

        dotAnimation.setOnFinished(e -> {
            loadingLabel1.setVisible(false);

            foundUser = userManager.findByEmail(email);

            if (foundUser != null) {
                // Email matched a saved account, so reveal the DOB check section
                emailVerified = true;
                dobPane.setVisible(true);
            }
            else {
                errorLabel1.setVisible(true);
                errorLabel1.setText("No account found with that email.");
            }
        });

        dotAnimation.play();
    }

    // === DOB Verification ===

    /**
     * Verifies that the entered date of birth matches the account found by email.
     * If the date matches, the controller hides the verification pane and opens
     * the new password pane. If the date does not match, an error message is shown.
     */
    private void checkDOB() {
        // Clear previous errors
        errorLabel2.setVisible(false);

        try {
            int month = UserAccount.parseBirthMonth(birthMonthField.getText());
            int day = UserAccount.parseBirthDay(birthDayField.getText());
            int year = UserAccount.parseBirthYear(birthYearField.getText());

            // Show loading animation
            loadingLabel2.setVisible(true);
            loadingLabel2.setText("Verifying birthday...");

            Timeline dotAnimation = new Timeline(
                    new KeyFrame(Duration.seconds(0), e ->
                            loadingLabel2.setText("Verifying birthday.")),
                    new KeyFrame(Duration.seconds(0.5), e ->
                            loadingLabel2.setText("Verifying birthday..")),
                    new KeyFrame(Duration.seconds(1.0), e ->
                            loadingLabel2.setText("Verifying birthday...")),
                    new KeyFrame(Duration.seconds(1.5))
            );
            dotAnimation.setCycleCount(2);

            dotAnimation.setOnFinished(e -> {
                loadingLabel2.setVisible(false);

                if (foundUser.verifyDOB(month, day, year)) {
                    // Identity is fully verified, so move to the reset form
                    step1Pane.setVisible(false);
                    step2Pane.setVisible(true);
                }
                else {
                    errorLabel2.setVisible(true);
                    errorLabel2.setText("Date of birth does not match our records.");
                }
            });

            dotAnimation.play();

        }
        catch (IllegalArgumentException e) {
            errorLabel2.setVisible(true);
            errorLabel2.setText(e.getMessage());
        }
    }

    // === Reset Password ===
    /**
     * Applies the new password after the verification step is complete.
     *
     * This method validates that:
     * - the new password field is not empty
     * - the confirm field is not empty
     * - both password fields match
     * The actual password rules are enforced by UserAccount.setPassword().
     * If the password is accepted, the user is saved and a success message is shown.
     * After a short delay, the controller returns to the appropriate screen.
     */
    @FXML
    private void handleResetPassword() {
        try {
            String pass = newPasswordField.getText();
            String confirm = confirmPasswordField.getText();

            if (pass == null || pass.trim().isEmpty()) {
                throw new IllegalArgumentException("Password field cannot be empty.");
            }
            if (confirm == null || confirm.trim().isEmpty()) {
                throw new IllegalArgumentException("Confirm password field cannot be empty.");
            }
            if (!pass.equals(confirm)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            // Set the new password on the found user
            foundUser.setPassword(pass);

            /*
             * This project currently saves changes through MainApp.saveActiveUser().
             * When this screen is opened from Settings, that makes sense because
             * the edited user is the active user.
             */
            MainApp.saveActiveUser();

            // Hide error and show success
            errorLabel3.setVisible(false);
            successLabel.setVisible(true);
            successLabel.setText("Password reset success!");

            // Wait 3 seconds then return to login
            Timeline delay = new Timeline(
                    new KeyFrame(Duration.seconds(3), e -> {
                        try {
                            goToLogin();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    })
            );
            delay.play();

        }
        catch (IllegalArgumentException e) {
            errorLabel3.setVisible(true);
            errorLabel3.setText(e.getMessage());
        }
    }

    // === Cancel (with confirmation) ===

    /**
     * Cancels the reset flow after confirmation.
     * This uses the shared popup from MainApp so the behavior is consistent with
     * the rest of the application.
     */
    @FXML
    private void handleCancel() throws Exception {
        if (MainApp.showCancelResetPasswordConfirmation()) {
            goToLogin();
        }
    }

    // === Back (no confirmation needed — just returns to login) ===
    /**
     * Returns immediately without a confirmation popup.
     * This is used as a simple back action rather than a full cancel action.
     */
    @FXML
    private void handleBack() throws Exception {
        goToLogin();
    }

    // === Helper to return to login screen ===
    /**
     * Returns the user to the correct previous screen.
     * If the reset page was opened from Settings, the controller goes back to
     * Settings and reloads the current user's profile data.
     * Otherwise, it returns to the Login screen.
     */
    private void goToLogin() throws Exception {
        if ("settings".equals(cameFrom)) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("SettingsView.fxml")
            );
            Stage stage = (Stage) step2Pane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            SettingsController controller = loader.getController();
            controller.loadUserData(settingsUser);
        }
        else {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("LoginView.fxml")
            );
            Stage stage = (Stage) step1Pane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        }
    }

    // === Settings travel handling ===

    /**
     * Prepares the controller for the Settings-based reset flow.
     *
     * In this path, the current user is already known, so the controller does
     * not need to ask for email or date of birth again. It stores the settings
     * context, sets foundUser directly, and jumps straight to the password form.
     */
    public void loadFromSettings(UserAccount user) {
        this.cameFrom = "settings";
        this.settingsUser = user;
        this.foundUser = user;

        // Skip verification, go straight to password reset
        step1Pane.setVisible(false);
        step2Pane.setVisible(true);
    }
}