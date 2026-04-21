/**
 * SavingsDetailController manages the detailed view for one savings account.
 *
 * This screen shows the selected savings account's basic information, including
 * its account number, current balance, withdrawal fee status, and transaction
 * history. It also gives the user access to actions related specifically to
 * savings accounts, such as deleting the account when allowed.
 *
 * A key part of this controller is the delete flow. If the savings account
 * still has money in it, the user cannot delete it immediately. Instead, the
 * controller sends the user into the transfer workflow first so the remaining
 * funds can be moved back into the default Debit account.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

public class SavingsDetailController {

    @FXML private Label accountNumLabel;
    @FXML private Label balanceLabel;
    @FXML private Label withdrawStatusLabel;
    @FXML private ScrollPane historyScrollPane;
    @FXML private VBox historyList;

    private UserAccount currentUser;  // Current logged-in user
    private Account savingsAccount; // The specific savings account being shown on this page


    /**
     * Initializes the screen.
     * No special startup logic is needed here because the page depends on
     * loadUserData() to know which user and which savings account it should show.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Loads the selected savings account into the screen.
     * This method fills in the main account details and also calculates the
     * current withdrawal fee status based on how many savings withdrawals or
     * transfers have already been counted on this account.
     */
    public void loadUserData(UserAccount user, Account account) {
        this.currentUser = user;
        this.savingsAccount = account;

        accountNumLabel.setText("ACC#: " + account.getAccountNumber());
        balanceLabel.setText(String.format("%.2f", account.getBalance()));

        int count = account.getWithdrawCount();

        /*
         * Savings accounts use a tiered transfer-fee system:
         * - first 3 are free
         * - next 6 cost $3
         * - after that cost $6
         *
         * This label gives the user a quick summary of where they currently are.
         */
        if (count < 3)
        {
            withdrawStatusLabel.setText(count + "/3 - Free");
        }
        else if (count < 9) {
            withdrawStatusLabel.setText((count - 3) + "/6 - $3 fee");
        }
        else {
            withdrawStatusLabel.setText("$6 fee");
        }

        loadHistory();
    }

    /**
     * Builds the transaction history list for this savings account only.
     * The user's full transaction history is filtered by checking whether the
     * associated account text contains this savings account's last four digits.
     * Matching records are then displayed newest first.
     */
    private void loadHistory() {
        historyList.getChildren().clear();

        ArrayList<TransactionRecord> records = currentUser.getBankingManager().getTransactionHistory();

        for (int i = records.size() - 1; i >= 0; i--) {
            TransactionRecord record = records.get(i);

            if (record.getAssociatedAccount().contains(savingsAccount.getLastFour())) {
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

        // Fallback message in case this account has no matching transactions yet
        if (historyList.getChildren().isEmpty()) {
            Label empty = new Label("No transaction history.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Myanmar Text';");
            historyList.getChildren().add(empty);
        }
    }

    /**
     * Handles deletion of the savings account.
     * There are two separate cases here:
     * 1. If the account still has money, the user must transfer those funds out first.
     * 2. If the balance is already zero, the account can be deleted directly after confirmation.
     * In the first case, this method opens the TransferAccount popup in a special
     * delete mode so that the selected savings account is transferred into the
     * default Debit account before the account is removed.
     */
    @FXML
    private void handleDelete() {
        double balance = savingsAccount.getBalance();

        if (balance > 0) {
            java.util.Optional<ButtonType> result = MainApp.showStyledPopup(
                    "deletesavings.drawio.png", "#C3BCEB",
                    "Delete Savings Account", "Transfer Required",
                    "Before you're able to delete, you must transfer your funds to DEBIT ACC - PB " +
                            currentUser.getAccounts().get(0).getLastFour() + ". Would you like to transfer?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("TransferAccountView.fxml")
                    );
                    Stage transferStage = new Stage();
                    transferStage.setTitle("");
                    transferStage.setResizable(false);
                    transferStage.initStyle(javafx.stage.StageStyle.UTILITY);
                    transferStage.setScene(new Scene(loader.load()));

                    TransferAccountController controller = loader.getController();
                    controller.loadUserData(currentUser);

                    /*
                     * Delete mode tells the transfer controller that this transfer
                     * is part of an account removal flow, not just a normal transfer.
                     * After the transfer completes, that controller can remove the
                     * savings account automatically.
                     */
                    controller.setDeleteMode(savingsAccount);

                    String savingsLabel = "SAVINGS ACC - PB " + savingsAccount.getLastFour();
                    String debitLabel = "DEBIT ACC - PB " + currentUser.getAccounts().get(0).getLastFour();

                    // Pre-fill the transfer so the user is guided directly into the required action
                    controller.prefillTransfer(savingsLabel, debitLabel);
                    controller.prefillAmount(savingsAccount.getBalance());

                    /*
                     * Once the transfer window closes, reload the account balance screen.
                     * That way the user immediately sees the updated account list.
                     */
                    transferStage.setOnHidden(e -> {
                        try {
                            FXMLLoader balanceLoader = new FXMLLoader(
                                    getClass().getResource("AccountBalanceView.fxml")
                            );
                            Stage stage = (Stage) balanceLabel.getScene().getWindow();
                            stage.setScene(new Scene(balanceLoader.load()));

                            AccountBalanceController balanceController = balanceLoader.getController();
                            balanceController.loadUserData(currentUser);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                    transferStage.show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If the account balance is already zero, only a confirmation is needed
        else {
            java.util.Optional<ButtonType> result2 = MainApp.showStyledPopup(
                    "deletesavings.drawio.png", "#C3BCEB",
                    "Delete Savings Account", "Delete Account",
                    "Are you sure you'd like to delete this savings account?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result2.isPresent() && result2.get().getText().equals("Yes")) {
                currentUser.getAccounts().remove(savingsAccount);

                MainApp.showStyledPopup(
                        "deletesavings.drawio.png", "#C3BCEB",
                        "Account Deleted", "Account Deleted",
                        "SAVINGS ACC - PB " + savingsAccount.getLastFour() + " deleted.",
                        new ButtonType("Okay")
                );

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
        }
    }

    // Returns the user to the main Account Balance screen.
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

    /**
     * Opens the Help screen and tags the request as coming from the
     * savings account detail page.
     */
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
            controller.setCameFrom("savingsDetail");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}