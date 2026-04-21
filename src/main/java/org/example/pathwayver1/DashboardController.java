/*
 * DashboardController handles the main hub screen after login.
 *
 * This page gives the user a quick overview of:
 * - their welcome message
 * - debit balance
 * - wallet balance
 * - latest activity
 * - unread notifications
 * - unlocked extra accounts
 * - a preview of the next scenario
 *
 * It also handles all the main navigation buttons and icons on the dashboard.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DashboardController {

    // Main summary labels on the screen
    @FXML private Label welcomeLabel;
    @FXML private Label debitBalanceLabel;
    @FXML private Label walletBalanceLabel;
    @FXML private Label activityOverviewLabel;
    @FXML private Label scenarioOverviewLabel;
    @FXML private Label notificationCountLabel;
    @FXML private Label accountSlot1BalanceLabel;
    @FXML private Label accountSlot2BalanceLabel;

    // These icons swap depending on which extra account types the user has unlocked
    @FXML private ImageView inboxAlert;
    @FXML private ImageView slot1SavingsIcon;
    @FXML private ImageView slot1CreditIcon;
    @FXML private ImageView slot2SavingsIcon;
    @FXML private ImageView slot2CreditIcon;

    private UserAccount currentUser; // Current logged in user

    @FXML
    private void initialize() {
        // Keep the dashboard blank until real user data is loaded in
        debitBalanceLabel.setText("");
        walletBalanceLabel.setText("");
        activityOverviewLabel.setText("");
        scenarioOverviewLabel.setText("");
        notificationCountLabel.setText("");
        accountSlot1BalanceLabel.setText("");
        accountSlot2BalanceLabel.setText("");

        // These stay hidden unless the user actually has extra accounts
        accountSlot1BalanceLabel.setVisible(false);
        accountSlot2BalanceLabel.setVisible(false);

    }

    /*
     * Loads all the user's dashboard data.
     * This fills in the overview labels and decides which extra account icons
     * need to be shown.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        welcomeLabel.setText("WELCOME, " + user.getFirstName().toUpperCase());
        debitBalanceLabel.setText("$" + String.format("%.2f", user.getAccounts().get(0).getBalance()));
        walletBalanceLabel.setText("$" + String.format("%.2f", user.getWalletManager().getBalance()));

        // Show the most recent transaction if the user has any history
        ArrayList<TransactionRecord> history = user.getBankingManager().getTransactionHistory();
        if (!history.isEmpty()) {
            TransactionRecord latest = history.get(history.size() - 1);
            activityOverviewLabel.setText(latest.getTransactionType() + " $"
                    + String.format("%.2f", latest.getAmount()) + " - "
                    + latest.getDate());
        }
        else {
            activityOverviewLabel.setText("No recent activity");
        }

        // Show unread notification count only when needed
        int unreadCount = user.getNotificationManager().getUnreadCount();
        if (unreadCount > 0) {
            notificationCountLabel.setText(String.valueOf(unreadCount));
            inboxAlert.setVisible(true);
        }
        else {
            notificationCountLabel.setText("");
            inboxAlert.setVisible(false);
        }

        // User always has the default Debit account, so extra accounts start after index 0
        int extraAccounts = user.getAccounts().size() - 1;

        // Reset all optional account visuals before filling them in
        slot1CreditIcon.setVisible(false);
        slot1SavingsIcon.setVisible(false);
        slot2CreditIcon.setVisible(false);
        slot2SavingsIcon.setVisible(false);
        accountSlot1BalanceLabel.setVisible(false);
        accountSlot2BalanceLabel.setVisible(false);

        // Fill in slot 1 if the user has at least one extra account
        if (extraAccounts >= 1) {
            Account acc1 = user.getAccounts().get(1);
            String type1 = acc1.getAccountType();

            if (type1.equalsIgnoreCase("Credit")) {
                slot1CreditIcon.setVisible(true);
                accountSlot1BalanceLabel.setText("$" + String.format("%.2f", acc1.getBalanceOwed()));
            }
            else if (type1.equalsIgnoreCase("Savings")) {
                slot1SavingsIcon.setVisible(true);
                accountSlot1BalanceLabel.setText("$" + String.format("%.2f", acc1.getBalance()));
            }
            accountSlot1BalanceLabel.setVisible(true);
        }

        // Fill in slot 2 if the user has a second extra account
        if (extraAccounts >= 2) {
            Account acc2 = user.getAccounts().get(2);
            String type2 = acc2.getAccountType();

            if (type2.equalsIgnoreCase("Credit")) {
                slot2CreditIcon.setVisible(true);
                accountSlot2BalanceLabel.setText("$" + String.format("%.2f", acc2.getBalanceOwed()));
            }
            else if (type2.equalsIgnoreCase("Savings")) {
                slot2SavingsIcon.setVisible(true);
                accountSlot2BalanceLabel.setText("$" + String.format("%.2f", acc2.getBalance()));
            }
            accountSlot2BalanceLabel.setVisible(true);
        }

        // Show a quick preview of the first available scenario
        ArrayList<Scenario> availableScenarios = user.getScenarioManager().getAvailableScenarios("All");
        if (!availableScenarios.isEmpty()) {
            Scenario latest = availableScenarios.get(0);
            scenarioOverviewLabel.setText("\"" + latest.getDescription().substring(0, Math.min(80, latest.getDescription().length())) + "...\"");
        }
        else {
            scenarioOverviewLabel.setText("All scenarios completed!");
        }
    }

    // === Bottom Tab Handlers ===

    @FXML
    private void handleDashboard() {
        // Already on dashboard — do nothing
    }

    @FXML
    private void handleAccountBalance() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("AccountBalanceView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
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
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DepositWithdrawController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActivityTracker() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ActivityTrackerView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ActivityTrackerController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Icon Handlers ===

    // Opens the scenario browser.
    @FXML
    private void handleScenario() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ScenarioView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ScenarioController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the help page and tags it as dashboard help.
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the inbox/notification screen.
    @FXML
    private void handleInbox() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("InboxView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            InboxController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the settings page.
    @FXML
    private void handleSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("SettingsView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            SettingsController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Logs the user out and sends them back to the login screen.
     * Also saves current progress and resets the background checkers.
     */
    @FXML
    private void handleLogout() {
        if (MainApp.showLogoutConfirmation()) {
            MainApp.saveActiveUser();
            MainApp.stopFeeChecker();
            MainApp.startMandatoryChecker();
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("LoginView.fxml")
                );
                Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Opens the assets screen.
    @FXML
    private void handleAssets() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("AssetsView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            AssetsController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     // Opens the wallet screen.
     // Also tells that screen it was opened from the dashboard.
    @FXML
    private void handleWallet() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("WalletView.fxml")
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            WalletController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}