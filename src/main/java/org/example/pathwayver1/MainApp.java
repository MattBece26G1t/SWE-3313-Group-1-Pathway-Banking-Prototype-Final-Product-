/**
 * MainApp is the central JavaFX application class for Pathway Banking.
 *
 * This class is responsible for launching the program, loading the first scene,
 * keeping track of the currently active user, and running the background timers
 * that support ongoing system behavior. Those timed behaviors include checking
 * for late fees, simulating random money transfers from contacts, and triggering
 * mandatory scenario interruptions.
 *
 * In addition, this class provides a group of reusable popup methods so the
 * rest of the program can display confirmation windows and styled alerts in a
 * consistent way.
 */

package org.example.pathwayver1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import java.util.Optional;

public class MainApp extends Application {

    // Repeating timer used to check late fees and random transfers
    private static javafx.animation.Timeline feeChecker;
    // Tracks whichever user is currently logged in
    private static UserAccount activeUser;
    // Repeating timer used for mandatory scenario interruptions
    private static javafx.animation.Timeline mandatoryChecker;

    /**
     * Stores the user who is currently logged in.
     * Other static methods in this class rely on activeUser so they know which
     * account to update while the program is running.
     */
    public static void setActiveUser(UserAccount user) {
        activeUser = user;
    }

    /**
     * Starts the background timer that handles recurring system checks.
     *
     * Every minute, this timer checks whether:
     * - credit accounts or subscriptions need late-fee processing
     * - a random contact transfer should occur
     *
     * If a previous timer already exists, it is stopped first so multiple
     * overlapping timers do not run at the same time.
     */
    public static void startFeeChecker() {

        if (feeChecker != null) {
            feeChecker.stop();
        }

        feeChecker = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.minutes(1), e -> {
                    if (activeUser != null) {
                        activeUser.getBankingManager().checkLateFees(activeUser);
                        activeUser.getBankingManager().randomContactTransfer(activeUser);
                    }
                })
        );

        feeChecker.setCycleCount(javafx.animation.Animation.INDEFINITE);
        feeChecker.play();
    }

    /**
     * Stops the fee checker timer and clears the active user.
     * This is mainly used during logout or shutdown so background updates do not
     * keep running after the session is supposed to end.
     */
    public static void stopFeeChecker() {
        if (feeChecker != null) {
            feeChecker.stop();
        }
        activeUser = null;
    }

    /**
     * JavaFX entry point.
     * This method loads the login screen, applies the first scene to the stage,
     * and also defines what should happen if the user tries to close the main
     * window directly using the window controls.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LoginView.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Pathway Banking");
        stage.setResizable(false);
        stage.setScene(scene);

        // Intercept the normal close event so the app can ask for confirmation
        stage.setOnCloseRequest(event -> {
            event.consume();

            if (showExitConfirmation()) {
                saveActiveUser(); // save

                // Close any extra popup windows that may still be open
                java.util.List<javafx.stage.Window> openWindows = new java.util.ArrayList<>(javafx.stage.Window.getWindows());
                for (javafx.stage.Window window : openWindows) {
                    if (window instanceof javafx.stage.Stage && window != stage) {
                        ((javafx.stage.Stage) window).close();
                    }
                }
                stage.close();
            }
        });

        stage.show();
    }

    /**
     * Shows the standard exit confirmation popup.
     * This method is reused in more than one place, including the main window
     * close event and the Exit button on the login page.
     */
    public static boolean showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "",
                new ButtonType("Yes"),
                new ButtonType("No"));
        alert.setTitle("Exit Application");
        alert.setHeaderText("Leaving so soon?");
        alert.setContentText("Are you sure you want to close out of the application?");

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        MainApp.class.getResourceAsStream("images/grnpig.png"),
                        52, 52, true, true
                )
        ));

        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );

        // Style the header and content text
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #C7FFC8;"
        );
        dialogPane.lookup(".content").setStyle(
                "-fx-text-fill: white; -fx-font-size: 14px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getText().equals("Yes");
    }

    // Shows the confirmation popup used when leaving the registration flow
    public static boolean showCancelRegistrationConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "",
                new ButtonType("Yes"),
                new ButtonType("No"));
        alert.setTitle("Cancel Registration");
        alert.setHeaderText("Leaving registration?");
        alert.setContentText("Are you sure you want to close out of registration?");

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        MainApp.class.getResourceAsStream("images/grnpig.png"),
                        52, 52, true, true
                )
        ));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );

        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #C7FFC8;"
        );
        dialogPane.lookup(".content").setStyle(
                "-fx-text-fill: white; -fx-font-size: 14px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getText().equals("Yes");
    }

    // Shows the confirmation popup used when leaving the password reset flow
    public static boolean showCancelResetPasswordConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "",
                new ButtonType("Yes"),
                new ButtonType("No"));
        alert.setTitle("Cancel Password Reset");
        alert.setHeaderText("Leaving password reset?");
        alert.setContentText("Are you sure you want to leave password reset?");

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        MainApp.class.getResourceAsStream("images/grnpig.png"),
                        52, 52, true, true
                )
        ));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );

        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #C7FFC8;"
        );
        dialogPane.lookup(".content").setStyle(
                "-fx-text-fill: white; -fx-font-size: 14px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getText().equals("Yes");
    }

    // Shows the standard logout confirmation popup
    public static boolean showLogoutConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "",
                new ButtonType("Yes"),
                new ButtonType("No"));
        alert.setTitle("Logout");
        alert.setHeaderText("Leaving so soon?");
        alert.setContentText("Are you sure you want to logout?");

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        MainApp.class.getResourceAsStream("images/grnpig.png"),
                        52, 52, true, true
                )
        ));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );

        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #C7FFC8;"
        );
        dialogPane.lookup(".content").setStyle(
                "-fx-text-fill: white; -fx-font-size: 14px;"
        );

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getText().equals("Yes");
    }

    // === ADDITIONAL POP UP METHODS FOR SPECIFIC AREAS OF THE PROGRAM ===


    /**
     * General reusable styled popup method.
     *
     * This method exists so the rest of the application can create custom
     * confirmation or message popups without repeating the same dialog styling
     * code each time.
     *
     * The caller provides:
     * - the icon file name
     * - the header color
     * - the title/header/content text
     * - the button options
     */
    public static Optional<ButtonType> showStyledPopup(String iconFileName, String headerColor,
                                                       String title, String headerText,
                                                       String contentText, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.NONE, "", buttons);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.setGraphic(new javafx.scene.image.ImageView( new javafx.scene.image.Image(MainApp.class.getResourceAsStream("images/" + iconFileName), 52, 52, true, true)));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2b2b3d;" + "-fx-font-family: 'Cascadia Mono';");

        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: " + headerColor + ";");
        dialogPane.lookup(".content").setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        return alert.showAndWait();
    }

    // === SPECIFIC POP UP AREAS ===

    /**
     * Starts the timer that checks for mandatory scenario interruptions.
     *
     * Every five minutes, this method checks whether:
     * - a user is logged in
     * - mandatory scenarios are enabled for that user
     * - there is an available mandatory scenario that fits the user's difficulty
     *
     * If so, it opens a required popup and then forces navigation to the
     * Scenario Activity screen.
     */
    public static void startMandatoryChecker() {
        if (mandatoryChecker != null) {
            mandatoryChecker.stop();
        }
        mandatoryChecker = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.minutes(5), e -> {
                    if (activeUser != null && activeUser.isMandatoryScenarioEnabled()) {
                        Scenario mandatory = activeUser.getScenarioManager().getMandatoryPendingForDifficulty(activeUser.getDifficultyLevel());

                        if (mandatory != null) {
                            javafx.application.Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.NONE, "", new ButtonType("Handle It"));
                                alert.setTitle("Emergency Scenario!");
                                alert.setHeaderText("Financial Emergency!");
                                alert.setContentText("\"" + mandatory.getTitle() + "\" — An emergency financial situation has come up! You must handle it now.");

                                alert.setGraphic(new javafx.scene.image.ImageView(
                                        new javafx.scene.image.Image(
                                                MainApp.class.getResourceAsStream("images/generalerror.drawio.png"),
                                                52, 52, true, true
                                        )
                                ));

                                DialogPane dialogPane = alert.getDialogPane();
                                dialogPane.setStyle("-fx-background-color: #2b2b3d; -fx-font-family: 'Cascadia Mono';");
                                dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #D66242;");
                                dialogPane.lookup(".content").setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

                                // Prevent the user from closing the popup without responding
                                alert.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());

                                alert.showAndWait();

                                try {
                                    javafx.stage.Stage currentStage = null;

                                    // Find the currently visible stage so the scene can be replaced
                                    for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                                        if (window instanceof javafx.stage.Stage && window.isShowing()) {
                                            currentStage = (javafx.stage.Stage) window;
                                            break;
                                        }
                                    }

                                    if (currentStage != null) {
                                        FXMLLoader loader = new FXMLLoader(
                                                MainApp.class.getResource("ScenarioActivityView.fxml")
                                        );
                                        currentStage.setScene(new javafx.scene.Scene(loader.load()));

                                        ScenarioActivityController controller = loader.getController();
                                        controller.loadUserData(activeUser);
                                        controller.loadScenario(mandatory);
                                    }
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }
                    }
                })
        );
        mandatoryChecker.setCycleCount(javafx.animation.Animation.INDEFINITE);
        mandatoryChecker.play();
    }

    // Stops the mandatory scenario timer
    public static void stopMandatoryChecker() {
        if (mandatoryChecker != null) {
            mandatoryChecker.stop();
        }
    }

    /**
     * Saves the currently active user to persistent storage.
     * Before saving, this method turns off the mandatory scenario setting.
     * Then it creates a DataManager and writes all current user data out to
     * the project's text-based storage files.
     */
    public static void saveActiveUser() {
        if (activeUser != null) {
            activeUser.setMandatoryScenarioEnabled(false);
            DataManager dm = new DataManager();
            dm.saveAll(activeUser);
        }
    }

    /**
     * Standard main method for launching the JavaFX application.
     * This simply forwards control to JavaFX, which will then call start().
     */
    public static void main(String[] args) {
        launch(args);
    }
}