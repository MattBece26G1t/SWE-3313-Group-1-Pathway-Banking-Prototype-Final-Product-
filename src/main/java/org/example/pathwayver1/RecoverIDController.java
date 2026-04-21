/**
 * RecoverIDController manages the Recover User ID screen.
 *
 * This controller handles the flow where a user enters either their email
 * address or phone number and asks the system to find the matching account.
 * If a match is found, the controller displays the saved UserID. If no match
 * is found, it shows an error message instead.
 *
 * The controller also includes a short loading animation before showing the
 * result. That animation does not change the lookup itself, but it helps make
 * the recovery process feel more deliberate from the user's perspective.
 */

package org.example.pathwayver1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecoverIDController {

    @FXML private TextField emailOrPhoneField;
    @FXML private Label resultLabel;
    @FXML private Label loadingLabel;

    // Uses the same shared UserManager as the rest of the login-related screens
    private UserManager userManager = LoginController.getUserManager();

    /**
     * Initializes the Recover ID screen.
     * Both labels start hidden because the user has not attempted a lookup yet.
     * The loading label is only shown during the short animation, and the result
     * label is only shown after validation or lookup finishes.
     */
    @FXML
    private void initialize() {
        resultLabel.setVisible(false);
        loadingLabel.setVisible(false);
    }

    /**
     * Handles the Recover button.
     *
     * Main steps:
     * 1. read the value entered by the user
     * 2. reject empty input immediately
     * 3. show a short loading animation
     * 4. search for a matching user by email or phone
     * 5. display either the recovered UserID or an error message
     *
     * The actual search logic is handled by UserManager. This controller mainly
     * manages the screen behavior around that search.
     */
    @FXML
    private void handleRecover() {
        String value = emailOrPhoneField.getText().trim();

        // Do not start the animation if the user has not entered anything
        if (value.isEmpty()) {
            loadingLabel.setVisible(false);
            resultLabel.setVisible(true);
            resultLabel.setText("Please enter your email or phone number.");
            return;
        }

        // Hide any previous result and show loading
        resultLabel.setVisible(false);
        loadingLabel.setVisible(true);

        /*
         * This Timeline just updates the loading label with 1, 2, and 3 dots.
         * The total effect lasts a few seconds, then the actual lookup result
         * is shown when the animation finishes.
         */
        Timeline dotAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0), e ->
                        loadingLabel.setText("Retrieving information and verifying.")),
                new KeyFrame(Duration.seconds(0.5), e ->
                        loadingLabel.setText("Retrieving information and verifying..")),
                new KeyFrame(Duration.seconds(1.0), e ->
                        loadingLabel.setText("Retrieving information and verifying...")),
                new KeyFrame(Duration.seconds(1.5)) // pause so the third dot is visible
        );
        dotAnimation.setCycleCount(3); // 3 cycles × 1.5 seconds = 4.5 seconds total


         // Once the animation is done, perform the lookup and show the result.
         // The UserManager method accepts either email input or phone input.
        dotAnimation.setOnFinished(e -> {
            loadingLabel.setVisible(false);

            UserAccount found = userManager.findByEmailOrPhone(value);

            resultLabel.setVisible(true);
            if (found != null) {
                resultLabel.setText("Your UserID is: " + found.getUserID());
            } else {
                resultLabel.setText("No account found with that email or phone number.");
            }
        });

        dotAnimation.play();
    }

    /**
     * Sends the user back to the Login screen.
     * This does not save or modify anything. It only switches the scene.
     */
    @FXML
    private void handleBack() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LoginView.fxml")
        );
        Stage stage = (Stage) emailOrPhoneField.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
    }
}