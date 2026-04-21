/**
 * RegistrationController manages the multi-step registration process for a new user.
 *
 * This controller breaks registration into several screens so the user can enter
 * information in smaller sections instead of filling everything out at once.
 * The overall flow is:
 * welcome screen, name, contact information, address, date of birth,
 * confirmation, credentials, and final success screen.
 *
 * This class is responsible for validating each step before the user moves
 * forward, storing the collected values inside a temporary UserAccount object,
 * and then completing the full registration once the user creates a valid
 * user ID and password.
 *
 * It also supports going backward between steps, canceling registration,
 * and returning to the login screen with the new credentials already filled in.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class RegistrationController {

    // === Step Panes ===
    @FXML private Pane step0Pane;
    @FXML private Pane step1Pane;
    @FXML private Pane step2Pane;
    @FXML private Pane step3Pane;
    @FXML private Pane step4Pane;
    @FXML private Pane step5Pane;  // Confirmation
    @FXML private Pane step6Pane;  // Credentials
    @FXML private Pane step7Pane;  // Congratulations

    // === Step 1: Name ===
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    // === Step 2: Electronic Communications ===
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    // === Step 3: Address ===
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipField;
    @FXML private TextField countryField;

    // === Step 4: Date of Birth ===
    @FXML private TextField birthMonthField;
    @FXML private TextField birthDayField;
    @FXML private TextField birthYearField;

    // === Step 5: Confirmation (read-only TextFields) ===
    @FXML private TextField confirmFirstName;
    @FXML private TextField confirmLastName;
    @FXML private TextField confirmEmail;
    @FXML private TextField confirmPhone;
    @FXML private TextField confirmStreet;
    @FXML private TextField confirmCity;
    @FXML private TextField confirmState;
    @FXML private TextField confirmZip;
    @FXML private TextField confirmCountry;
    @FXML private TextField confirmDOB;

    // === Step 6: Credentials ===
    @FXML private TextField userIDField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label credentialHintLabel;

    // === Error Labels ===
    @FXML private Label errorLabel1;
    @FXML private Label errorLabel2;
    @FXML private Label errorLabel3;
    @FXML private Label errorLabel4;
    @FXML private Label errorLabel6;

    /*
     * Instead of manually choosing which error label to update every time,
     * this variable is pointed at the label that belongs to the current step.
     */
    private Label currentErrorLabel;

    // Shared user manager so registration adds users to the same collection used by login
    private UserManager userManager = LoginController.getUserManager();

    /*
     * This temporary UserAccount acts like a container while registration is in progress.
     * Each step stores validated values into this object. The final registration step
     * completes the remaining fields, creates the default account, and saves the user.
     */
    private UserAccount newUser = new UserAccount();

    /**
     * Initializes the registration screen.
     * This method starts the user on the first welcome step and sets up the hint
     * label behavior for the credential fields. The credential rules only appear
     * when the user focuses those fields, which keeps the earlier steps less cluttered.
     */
    @FXML
    private void initialize() {
        showStep(0);

        // Show UserID rules when user clicks into the userID field
        userIDField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                credentialHintLabel.setVisible(true);
                credentialHintLabel.setText(
                        "UserID must:\n" +
                                "• Be 6-15 characters\n" +
                                "• Start with a letter\n" +
                                "• Contain only letters, numbers, or underscores\n" +
                                "• Not start or end with an underscore"
                );
            }
        });

        // Show Password rules when user clicks into either password field
        passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
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
         * The confirm password field does not need the full password rules again.
         * It only needs to remind the user what they are doing on that field.
         */
        confirmPasswordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                credentialHintLabel.setVisible(true);
                credentialHintLabel.setText(
                        "Re-enter your password to confirm."
                );
            }
        });
    }

    // Moves the user from the welcome pane into the first real registration step.
    @FXML
    private void handleStartRegister() {
        showStep(1);
    }

    // === Step Navigation ===

    /**
     * Shows one registration step and hides all the others.
     *
     * This method is the center of the step navigation logic. It also updates
     * currentErrorLabel so the rest of the controller can display validation
     * messages in the correct place without repeating step-specific code.
     */
    private void showStep(int step) {
        step0Pane.setVisible(step == 0);
        step1Pane.setVisible(step == 1);
        step2Pane.setVisible(step == 2);
        step3Pane.setVisible(step == 3);
        step4Pane.setVisible(step == 4);
        step5Pane.setVisible(step == 5);
        step6Pane.setVisible(step == 6);
        step7Pane.setVisible(step == 7);

        // Point to the right error label for this step
        switch (step) {
            case 1: currentErrorLabel = errorLabel1; break;
            case 2: currentErrorLabel = errorLabel2; break;
            case 3: currentErrorLabel = errorLabel3; break;
            case 4: currentErrorLabel = errorLabel4; break;
            case 6: currentErrorLabel = errorLabel6; break;
        }

        // Hide all error labels when switching
        errorLabel1.setVisible(false);
        errorLabel2.setVisible(false);
        errorLabel3.setVisible(false);
        errorLabel4.setVisible(false);
        errorLabel6.setVisible(false);

        // Hide credential hints when switching steps
        if (credentialHintLabel != null) {
            credentialHintLabel.setVisible(false);
        }
    }

    // === Step 1: Name ===
    /**
     * Validates the name fields and moves to step 2 if they are valid.
     *
     * Validation is delegated to the UserAccount setter methods, so any
     * formatting problem immediately throws an exception with a user-friendly message.
     */
    @FXML
    private void handleNext1() {
        try {
            newUser.setFirstName(firstNameField.getText());
            newUser.setLastName(lastNameField.getText());

            showStep(2);

        } catch (IllegalArgumentException e) {
            currentErrorLabel.setVisible(true);
            currentErrorLabel.setText(e.getMessage());
        }
    }

    // === Step 2: Electronic Communications ===

    // Returns from the contact-information step to the name step.
    @FXML
    private void handleBack2() {
        showStep(1);
    }

    /**
     * Validates email and phone number, checks whether they are already in use,
     * and moves to step 3 if everything is valid.
     *
     * This step uses both UserAccount validation and UserManager uniqueness checks.
     * The first makes sure the format is acceptable, and the second makes sure the
     * information is not already attached to another account.
     */
    @FXML
    private void handleNext2() {
        try {
            newUser.setEmail(emailField.getText());
            if (userManager.isEmailTaken(emailField.getText())) {
                throw new IllegalArgumentException("Email already registered.");
            }

            newUser.setPhoneNumber(phoneField.getText());
            if (userManager.isPhoneTaken(phoneField.getText())) {
                throw new IllegalArgumentException("Phone number already registered.");
            }

            showStep(3);

        } catch (IllegalArgumentException e) {
            currentErrorLabel.setVisible(true);
            currentErrorLabel.setText(e.getMessage());
        }
    }

    // === Step 3: Address ===

    // Returns from the address step to the contact-information step.
    @FXML
    private void handleBack3() {
        showStep(2);
    }

    // Validates the address fields and moves to the date-of-birth step.
    @FXML
    private void handleNext3() {
        try {
            newUser.setStreet(streetField.getText());
            newUser.setCity(cityField.getText());
            newUser.setState(stateField.getText());
            newUser.setZipCode(zipField.getText());
            newUser.setCountry(countryField.getText());

            showStep(4);

        } catch (IllegalArgumentException e) {
            currentErrorLabel.setVisible(true);
            currentErrorLabel.setText(e.getMessage());
        }
    }

    // === Step 4: Date of Birth ===

    // Returns from the date-of-birth step to the address step.
    @FXML
    private void handleBack4() {
        showStep(3);
    }

    /**
     * Parses and validates the date of birth.
     * If the values are valid, this method also fills the confirmation screen
     * so the user can review everything before creating credentials.
     */
    @FXML
    private void handleNext4() {
        try {
            int month = UserAccount.parseBirthMonth(birthMonthField.getText());
            int day = UserAccount.parseBirthDay(birthDayField.getText());
            int year = UserAccount.parseBirthYear(birthYearField.getText());
            newUser.setDOB(month, day, year);

            populateConfirmation();
            showStep(5);

        } catch (IllegalArgumentException e) {
            currentErrorLabel.setVisible(true);
            currentErrorLabel.setText(e.getMessage());
        }
    }

    // === Step 5: Confirmation ===

    /**
     * Copies the values collected so far into the confirmation text fields.
     * This gives the user one review step before the system asks them to create
     * a user ID and password.
     */
    private void populateConfirmation() {
        confirmFirstName.setText(newUser.getFirstName());
        confirmLastName.setText(newUser.getLastName());
        confirmEmail.setText(newUser.getEmail());
        confirmPhone.setText(newUser.getPhoneNumberRaw());
        confirmStreet.setText(newUser.getStreet());
        confirmCity.setText(newUser.getCity());
        confirmState.setText(newUser.getState());
        confirmZip.setText(newUser.getZipCode());
        confirmCountry.setText(newUser.getCountry());
        confirmDOB.setText(newUser.getBMonth() + "/" + newUser.getBDay() + "/" + newUser.getBYear());
    }

    // Moves from the confirmation step to the credentials step.
    @FXML
    private void handleNext5() {
        // User confirmed everything looks good — move to credentials
        showStep(6);
    }

    /**
     * Returns from the confirmation step to the first editable step.
     * This lets the user revise earlier information if they notice something wrong.
     */
    @FXML
    private void handleBack5() {
        // User wants to go back and fix something — return to step 1
        showStep(1);
    }

    // === Step 6: Credentials ===

    // Returns from the credentials step back to the confirmation step.
    @FXML
    private void handleBack6() {
        showStep(5);
    }

    /**
     * Finalizes registration.
     *
     * This is the step where the temporary UserAccount becomes a real saved user.
     * The method:
     * - validates the user ID
     * - checks that the user ID is not already taken
     * - validates both password fields
     * - creates the default Debit account
     * - adds the welcome notification
     * - registers the user through UserManager
     * - saves the user to storage
     * - shows the final success screen
     */
    @FXML
    private void handleRegister() throws Exception {
        try {
            // Validate UserID
            newUser.setUserID(userIDField.getText());
            if (userManager.isUserIDTaken(userIDField.getText())) {
                throw new IllegalArgumentException("UserID already exists.");
            }

            // Validate passwords
            String pass = passwordField.getText();
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

            // Store password only after both fields are confirmed to match
            newUser.setPassword(pass);

            /*
             * Every new user starts with a default Debit account.
             * The constructor being used here creates the starting account balance
             * defined for a new account in the project.
             */
            newUser.addAccount(new Account(55));

            /*
             * Add a starter notification so the inbox is not empty and the user
             * gets a clear introduction to the system.
             */
            newUser.getNotificationManager().addNotification(new Notification(
                    "Welcome to Pathway Banking! Start by exploring your Dashboard and making your first deposit.",
                    "System",
                    "System",
                    java.time.LocalDate.now().toString()
            ));

            // Add the new user to the shared collection and write it to storage
            userManager.registerUser(newUser);
            userManager.saveUser(newUser);

            // Move to the success screen
            showStep(7);

        }
        catch (IllegalArgumentException e) {
            currentErrorLabel.setVisible(true);
            currentErrorLabel.setText(e.getMessage());
        }
    }

    // === Step 7: Congratulations ===

    /**
     * Returns to the login screen after successful registration.
     * The new user ID and password are prefilled so the user does not need to
     * type them again immediately after creating the account.
     */
    @FXML
    private void handleOkay() throws Exception {
        // Go to login screen with credentials pre-filled
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LoginView.fxml")
        );
        Scene scene = new Scene(loader.load());

        // Get the LoginController and pre-fill the credentials
        LoginController loginController = loader.getController();
        loginController.prefillCredentials(newUser.getUserID(), newUser.getPassword());

        Stage stage = (Stage) step7Pane.getScene().getWindow();
        stage.setScene(scene);
    }

    // === Cancel Registration ===

    // Cancels registration if the user confirms the action.
    @FXML
    private void handleCancel() throws Exception {
        if (MainApp.showCancelRegistrationConfirmation()) {
            goToLogin();
        }
    }

    // === Helper to return to login screen ===
    // This is used by both the cancel action and some other registration flow exits.
    private void goToLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LoginView.fxml")
        );
        Stage stage = (Stage) step0Pane.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}