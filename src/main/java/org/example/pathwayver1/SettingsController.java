/**
 * SettingsController manages the Settings screen for the current user.
 *
 * This controller is responsible for displaying the user's saved profile
 * information, allowing that information to be edited, and saving any valid
 * changes back to the user's account.
 *
 * It also handles a few settings specific interface actions, such as:
 * - toggling password visibility
 * - opening the password reset screen
 * - updating the displayed difficulty after a DOB change
 * - returning to the Dashboard
 *
 * Most of the actual field validation still happens inside UserAccount.
 * This controller mainly collects the entered values, applies them to the
 * current user object, and reacts to success or validation errors.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SettingsController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipField;
    @FXML private TextField countryField;
    @FXML private TextField dobMonthField;
    @FXML private TextField dobDayField;
    @FXML private TextField dobYearField;

    @FXML private Label userIDLabel;
    @FXML private Label passwordLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    @FXML private ImageView eyeOpenIcon;
    @FXML private ImageView eyeClosedIcon;

    @FXML private ScrollPane settingsScrollPane;

    private UserAccount currentUser;  // User currently being edited on the settings page
    private boolean passwordVisible = false; // Tracks whether the password is currently shown in plain text

    /**
     * Initializes the Settings screen.
     * The status labels start hidden so the page only shows a message after
     * the user actually tries to save changes.
     */
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    /**
     * Loads the current user's saved information into the Settings form.
     * This method fills every editable field, updates the read only labels,
     * and makes sure the password is shown in masked form by default.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumberRaw());
        streetField.setText(user.getStreet());
        cityField.setText(user.getCity());
        stateField.setText(user.getState());
        zipField.setText(user.getZipCode());
        countryField.setText(user.getCountry());
        dobMonthField.setText(String.valueOf(user.getBMonth()));
        dobDayField.setText(String.valueOf(user.getBDay()));
        dobYearField.setText(String.valueOf(user.getBYear()));

        userIDLabel.setText(user.getUserID());
        difficultyLabel.setText(user.getDifficultyLevel().toUpperCase());
        maskPassword();

        passwordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
    }

    /**
     * Replaces the visible password with asterisks.
     * This keeps the password hidden until the user explicitly chooses
     * to reveal it with the eye icon.
     */
    private void maskPassword() {
        String dots = "";
        for (int i = 0; i < currentUser.getPassword().length(); i++) {
            dots += "*";
        }
        passwordLabel.setText(dots);
    }

    /**
     * Toggles whether the saved password is visible.
     * When visible, the real password text is shown.
     * When hidden, the password is shown as asterisks instead.
     * The eye icons are also swapped so the screen matches the current state.
     */
    @FXML
    private void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordLabel.setText(currentUser.getPassword());
            eyeOpenIcon.setVisible(true);
            eyeClosedIcon.setVisible(false);
        }

        else {
            maskPassword();
            eyeOpenIcon.setVisible(false);
            eyeClosedIcon.setVisible(true);
        }
    }

    /**
     * Saves changes made in the Settings form.
     *
     * This method reads the current field values and applies them back to the
     * UserAccount object. Validation is mostly handled by the setter methods
     * in UserAccount, so if any field is invalid, an IllegalArgumentException
     * is thrown and its message is shown to the user.
     * If the save succeeds:
     * - the difficulty label is recalculated in case DOB changed
     * - the updated user is saved through UserManager
     * - a success message appears briefly
     */
    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        try {
            currentUser.setFirstName(firstNameField.getText());
            currentUser.setLastName(lastNameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhoneNumber(phoneField.getText());
            currentUser.setStreet(streetField.getText());
            currentUser.setCity(cityField.getText());
            currentUser.setState(stateField.getText());
            currentUser.setZipCode(zipField.getText());
            currentUser.setCountry(countryField.getText());

            int month = UserAccount.parseBirthMonth(dobMonthField.getText());
            int day = UserAccount.parseBirthDay(dobDayField.getText());
            int year = UserAccount.parseBirthYear(dobYearField.getText());
            currentUser.setDOB(month, day, year);

            // DOB can affect the user's difficulty level, so refresh that label too
            difficultyLabel.setText(currentUser.getDifficultyLevel().toUpperCase());

            // Save the updated user data to file
            LoginController.getUserManager().saveUser(currentUser);

            successLabel.setVisible(true);
            successLabel.setText("SETTINGS CHANGED SUCCESSFULLY");

            successLabel.setVisible(true);
            successLabel.setText("SETTINGS CHANGED SUCCESSFULLY");

            // Hide the success message after a short delay
            Timeline delay = new Timeline(
                    new KeyFrame(Duration.seconds(3), e -> {
                        successLabel.setVisible(false);
                    })
            );
            delay.play();

        }

        catch (IllegalArgumentException e) {
            errorLabel.setVisible(true);
            errorLabel.setText(e.getMessage());
        }
    }

    /**
     * Opens the password reset screen from Settings.
     * In this path, the reset screen already knows which user is being edited,
     * so it can skip the normal login-side verification flow.
     */
    @FXML
    private void handleEditPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ResetPassView.fxml")
            );
            Stage stage = (Stage) settingsScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ResetPassController controller = loader.getController();
            controller.loadFromSettings(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns the user to the Dashboard screen
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) settingsScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the Help screen for the Settings section
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) settingsScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("settings");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}