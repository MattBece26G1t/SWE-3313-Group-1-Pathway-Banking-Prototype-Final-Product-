/**
 * ActivityTrackerController manages the Activity Tracker screen.
 *
 * This screen displays the user's transaction history in reverse order,
 * with optional filtering by transaction type and account type.
 *
 * The controller is responsible for:
 * - setting up the filter dropdowns
 * - loading transaction history from the current user's BankingManager
 * - filtering records before displaying them
 * - building a visual log entry for each transaction
 * - handling navigation to other main sections and help
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ActivityTrackerController {

    // Filter dropdown for transaction category
    @FXML private ComboBox<String> transactionTypeFilter;
    // Filter dropdown for which account type the transaction belongs to
    @FXML private ComboBox<String> accountTypeFilter;
    // Main scroll container for the activity screen
    @FXML private ScrollPane activityScrollPane;
    // VBox that holds all generated transaction log entries
    @FXML private VBox activityList;

    // Current logged-in user
    private UserAccount currentUser;
    // Cached banking manager for quick access to transaction history
    private BankingManager bankingManager;

    /**
     * Runs automatically when the FXML file is loaded.
     *
     * This method:
     * - fills both filter dropdowns with available options
     * - sets default values
     * - connects filter changes to a reload of the activity list
     */
    @FXML
    private void initialize() {
        transactionTypeFilter.getItems().addAll(
                "All Transactions", "Deposits", "Withdrawals", "Transfers", "Payments"
        );
        transactionTypeFilter.setValue("All Transactions");

        accountTypeFilter.getItems().addAll(
                "All Accounts", "Debit", "Credit", "Savings"
        );
        accountTypeFilter.setValue("All Accounts");

        transactionTypeFilter.setOnAction(e -> loadActivity());
        accountTypeFilter.setOnAction(e -> loadActivity());
    }

    /**
     * Loads the user and banking data for this screen.
     * After storing the references, it immediately loads the visible activity list.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.bankingManager = user.getBankingManager();
        loadActivity();
    }

    /**
     * Rebuilds the activity list shown on screen.
     * This method clears the current entries, applies the selected filters,
     * and creates a new visual entry for each matching transaction.
     * Transactions are displayed newest first.
     */
    private void loadActivity() {
        activityList.getChildren().clear();

        String typeFilter = transactionTypeFilter.getValue();
        String accountFilter = accountTypeFilter.getValue();

        ArrayList<TransactionRecord> records = bankingManager.getTransactionHistory();

        // Walk backward so the newest transaction appears first
        for (int i = records.size() - 1; i >= 0; i--) {
            TransactionRecord record = records.get(i);

            // Skip transactions that do not match the selected type filter
            if (!matchesTypeFilter(record, typeFilter)) {
                continue;
            }

            // Skip transactions that do not match the selected account filter
            if (!matchesAccountFilter(record, accountFilter)) {
                continue;
            }

            HBox entry = createLogEntry(record);
            activityList.getChildren().add(entry);
        }

        // Show a fallback label if no matching transactions were found
        if (activityList.getChildren().isEmpty()) {
            Label empty = new Label("No activity to display.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");
            activityList.getChildren().add(empty);
        }
    }

    /**
     * Checks whether a transaction matches the currently selected
     * transaction type filter.
     * Returns true if the record should be shown.
     */
    private boolean matchesTypeFilter(TransactionRecord record, String filter) {
        if (filter.equals("All Transactions")) return true;
        if (filter.equals("Deposits") && record.getTransactionType().equals("Deposit")) return true;
        if (filter.equals("Withdrawals") && record.getTransactionType().equals("Withdrawal")) return true;
        if (filter.equals("Transfers") && record.getTransactionType().equals("Transfer")) return true;
        if (filter.equals("Payments") && record.getTransactionType().equals("Payment")) return true;
        return false;
    }

    /**
     * Checks whether a transaction matches the currently selected
     * account type filter.
     * The record stores account information as text, so this method
     * checks whether the associated account label contains the selected type.
     */
    private boolean matchesAccountFilter(TransactionRecord record, String filter) {
        if (filter.equals("All Accounts")) return true;
        String associated = record.getAssociatedAccount().toUpperCase();
        if (filter.equals("Debit") && associated.contains("DEBIT")) return true;
        if (filter.equals("Credit") && associated.contains("CREDIT")) return true;
        if (filter.equals("Savings") && associated.contains("SAVINGS")) return true;
        return false;
    }

    /**
     * Builds one visual log entry for a single transaction.
     * Each entry contains:
     * - a text summary of the transaction
     * - a colored circle showing whether the action was positive, negative, or neutral
     */
    private HBox createLogEntry(TransactionRecord record) {
        HBox entry = new HBox(10);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPrefHeight(45);
        entry.setPrefWidth(650);
        entry.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 8;");

        String text = record.getTransactionType() + " $" + String.format("%.2f", record.getAmount())
                + " - " + record.getAssociatedAccount()
                + " - " + record.getDate();

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Myanmar Text';");
        textLabel.setPrefWidth(590);

        Circle indicator = new Circle(10);

        // Color code the entry based on how the transaction was classified
        String status = record.getResultStatus();
        if (status.equals("Positive")) {
            indicator.setFill(Color.GREEN);
        }
        else if (status.equals("Negative")) {
            indicator.setFill(Color.RED);
        }
        else {
            indicator.setFill(Color.YELLOW);
        }

        entry.getChildren().addAll(textLabel, indicator);
        return entry;
    }

    // === Bottom Tab Navigations ===
    @FXML
    private void handleDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) activityScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountBalance() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("AccountBalanceView.fxml")
            );
            Stage stage = (Stage) activityScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            AccountBalanceController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDepositWithdraw() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DepositWithdrawView.fxml")
            );
            Stage stage = (Stage) activityScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DepositWithdrawController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActivityTracker() {
        // Already here
    }

    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) activityScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("activityTracker");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}