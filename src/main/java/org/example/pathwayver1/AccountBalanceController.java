/**
 * AccountBalanceController manages the Account Balance screen.
 *
 * This screen shows the user's default Debit account plus up to two additional
 * account slots that can either:
 * 1. remain empty and offer an "add account" option, or
 * 2. display an unlocked Credit or Savings account.
 *
 * The controller is responsible for:
 * - loading the user's current account data into the UI
 * - showing the correct account card for each slot
 * - handling account unlock actions
 * - navigating to detailed account views
 * - handling bottom-tab and help navigation
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class AccountBalanceController {

    // Debit labels
    @FXML private Label debitBalanceLabel;
    @FXML private Label debitAccountNumLabel;
    @FXML private Label debitActivityLabel;

    // Slot 2 - Add Account
    @FXML private ImageView slot2AddCard;
    @FXML private ImageView slot2AddExpanded;
    @FXML private Button slot2AddButton;
    @FXML private Button slot2AddToggleClose;
    @FXML private Button slot2AddCredit;
    @FXML private Button slot2AddSavings;

    // Slot 2 - Credit
    @FXML private ImageView slot2CreditCard;
    @FXML private Button slot2CreditViewDetails;
    @FXML private Label slot2CreditBalanceLabel;
    @FXML private Label slot2CreditAccountNumLabel;
    @FXML private Label slot2CreditActivityLabel;
    @FXML private Label slot2CreditPayDueLabel;

    // Slot 2 - Savings
    @FXML private ImageView slot2SavingsCard;
    @FXML private Button slot2SavingsViewDetails;
    @FXML private Label slot2SavingsBalanceLabel;
    @FXML private Label slot2SavingsAccountNumLabel;
    @FXML private Label slot2SavingsActivityLabel;

    // Slot 3 - Add Account
    @FXML private ImageView slot3AddCard;
    @FXML private ImageView slot3AddExpanded;
    @FXML private Button slot3AddButton;
    @FXML private Button slot3AddToggleClose;
    @FXML private Button slot3AddCredit;
    @FXML private Button slot3AddSavings;

    // Slot 3 - Credit
    @FXML private ImageView slot3CreditCard;
    @FXML private Button slot3CreditViewDetails;
    @FXML private Label slot3CreditBalanceLabel;
    @FXML private Label slot3CreditAccountNumLabel;
    @FXML private Label slot3CreditActivityLabel;
    @FXML private Label slot3CreditPayDueLabel;

    // Slot 3 - Savings
    @FXML private ImageView slot3SavingsCard;
    @FXML private Button slot3SavingsViewDetails;
    @FXML private Label slot3SavingsBalanceLabel;
    @FXML private Label slot3SavingsAccountNumLabel;
    @FXML private Label slot3SavingsActivityLabel;

    // Button for opening the Debit detail screen
    @FXML private Button slot1ViewDetails;

    // Help button for the page
    @FXML private Button helpButton;

    // Currently logged-in user and their banking manager
    private UserAccount currentUser;
    private BankingManager bankingManager;

    /**
     * Runs automatically when the FXML is loaded.
     *
     * This clears dynamic labels so the UI does not show stale or null values
     * before real user data is loaded in.
     */
    @FXML
    private void initialize() {
        debitBalanceLabel.setText("");
        debitAccountNumLabel.setText("");
        debitActivityLabel.setText("");
    }

    /**
     * Looks through the user's transaction history and finds the most recent
     * activity involving the account that matches the provided last four digits.
     *
     * This is used to populate the small "recent activity" summary labels shown
     * on the account cards.
     */
    private String getLatestActivity(String lastFour) {
        ArrayList<TransactionRecord> records = currentUser.getBankingManager().getTransactionHistory();

        // Search backward so the first match we find is the newest one
        for (int i = records.size() - 1; i >= 0; i--) {
            TransactionRecord record = records.get(i);

            if (record.getAssociatedAccount().contains(lastFour)) {
                return record.getTransactionType() + " $" + String.format("%.2f", record.getAmount()) + " - " + record.getDate();
            }
        }
        return "No recent activity";
    }

    /**
     * Loads all account-balance screen data for the current user.
     *
     * This method:
     * - stores the logged-in user reference
     * - fills in the default Debit account card
     * - resets slots 2 and 3 back to their default hidden/empty states
     * - shows any additional unlocked Credit or Savings accounts
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.bankingManager = user.getBankingManager();

        // Debit account is always the first/default account
        debitBalanceLabel.setText(String.format("%.2f", user.getAccounts().get(0).getBalance()));
        debitAccountNumLabel.setText("ACC#: " + user.getAccounts().get(0).getAccountNumber());

        String debitLastFour = user.getAccounts().get(0).getLastFour();
        String debitActivity = getLatestActivity(debitLastFour);
        debitActivityLabel.setText(debitActivity);

        // Count any accounts beyond the default Debit account
        int extraAccounts = user.getAccounts().size() - 1;

        // Reset slot 2 and slot 3 to the "add account" state before deciding what to show
        slot2AddCard.setVisible(true);
        slot2AddButton.setVisible(true);
        slot3AddCard.setVisible(true);
        slot3AddButton.setVisible(true);

        // Hide every slot 2 account-specific element
        slot2CreditCard.setVisible(false);
        slot2CreditViewDetails.setVisible(false);
        slot2CreditBalanceLabel.setVisible(false);
        slot2CreditAccountNumLabel.setVisible(false);
        slot2CreditActivityLabel.setVisible(false);
        slot2CreditPayDueLabel.setVisible(false);


        slot2SavingsCard.setVisible(false);
        slot2SavingsViewDetails.setVisible(false);
        slot2SavingsBalanceLabel.setVisible(false);
        slot2SavingsAccountNumLabel.setVisible(false);
        slot2SavingsActivityLabel.setVisible(false);

        // Hide every slot 3 account-specific element
        slot3CreditCard.setVisible(false);
        slot3CreditViewDetails.setVisible(false);
        slot3CreditBalanceLabel.setVisible(false);
        slot3CreditAccountNumLabel.setVisible(false);
        slot3CreditActivityLabel.setVisible(false);
        slot3CreditPayDueLabel.setVisible(false);

        slot3SavingsCard.setVisible(false);
        slot3SavingsViewDetails.setVisible(false);
        slot3SavingsBalanceLabel.setVisible(false);
        slot3SavingsAccountNumLabel.setVisible(false);
        slot3SavingsActivityLabel.setVisible(false);

        // If the user has one additional account, show it in slot 2
        if (extraAccounts >= 1) {
            Account acc1 = user.getAccounts().get(1);
            showSlot(2, acc1);
        }

        // If the user has a second additional account, show it in slot 3
        if (extraAccounts >= 2) {
            Account acc2 = user.getAccounts().get(2);
            showSlot(3, acc2);
        }
    }

    /**
     * Displays the correct UI card for a specific slot based on the account type.
     *
     * Slot 2 and slot 3 can each show either:
     * - a Credit account card
     * - a Savings account card
     *
     * This method hides the "add account" card for the chosen slot and then
     * fills in the matching account details.
     */
    private void showSlot(int slot, Account account) {
        String type = account.getAccountType();

        if (slot == 2) {
            // Hide the add-account state because this slot is now occupied
            slot2AddCard.setVisible(false);
            slot2AddButton.setVisible(false);

            if (type.equalsIgnoreCase("Credit")) {
                slot2CreditCard.setVisible(true);
                slot2CreditViewDetails.setVisible(true);
                slot2CreditBalanceLabel.setVisible(true);
                slot2CreditBalanceLabel.setText(String.format("%.2f", account.getBalanceOwed()));
                slot2CreditAccountNumLabel.setVisible(true);
                slot2CreditAccountNumLabel.setText("ACC#: " + account.getAccountNumber());
                slot2CreditActivityLabel.setVisible(true);
                slot2CreditActivityLabel.setText(getLatestActivity(account.getLastFour()));
                slot2CreditPayDueLabel.setVisible(true);
                slot2CreditPayDueLabel.setText(account.getPaymentDueDate());
            }

            else if (type.equalsIgnoreCase("Savings")) {
                slot2SavingsCard.setVisible(true);
                slot2SavingsViewDetails.setVisible(true);
                slot2SavingsBalanceLabel.setVisible(true);
                slot2SavingsBalanceLabel.setText(String.format("%.2f", account.getBalance()));
                slot2SavingsAccountNumLabel.setVisible(true);
                slot2SavingsAccountNumLabel.setText("ACC#: " + account.getAccountNumber());
                slot2SavingsActivityLabel.setVisible(true);
                slot2SavingsActivityLabel.setText(getLatestActivity(account.getLastFour()));
            }
        }

        else if (slot == 3) {
            // Hide the add-account state because this slot is now occupied
            slot3AddCard.setVisible(false);
            slot3AddButton.setVisible(false);

            if (type.equalsIgnoreCase("Credit")) {
                slot3CreditCard.setVisible(true);
                slot3CreditViewDetails.setVisible(true);
                slot3CreditBalanceLabel.setVisible(true);
                slot3CreditBalanceLabel.setText(String.format("%.2f", account.getBalanceOwed()));
                slot3CreditAccountNumLabel.setVisible(true);
                slot3CreditAccountNumLabel.setText("ACC#: " + account.getAccountNumber());
                slot3CreditActivityLabel.setVisible(true);
                slot3CreditActivityLabel.setText(getLatestActivity(account.getLastFour()));
                slot3CreditPayDueLabel.setVisible(true);
                slot3CreditPayDueLabel.setText(account.getPaymentDueDate());
            }

            else if (type.equalsIgnoreCase("Savings")) {
                slot3SavingsCard.setVisible(true);
                slot3SavingsViewDetails.setVisible(true);
                slot3SavingsBalanceLabel.setVisible(true);
                slot3SavingsBalanceLabel.setText(String.format("%.2f", account.getBalance()));
                slot3SavingsAccountNumLabel.setVisible(true);
                slot3SavingsAccountNumLabel.setText("ACC#: " + account.getAccountNumber());
                slot3SavingsActivityLabel.setVisible(true);
                slot3SavingsActivityLabel.setText(getLatestActivity(account.getLastFour()));
            }
        }
    }

    // === Slot 2 Add Toggle ===

    /**
     * Expands or collapses the slot 2 add-account menu.
     *
     * This lets the user choose between unlocking a Credit or Savings account.
     */
    @FXML
    private void handleSlot2Add() {
        boolean expanding = slot2AddCard.isVisible();

        slot2AddCard.setVisible(!expanding);
        slot2AddButton.setVisible(!expanding);

        slot2AddExpanded.setVisible(expanding);
        slot2AddToggleClose.setVisible(expanding);
        slot2AddCredit.setVisible(expanding);
        slot2AddSavings.setVisible(expanding);
    }

    // === Slot 3 Add Toggle ===

    /**
     * Expands or collapses the slot 3 add-account menu.
     */
    @FXML
    private void handleSlot3Add() {
        boolean expanding = slot3AddCard.isVisible();

        slot3AddCard.setVisible(!expanding);
        slot3AddButton.setVisible(!expanding);

        slot3AddExpanded.setVisible(expanding);
        slot3AddToggleClose.setVisible(expanding);
        slot3AddCredit.setVisible(expanding);
        slot3AddSavings.setVisible(expanding);
    }

    // === Slot 2 Add Credit ===
    // Attempts to unlock a Credit account in slot 2
    @FXML
    private void handleSlot2AddCredit() {
        unlockAccount("Credit", 150.0, 2);
    }

    // === Slot 2 Add Savings ===
    // Attempts to unlock a Savings account in slot 2.
    @FXML
    private void handleSlot2AddSavings() {
        unlockAccount("Savings", 100.0, 2);
    }

    // === Slot 3 Add Credit ===
    // Attempts to unlock a Credit account in slot 3.
    @FXML
    private void handleSlot3AddCredit() {
        unlockAccount("Credit", 150.0, 3);
    }

    // === Slot 3 Add Savings ===
    //  Attempts to unlock a Savings account in slot 3.
    @FXML
    private void handleSlot3AddSavings() {
        unlockAccount("Savings", 100.0, 3);
    }

    // === Unlock Account ===

    /**
     * Handles the full account unlock flow.
     *
     * This method:
     * - shows a confirmation popup with the required downpayment
     * - asks the BankingManager to unlock the account
     * - shows success or failure feedback
     * - reloads the Account Balance screen so the new account appears
     */
    private void unlockAccount(String type, double cost, int slot) {
        String iconFile = type.equals("Credit") ? "addcredit.drawio.png" : "addsavings.drawio.png";

        Optional<ButtonType> result = MainApp.showStyledPopup(
                iconFile, "#EDAC45",
                "Unlock Account", type + " Account",
                "Make a downpayment of $" + String.format("%.2f", cost) + " to unlock " + type + " account.",
                new ButtonType("Pay"), new ButtonType("Not Now")
        );

        if (result.isPresent() && result.get().getText().equals("Pay")) {
            if (bankingManager.unlockAccount(currentUser, type, cost)) {

                MainApp.showStyledPopup(
                        iconFile, "#EDAC45",
                        "Unlock Account", "Congratulations!",
                        "Congratulations you unlocked " + type + " Account!",
                        new ButtonType("Okay")
                );

                // Reload this screen so all slot data refreshes immediately
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("AccountBalanceView.fxml")
                    );
                    Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));

                    AccountBalanceController controller = loader.getController();
                    controller.loadUserData(currentUser);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            else {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Unlock Account", "Insufficient Funds",
                        "You don't have enough funds to access this account. Try DEPOSITING money into your Debit Account first!",
                        new ButtonType("Okay")
                );
            }
        }
    }

    // === View Details ===
    // Opens the Debit account detail screen.
    @FXML
    private void handleSlot1ViewDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DebitDetailView.fxml")
            );
            Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DebitDetailController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the appropriate detail page for a non-debit account.
     *
     * Credit accounts go to CreditDetailView.
     * Savings accounts go to SavingsDetailView.
     */
    private void navigateToDetail(Account account) {
        try {
            String type = account.getAccountType();

            if (type.equalsIgnoreCase("Credit")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("CreditDetailView.fxml")
                );
                Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                CreditDetailController controller = loader.getController();
                controller.loadUserData(currentUser, account);

            } else if (type.equalsIgnoreCase("Savings")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("SavingsDetailView.fxml")
                );
                Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                SavingsDetailController controller = loader.getController();
                controller.loadUserData(currentUser, account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the detail view for the account currently shown in slot 2.
    @FXML
    private void handleSlot2ViewDetails() {
        if (currentUser.getAccounts().size() > 1) {
            Account acc = currentUser.getAccounts().get(1);
            navigateToDetail(acc);
        }
    }

    // Opens the detail view for the account currently shown in slot 3.
    @FXML
    private void handleSlot3ViewDetails() {
        if (currentUser.getAccounts().size() > 2) {
            Account acc = currentUser.getAccounts().get(2);
            navigateToDetail(acc);
        }
    }

    // === Bottom Tab Navigation ===

    // Returns the user to the Dashboard screen.
    @FXML
    private void handleDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountBalance() {
        // Already here
    }

    // Opens the Deposit/Withdraw screen.
    @FXML
    private void handleDepositWithdraw() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DepositWithdrawView.fxml")
            );
            Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DepositWithdrawController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the Activity Tracker screen.
    @FXML
    private void handleActivityTracker() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ActivityTrackerView.fxml")
            );
            Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ActivityTrackerController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Help screen and tells it this request came from
     * the Account Balance page.
     */
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) debitBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("accountBalance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}