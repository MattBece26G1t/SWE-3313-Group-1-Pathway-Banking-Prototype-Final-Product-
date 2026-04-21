/**
 * ScenarioController manages the Scenario Browser screen.
 * This controller is responsible for displaying all scenarios available to the
 * current user and letting the user filter them by category and completion
 * status. It also controls the mandatory scenario toggle and handles navigation
 * into the actual Scenario Activity screen.
 * One important part of this controller is that it does not simply show every
 * scenario in the system. It also checks the user's difficulty level so that
 * Child mode users do not see Advanced scenarios.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

import java.util.ArrayList;

public class ScenarioController {

    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> completionFilter;
    @FXML private ScrollPane scenarioScrollPane;
    @FXML private VBox scenarioList;
    @FXML private ImageView toggleOnIcon;
    @FXML private ImageView toggleOffIcon;

    private UserAccount currentUser; // Current logged-in user
    private ScenarioManager scenarioManager; // Handles scenario data such as filtering, completion state, and resets

    /**
     * Initializes the Scenario Browser screen.
     * This method fills the filter dropdowns with their options, chooses the
     * default selections, and makes both filters reload the scenario list
     * whenever the user changes one of them.
     */
    @FXML
    private void initialize() {
        categoryFilter.getItems().addAll(
                "All Scenarios", "Emergency Expense", "Everyday Spending",
                "Bills and Expenses", "Income Opportunity"
        );
        categoryFilter.setValue("All Scenarios");

        completionFilter.getItems().addAll(
                "Every Status", "Completed", "In Progress", "Available"
        );
        completionFilter.setValue("Every Status");

        categoryFilter.setOnAction(e -> loadScenarios());
        completionFilter.setOnAction(e -> loadScenarios());
    }

    /**
     * Loads the user-specific data needed for this screen.
     * After storing the current user and ScenarioManager, this method refreshes
     * the scenario list and updates the mandatory toggle icons so they match the
     * user's saved setting.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.scenarioManager = user.getScenarioManager();
        loadScenarios();
        toggleOnIcon.setVisible(currentUser.isMandatoryScenarioEnabled());
        toggleOffIcon.setVisible(!currentUser.isMandatoryScenarioEnabled());
    }

    /**
     * Rebuilds the scenario list using the currently selected filters.
     * This method checks:
     * - category filter
     * - completion filter
     * - difficulty restrictions for Child mode users
     * It also adds a reset/replay entry if every scenario has already been
     * completed, and it shows a fallback label if no scenarios match the filters.
     */
    private void loadScenarios() {
        scenarioList.getChildren().clear();

        String catFilter = categoryFilter.getValue();
        String compFilter = completionFilter.getValue();

        ArrayList<Scenario> allScenarios = scenarioManager.getAllScenarios();

        for (Scenario scenario : allScenarios) {

            // Skip anything that does not match the chosen category
            if (!catFilter.equals("All Scenarios") && !scenario.getCategory().equals(catFilter)) {
                continue;
            }

            // Child mode users are limited to Beginner scenarios only
            if (currentUser.getDifficultyLevel().equals("Child-mode") &&
                    scenario.getDifficultyLevel().equalsIgnoreCase("Advanced")) {
                continue;
            }

            // Skip anything that does not match the chosen completion status
            if (!compFilter.equals("Every Status") && !scenario.getCompletionStatus().equals(compFilter)) {
                continue;
            }

            HBox entry = createScenarioEntry(scenario);
            scenarioList.getChildren().add(entry);
        }

        /*
         * If every scenario has been completed, show one extra row that lets the
         * user reset them all and replay the full set.
         */
        boolean allCompleted = true;
        for (Scenario s : allScenarios) {

            if (!s.getCompletionStatus().equals("Completed")) {
                allCompleted = false;
                break;
            }
        }

        if (allCompleted) {
            HBox replayEntry = new HBox(10);
            replayEntry.setAlignment(Pos.CENTER);
            replayEntry.setPrefHeight(50);
            replayEntry.setPrefWidth(670);
            replayEntry.setStyle("-fx-background-color: rgba(164,224,178,0.3); -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

            Label replayLabel = new Label("All scenarios completed! Click here to reset and replay.");
            replayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Myanmar Text';");

            replayEntry.getChildren().add(replayLabel);

            replayEntry.setOnMouseClicked(e -> {
                for (Scenario s : allScenarios) {
                    scenarioManager.resetScenario(s);
                }
                loadScenarios();
            });

            scenarioList.getChildren().add(replayEntry);
        }

        // Fallback message if the filters leave nothing to display
        if (scenarioList.getChildren().isEmpty()) {
            Label empty = new Label("No scenarios match your filters.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");
            scenarioList.getChildren().add(empty);
        }
    }

    /**
     * Builds one visual row for a scenario in the browser.
     * Each row shows:
     * - difficulty badge
     * - title
     * - category
     * - colored completion square
     * - completion status text
     * Clicking the row opens that scenario in the activity screen.
     */
    private HBox createScenarioEntry(Scenario scenario) {
        HBox entry = new HBox(10);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPrefHeight(60);
        entry.setPrefWidth(670);
        entry.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        // Difficulty badge
        Label badge = new Label(scenario.getDifficultyLevel());
        badge.setPrefWidth(80);
        badge.setAlignment(Pos.CENTER);
        if (scenario.getDifficultyLevel().equalsIgnoreCase("Beginner")) {
            badge.setStyle("-fx-background-color: #A4E0B2; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3;");
        }

        else {
            badge.setStyle("-fx-background-color: #D66242; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3;");
        }

        // Main text area: title and category
        VBox titleBox = new VBox(2);

        Label titleLabel = new Label(scenario.getTitle());
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Myanmar Text';");

        Label categoryLabel = new Label(scenario.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #c49063; -fx-font-size: 11px; -fx-font-family: 'Myanmar Text';");

        titleBox.getChildren().addAll(titleLabel, categoryLabel);
        titleBox.setPrefWidth(400);

        // Colored square gives a quick visual status cue
        Rectangle statusSquare = new Rectangle(20, 20);
        String status = scenario.getCompletionStatus();
        if (status.equals("Completed")) {
            statusSquare.setFill(Color.GREEN);
        }

        else if (status.equals("In Progress")) {
            statusSquare.setFill(Color.YELLOW);
        }

        else {
            statusSquare.setFill(Color.GRAY);
        }

        // Status label
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-family: 'Myanmar Text';");

        // Arrow
        Label arrow = new Label(">");
        arrow.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        entry.getChildren().addAll(badge, titleBox, statusSquare, statusLabel, arrow);

        entry.setOnMouseClicked(e -> {
            openScenarioActivity(scenario);
        });

        return entry;
    }

    /**
     * Opens the Scenario Activity screen for the selected scenario.
     * The selected scenario object is passed directly into the next controller
     * after the scene switch so the activity screen knows what to display.
     */
    private void openScenarioActivity(Scenario scenario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ScenarioActivityView.fxml")
            );
            Stage stage = (Stage) scenarioScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ScenarioActivityController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.loadScenario(scenario);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the mandatory scenario toggle.
     * This switches the user's setting on or off, updates the visible icon,
     * and then shows a popup explaining the result.
     * When enabled, emergency scenarios may interrupt normal use later in the
     * session through the timer logic handled elsewhere in the application.
     */
    @FXML
    private void handleToggleMandatory() {
        boolean newState = !toggleOnIcon.isVisible();
        toggleOnIcon.setVisible(newState);
        toggleOffIcon.setVisible(!newState);
        currentUser.setMandatoryScenarioEnabled(newState);

        if (newState) {
            Alert alert = new Alert(Alert.AlertType.NONE, "", new ButtonType("Okay"));
            alert.setTitle("Mandatory Scenarios");
            alert.setHeaderText("Mandatory Scenarios Enabled");
            alert.setContentText("Emergency financial scenarios may now appear during normal use to test your decision-making skills. You can toggle this off at any time.");

            alert.setGraphic(new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(
                            getClass().getResourceAsStream("images/togglemandatory.drawio.png"),
                            52, 52, true, true
                    )
            ));

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #2b2b3d; -fx-font-family: 'Cascadia Mono';");
            dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #367A46;");
            dialogPane.lookup(".content").setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            // Prevent closing the popup from the window controls
            alert.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> event.consume());

            alert.showAndWait();
        }

        else {
            MainApp.showStyledPopup(
                    "togglemandatory.drawio.png", "#367A46",
                    "Mandatory Scenarios", "Mandatory Scenarios Disabled",
                    "Emergency scenarios will no longer interrupt your session.",
                    new ButtonType("Okay")
            );
        }
    }

    // Returns the user to the Dashboard
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) scenarioScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Help screen for the Scenario Browser.
     * The Help controller is told that the user came from the scenario section
     * so it can load the correct help content.
     */
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) scenarioScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("scenario");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}