/*
 * This controller handles the detail page for the user's default debit account.
 *
 * The screen is pretty simple. It shows:
 * - the debit account number
 * - the current balance
 * - transaction history tied to that account
 *
 * From here the user can go back to the account balance screen
 * or open the help page.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DebitDetailController {

    @FXML private Label accountNumLabel;
    @FXML private Label balanceLabel;
    @FXML private ScrollPane historyScrollPane;
    @FXML private VBox historyList;

    private UserAccount currentUser; // Logged-in user for this screen

    @FXML
    private void initialize() {
    }

    /*
     * Loads the debit account info into the page.
     * Debit is always the first account in the user's account list,
     * so this page pulls from index 0.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;

        Account debit = user.getAccounts().get(0);
        accountNumLabel.setText("ACC#: " + debit.getAccountNumber());
        balanceLabel.setText(String.format("%.2f", debit.getBalance()));
        loadHistory();
    }

    /*
     * Builds the transaction history list for just the debit account.
     * The full banking history is filtered down by checking for the
     * debit account's last four digits inside each transaction label.
     */
    private void loadHistory() {
        historyList.getChildren().clear();

        String accountLabel = "DEBIT ACC - PB " + currentUser.getAccounts().get(0).getLastFour();

        ArrayList<TransactionRecord> records = currentUser.getBankingManager().getTransactionHistory();

        // Go backwards so the newest items show first
        for (int i = records.size() - 1; i >= 0; i--) {
            TransactionRecord record = records.get(i);

            if (record.getAssociatedAccount().contains(currentUser.getAccounts().get(0).getLastFour())) {
                javafx.scene.layout.HBox entry = new javafx.scene.layout.HBox(10);
                entry.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                entry.setPrefHeight(35);
                entry.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 8;");

                Label text = new Label(record.getTransactionType() + " $" +
                        String.format("%.2f", record.getAmount()) + " - " + record.getDate());
                text.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Myanmar Text';");

                entry.getChildren().add(text);
                historyList.getChildren().add(entry);
            }
        }

        // Fallback message if there is no history yet
        if (historyList.getChildren().isEmpty()) {
            Label empty = new Label("No transaction history.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Myanmar Text';");
            historyList.getChildren().add(empty);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("AccountBalanceView.fxml")
            );
            Stage stage = (Stage) balanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            AccountBalanceController controller = loader.getController();
            controller.loadUserData(currentUser);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) balanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("debitDetail");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}