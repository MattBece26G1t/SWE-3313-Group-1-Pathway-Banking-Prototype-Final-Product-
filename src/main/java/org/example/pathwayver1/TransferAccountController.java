/**
 * TransferAccountController manages the popup used for transfers between the
 * user's own bank accounts.
 *
 * This controller supports normal account to account transfers, but it is also
 * reused during the savings account deletion flow. In normal use, the user
 * chooses a source account, a destination account, and an amount. During the
 * deletion flow, the controller is prefilled so the remaining savings balance
 * can be moved into the default Debit account before the savings account is removed.
 *
 * A major part of this class is the savings transfer fee logic. If the user is
 * transferring money out of a Savings account, the controller checks the current
 * withdrawal count, determines whether a fee applies, and makes sure the account
 * has enough money to cover both the transfer and the fee when necessary.
 */

package org.example.pathwayver1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;

public class TransferAccountController {

    @FXML private ComboBox<String> fromAccountSelector;
    @FXML private ComboBox<String> toAccountSelector;
    @FXML private TextField transferAmountField;
    @FXML private Label resultLabel;

    private UserAccount currentUser; // Current logged-in user

    /*
     * This field appears to have been intended for tracking whether the first
     * savings transfer message was shown, but the current implementation uses
     * the flag stored in UserAccount instead.
     */
    private boolean firstSavingsTransferShown = false;

    /*
     * deleteMode changes the behavior of a successful transfer.
     * If true, the transfer is part of deleting a savings account, so the
     * account should be removed after the transfer finishes.
     */
    private boolean deleteMode = false;

    // If deleteMode is active, this is the account that should be removed afterward
    private Account accountToDelete = null;

    // If deleteMode is active, this is the account that should be removed afterward
    private BankingManager bankingManager;

    /**
     * Initializes the transfer popup.
     * The result label starts hidden and is only shown when the user completes
     * an action or when validation fails.
     */
    @FXML
    private void initialize() {
        resultLabel.setVisible(false);
    }

    /**
     * Loads the current user and prepares the account dropdowns.
     * Only Debit and Savings accounts are included here because this transfer
     * screen is meant for moving funds between regular balance accounts.
     * Credit accounts are intentionally excluded.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.bankingManager = user.getBankingManager();
        populateSelectors();
    }

    /**
     * Fills the From and To dropdowns with eligible account labels.
     * Each label uses the same format seen elsewhere in the program so the user
     * can easily recognize which account is being selected.
     */
    private void populateSelectors() {
        fromAccountSelector.getItems().clear();
        toAccountSelector.getItems().clear();

        for (Account account : currentUser.getAccounts()) {
            if (account.getAccountType().equalsIgnoreCase("Debit") ||
                    account.getAccountType().equalsIgnoreCase("Savings")) {
                String label = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
                fromAccountSelector.getItems().add(label);
                toAccountSelector.getItems().add(label);
            }
        }
    }

    /**
     * Handles the Transfer button.
     * This method performs the full transfer workflow:
     * 1. validate the selected accounts and entered amount
     * 2. locate the matching Account objects
     * 3. check for sufficient funds
     * 4. apply any savings transfer fee rules when needed
     * 5. call BankingManager to complete the transfer
     * 6. either close the popup or briefly show a success message
     * If the popup is being used for savings account deletion, a successful
     * transfer also removes the savings account and closes the window.
     */
    @FXML
    private void handleTransfer() {
        resultLabel.setVisible(false);

        String from = fromAccountSelector.getValue();
        String to = toAccountSelector.getValue();
        String amountText = transferAmountField.getText().trim();

        if (from == null || to == null) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Please select both accounts.");
            return;
        }

        if (from.equals(to)) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Cannot transfer to the same account.");
            return;
        }

        if (amountText.isEmpty()) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Please enter an amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
                resultLabel.setText("Please enter a valid amount.");
                return;
            }

            Account fromAccount = findAccountByLabel(from);
            Account toAccount = findAccountByLabel(to);

            if (fromAccount == null || toAccount == null) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
                resultLabel.setText("Account not found.");
                return;
            }

            if (fromAccount.getBalance() < amount) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
                resultLabel.setText("Insufficient funds in selected account.");
                return;
            }

            System.out.println("Account type: " + fromAccount.getAccountType());
            System.out.println("Withdraw count: " + fromAccount.getWithdrawCount());

            /*
             * Savings transfer rules apply only when money is moving out of
             * Savings during a normal transfer. The deletion flow skips this
             * branch because that transfer is handled as a required cleanup step.
             */
            if (fromAccount.getAccountType().equalsIgnoreCase("Savings") && !deleteMode)  {

                /*
                 * The user sees the fee explanation only the first time this kind
                 * of transfer happens. That flag is stored on the user so it can
                 * persist across page openings.
                 */
                if (!currentUser.isSavingsTransferInfoShown()) {
                    MainApp.showStyledPopup(
                            "savingsfeewarn.drawio.png", "#D66242",
                            "Savings Transfer", "Good to know!",
                            "Your first 3 transfers from savings are free. After that, a $3 fee applies for the next 6 transfers, then $6 per transfer going forward.",
                            new ButtonType("Okay")
                    );
                    currentUser.setSavingsTransferInfoShown(true);
                }

                int withdrawCount = fromAccount.getWithdrawCount();
                double fee = 0;
                String feeMessage = "";

                // Determine which fee tier the user is currently in
                if (withdrawCount < 3) {
                    fee = 0;
                    feeMessage = "This transfer is free. You have " + (2 - withdrawCount) + " free transfers remaining.";
                }
                else if (withdrawCount < 9) {
                    fee = 3;
                    feeMessage = "A $3.00 fee will be applied to this transfer.";
                }
                else {
                    fee = 6;
                    feeMessage = "A $6.00 fee will be applied to this transfer.";
                }

                /*
                 * If a fee applies, make sure the account can cover the transfer
                 * and the fee together. There is one exception here: if the
                 * remaining balance is too small to cover the fee alone, the fee
                 * is effectively waived by the current logic.
                 */
                if (fee > 0) {
                    if (fromAccount.getBalance() <= fee) {
                        // Waive the fee — balance too low to cover it
                    }
                    else if (fromAccount.getBalance() < amount + fee) {
                        resultLabel.setVisible(true);
                        resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
                        resultLabel.setText("Insufficient funds to cover transfer plus $" + String.format("%.2f", fee) + " fee.");
                        return;
                    }
                    else {
                        Optional<ButtonType> feeResult = MainApp.showStyledPopup(
                                "savingsfeewarn.drawio.png", "#D66242",
                                "Transfer Fee", "Savings Transfer Fee",
                                feeMessage + " Do you wish to proceed?",
                                new ButtonType("Yes"), new ButtonType("No")
                        );

                        if (!feeResult.isPresent() || !feeResult.get().getText().equals("Yes")) {
                            return;
                        }

                        fromAccount.withdraw(fee);
                    }
                }

                // Count this as one savings withdrawal/transfer
                fromAccount.incrementWithdrawCount();
            }

            // Complete the actual movement of funds
            bankingManager.processTransfer(fromAccount, toAccount, amount);

            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #16911b; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Transfer successful!");

            /*
             * In delete mode, the transfer is immediately followed by removing
             * the savings account that was emptied.
             */
            if (deleteMode && accountToDelete != null) {
                currentUser.getAccounts().remove(accountToDelete);

                MainApp.showStyledPopup(
                        "deletesavings.drawio.png", "#C3BCEB",
                        "Account Deleted", "Account Deleted",
                        "SAVINGS ACC - PB " + accountToDelete.getLastFour() + " deleted. Funds transferred to DEBIT ACC - PB " + currentUser.getAccounts().get(0).getLastFour() + ".",
                        new ButtonType("Okay")
                );

                Stage stage = (Stage) resultLabel.getScene().getWindow();
                stage.close();
            }

            // For normal transfers, leave the popup open briefly so the user can see the success message
            else {
                Timeline delay = new Timeline(
                        new KeyFrame(Duration.seconds(3), e -> {
                            resultLabel.setVisible(false);
                        })
                );
                delay.play();
            }

        }
        catch (NumberFormatException e) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Please enter a valid number.");
        }
    }

    /**
     * Converts a dropdown label back into the matching Account object.
     * The labels used in the ComboBoxes are not enough on their own to perform
     * the transfer, so this method searches the user's account list and returns
     * the account that matches the label exactly.
     */
    private Account findAccountByLabel(String label) {
        for (Account account : currentUser.getAccounts()) {
            String accountLabel = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
            if (accountLabel.equals(label)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Turns on delete mode for the popup.
     * This is used by SavingsDetailController so that once the required transfer
     * is complete, the emptied savings account can be deleted automatically.
     */
    public void setDeleteMode(Account account) {
        this.deleteMode = true;
        this.accountToDelete = account;
    }

    /**
     * Prefills the From and To account selections and locks them.
     * This is mainly used in the savings account deletion flow so the user is
     * guided directly into the required transfer path.
     */
    public void prefillTransfer(String fromLabel, String toLabel) {
        fromAccountSelector.setValue(fromLabel);
        toAccountSelector.setValue(toLabel);
        fromAccountSelector.setDisable(true);
        toAccountSelector.setDisable(true);
    }

    /**
     * Prefills the transfer amount and makes the field read only.
     * This is also mainly used during account deletion so the transfer amount
     * exactly matches the remaining balance that must be moved out.
     */
    public void prefillAmount(double amount) {
        transferAmountField.setText(String.format("%.2f", amount));
        transferAmountField.setEditable(false);
    }

    // Closes the popup without making any changes
    @FXML
    private void handleBack() {
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows a short help popup explaining how account transfers work.
     * The help text also summarizes the savings transfer fee rules, since that
     * is the main extra rule the user needs to understand on this screen.
     */
    @FXML
    private void handleHelp() {
        MainApp.showStyledPopup(
                "generalerror.drawio.png", "#367A46",
                "Help", "Transfer - Account Help",
                "Transfer money between your Debit and Savings accounts.\n\n" +
                        "• Select which account to transfer FROM and TO\n" +
                        "• Enter the amount and press Transfer\n" +
                        "• Transferring FROM Savings has fees:\n" +
                        "  - First 3 transfers: Free\n" +
                        "  - Transfers 4-9: $3 fee each\n" +
                        "  - After 9: $6 fee each\n" +
                        "• Credit accounts cannot be used for transfers",
                new ButtonType("Okay")
        );
    }
}