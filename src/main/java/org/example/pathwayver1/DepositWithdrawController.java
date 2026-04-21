/*
 * This controller handles the Deposit / Withdraw screen.
 *
 * This page:
 * - deposits from wallet into an account
 * - withdrawals from debit back into wallet
 * - opens transfer windows
 * - opens payment windows for credit, subscriptions, pending debt, and transfer requests
 *
 * This class is kind of the main "money movement" page in the app.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Optional;

import org.example.pathwayver1.TransferRequest;

import org.example.pathwayver1.Asset;

public class DepositWithdrawController {

    @FXML private Label walletBalanceLabel;
    @FXML private Label paymentBalanceOwedLabel;

    @FXML private TextField depositAmountField;
    @FXML private TextField withdrawAmountField;

    @FXML private ComboBox<String> depositAccountSelector;
    @FXML private ComboBox<String> withdrawAccountSelector;
    @FXML private ComboBox<String> paymentEntitySelector;

    private UserAccount currentUser; // Logged-in user
    private BankingManager bankingManager;  // Shortcut to the user's banking manager
    private boolean transferWindowOpen = false; // Used so the transfer request popup does not get reopened weirdly

    @FXML
    private void initialize() {
        // Start blank until the screen gets real user data
        walletBalanceLabel.setText("");
        paymentBalanceOwedLabel.setText("");
    }

    /*
     * Loads everything this page needs for the current user.
     *
     * This fills the wallet balance, sets up the account dropdowns,
     * and decides what amount to show when the payment dropdown changes.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.bankingManager = user.getBankingManager();
        walletBalanceLabel.setText("$" + String.format("%.2f", user.getWalletManager().getBalance()));

        // Build all dropdown choices fresh each time this screen loads
        populateAccountSelectors();

        // If the user already has pending debt, show it right away
        if (currentUser.getPendingDebt() > 0) {
            paymentBalanceOwedLabel.setText(String.format("%.2f", currentUser.getPendingDebt()));
        }

        // Update the amount label whenever the user picks a payment target
        paymentEntitySelector.setOnAction(e -> {
            String selected = paymentEntitySelector.getValue();
            if (selected != null) {

                // Pending debt shows the current debt amount
                if (selected.equals("PENDING DEBT")) {
                    paymentBalanceOwedLabel.setText(String.format("%.2f", currentUser.getPendingDebt()));
                    return;
                }

                // Transfer requests store the amount right inside the label text
                if (selected.startsWith("TRANSFER: ")) {
                    if (transferWindowOpen) return;

                    String[] parts = selected.split(" - \\$");
                    if (parts.length >= 2) {
                        paymentBalanceOwedLabel.setText(parts[1]);
                    }
                    return;
                }

                // Subscription labels start with SUB:
                if (selected.startsWith("SUB: ")) {
                    String assetName = selected.substring(5);

                    for (Asset asset : currentUser.getAssetManager().getOwnedAssets()) {
                        if (asset.getName().equals(assetName)) {
                            paymentBalanceOwedLabel.setText(String.format("%.2f", asset.getRecurringCost()));
                            return;
                        }
                    }
                }

                for (Account account : currentUser.getAccounts()) {
                    String label = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
                    if (label.equals(selected) && account.getAccountType().equalsIgnoreCase("Credit")) {
                        paymentBalanceOwedLabel.setText(String.format("%.2f", account.getBalanceOwed()));
                        return;
                    }
                }
            }
            paymentBalanceOwedLabel.setText("");
        });
    }

    /*
     * Rebuilds all 3 dropdowns on this page.
     *
     * Deposit and withdraw choices depend on the account type.
     * Payment choices are broader because they can include credit cards,
     * subscriptions, pending debt, and pending transfer requests.
     */
    private void populateAccountSelectors() {
        depositAccountSelector.getItems().clear();
        withdrawAccountSelector.getItems().clear();
        paymentEntitySelector.getItems().clear();

        for (Account account : currentUser.getAccounts()) {
            String label = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();

            // Deposits can go into Debit or Savings
            if (account.getAccountType().equalsIgnoreCase("Debit") ||
                    account.getAccountType().equalsIgnoreCase("Savings")) {
                depositAccountSelector.getItems().add(label);
            }

            // Withdrawals are only allowed from Debit on this screen
            if (account.getAccountType().equalsIgnoreCase("Debit")) {
                withdrawAccountSelector.getItems().add(label);
            }

            // Credit accounts can appear in the payment dropdown
            if (account.getAccountType().equalsIgnoreCase("Credit")) {
                paymentEntitySelector.getItems().add(label);
            }
        }

        // Add subscriptions to the payment dropdown
        for (Asset asset : currentUser.getAssetManager().getOwnedAssets()) {
            if (asset.getItemType().equals("Subscription")) {
                paymentEntitySelector.getItems().add("SUB: " + asset.getName());
            }
        }

        // Add pending debt if exists
        if (currentUser.getPendingDebt() > 0) {
            paymentEntitySelector.getItems().add("PENDING DEBT");
        }

        // Add any unpaid transfer requests created by scenarios
        for (TransferRequest request : currentUser.getPendingTransferRequests()) {
            if (!request.isCompleted()) {
                paymentEntitySelector.getItems().add("TRANSFER: " + request.getContactName() + " - $" + String.format("%.2f", request.getAmount()));
            }
        }
    }


    // === Deposit ===
    /*
     * Handles deposits from the wallet into an account.
     * This method mainly does validation, shows confirmation popups,
     * then passes the actual banking action off to BankingManager.
     */
    @FXML
    private void handleDeposit() {
        if (depositAccountSelector.getValue() == null) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Deposit", "No Account Selected",
                    "Please select an account to deposit to.",
                    new ButtonType("Okay")
            );
            return;
        }

        String amountText = depositAmountField.getText().trim();
        if (amountText.isEmpty()) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Deposit", "No Amount Entered",
                    "Please enter an amount to deposit.",
                    new ButtonType("Okay")
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Deposit", "Invalid Amount",
                        "Please enter a valid amount greater than zero.",
                        new ButtonType("Okay")
                );
                return;
            }

            String selectedLabel = depositAccountSelector.getValue();
            Account selectedAccount = findAccountByLabel(selectedLabel);

            if (selectedAccount == null) {
                return;
            }

            if (currentUser.getWalletManager().getBalance() < amount) {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Deposit", "Insufficient Funds",
                        "Your wallet does not have enough funds for this deposit.",
                        new ButtonType("Okay")
                );
                return;
            }

            Optional<ButtonType> result = MainApp.showStyledPopup(
                    "depositicon.drawio.png", "#EBF7A6",
                    "Deposit", "Confirm Deposit",
                    "Do you wish to deposit $" + String.format("%.2f", amount) + " to " + selectedLabel + "?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                bankingManager.processDeposit(selectedAccount, amount, currentUser.getWalletManager());

                MainApp.showStyledPopup(
                        "depositicon.drawio.png", "#EBF7A6",
                        "Deposit", "Deposit Successful",
                        "Deposit to " + selectedLabel + " successful!",
                        new ButtonType("Okay")
                );

                // Refresh visible balance after the deposit
                walletBalanceLabel.setText("$" + String.format("%.2f", currentUser.getWalletManager().getBalance()));
                depositAmountField.clear();
            }

        }
        catch (NumberFormatException e) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Deposit", "Invalid Entry",
                    "Please enter a valid number.",
                    new ButtonType("Okay")
            );
        }
    }

    // === Withdraw ===
    // Handles withdrawals from the selected debit account back into the wallet.
    @FXML
    private void handleWithdraw() {
        if (withdrawAccountSelector.getValue() == null) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Withdraw", "No Account Selected",
                    "Please select an account to withdraw from.",
                    new ButtonType("Okay")
            );
            return;
        }

        String amountText = withdrawAmountField.getText().trim();
        if (amountText.isEmpty()) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Withdraw", "No Amount Entered",
                    "Please enter an amount to withdraw.",
                    new ButtonType("Okay")
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Withdraw", "Invalid Amount",
                        "Please enter a valid amount greater than zero.",
                        new ButtonType("Okay")
                );
                return;
            }

            String selectedLabel = withdrawAccountSelector.getValue();
            Account selectedAccount = findAccountByLabel(selectedLabel);

            if (selectedAccount == null) {
                return;
            }

            if (selectedAccount.getBalance() < amount) {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Withdraw", "Insufficient Funds",
                        "This account does not have enough funds for this withdrawal.",
                        new ButtonType("Okay")
                );
                return;
            }

            Optional<ButtonType> result = MainApp.showStyledPopup(
                    "withdrawicon.drawio.png", "#EBF7A6",
                    "Withdraw", "Confirm Withdrawal",
                    "Do you wish to withdraw $" + String.format("%.2f", amount) + " from " + selectedLabel + "?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                bankingManager.processWithdrawal(selectedAccount, amount, currentUser.getWalletManager());

                MainApp.showStyledPopup(
                        "withdrawicon.drawio.png", "#EBF7A6",
                        "Withdraw", "Withdrawal Successful",
                        "Withdrawal from " + selectedLabel + " successful!",
                        new ButtonType("Okay")
                );

                walletBalanceLabel.setText("$" + String.format("%.2f", currentUser.getWalletManager().getBalance()));
                withdrawAmountField.clear();
            }

        }

        catch (NumberFormatException e) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Withdraw", "Invalid Entry",
                    "Please enter a valid number.",
                    new ButtonType("Okay")
            );
        }
    }

    // === Transfer ===
    // Opens the account-to-account transfer popups
    @FXML
    private void handleTransferAccount() {
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

            transferWindowOpen = true;
            transferStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the contact list popup for sending money to a contact.
    @FXML
    private void handleTransferContact() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("TransferContactListView.fxml")
            );
            Stage contactStage = new Stage();
            contactStage.setTitle("");
            contactStage.setResizable(false);
            contactStage.initStyle(javafx.stage.StageStyle.UTILITY);
            contactStage.setScene(new Scene(loader.load()));

            TransferContactListController controller = loader.getController();
            controller.loadUserData(currentUser);

            contactStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Payment ===
    /*
     * Opens the payment popup for whatever payment target is selected.
     *
     * This could be:
     * - pending debt
     * - a transfer request
     * - a subscription
     * - a credit account
     */
    @FXML
    private void handlePay() {
        if (paymentEntitySelector.getValue() == null) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Payment", "No Entity Selected",
                    "Please select an entity to pay first.",
                    new ButtonType("Okay")
            );
            return;
        }

        try {
            String selected = paymentEntitySelector.getValue();

            // Pending debt opens the payment window preloaded with current debt
            if (selected.equals("PENDING DEBT")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("PaymentView.fxml")
                );
                Stage paymentStage = new Stage();
                paymentStage.setTitle("");
                paymentStage.setResizable(false);
                paymentStage.initStyle(javafx.stage.StageStyle.UTILITY);
                paymentStage.setScene(new Scene(loader.load()));

                PaymentController controller = loader.getController();
                controller.loadUserData(currentUser);
                controller.loadPendingDebtPayment(currentUser.getPendingDebt());

                paymentStage.show();
                return;
            }

            // Transfer requests use the contact transfer confirm popup instead
            if (selected.startsWith("TRANSFER: ")) {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("TransferContactConfirmView.fxml")
                    );
                    Stage transferStage = new Stage();
                    transferStage.setTitle("");
                    transferStage.setResizable(false);
                    transferStage.initStyle(javafx.stage.StageStyle.UTILITY);
                    transferStage.setScene(new Scene(loader.load()));

                    // Find the matching request objects
                    TransferRequest matchedRequest = null;
                    for (TransferRequest request : currentUser.getPendingTransferRequests()) {
                        if (!request.isCompleted() && selected.contains(request.getContactName())) {
                            matchedRequest = request;
                            break;
                        }
                    }

                    if (matchedRequest != null) {

                        if (matchedRequest.isCompleted()) {
                            MainApp.showStyledPopup(
                                    "generalerror.drawio.png", "#D66242",
                                    "Transfer", "Already Completed",
                                    "This transfer has already been completed.",
                                    new ButtonType("Okay")
                            );
                            populateAccountSelectors();
                            paymentBalanceOwedLabel.setText("");
                            return;
                        }

                        TransferContactConfirmController controller = loader.getController();
                        controller.loadUserData(currentUser);
                        controller.setSelectedContact(matchedRequest.getContactName());
                        controller.prefillTransferRequest(matchedRequest);

                        transferStage.setOnHidden(e -> {
                            transferWindowOpen = false;
                            populateAccountSelectors();
                            paymentBalanceOwedLabel.setText("");
                        });

                        transferStage.show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            // Regular payment popup for subscriptions or credit accounts
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("PaymentView.fxml")
            );
            Stage paymentStage = new Stage();
            paymentStage.setTitle("");
            paymentStage.setResizable(false);
            paymentStage.initStyle(javafx.stage.StageStyle.UTILITY);
            paymentStage.setScene(new Scene(loader.load()));

            PaymentController controller = loader.getController();
            controller.loadUserData(currentUser);

            if (selected.startsWith("SUB: ")) {
                String assetName = selected.substring(5);
                for (Asset asset : currentUser.getAssetManager().getOwnedAssets()) {
                    if (asset.getName().equals(assetName)) {
                        controller.loadSubscriptionPayment(
                                asset.getName(),
                                asset.getRecurringCost(),
                                asset.getNextPaymentDue(),
                                asset.getBillingCycle(),
                                asset.getPaymentStatus()
                        );
                        break;
                    }
                }
            }

            else {
                for (Account account : currentUser.getAccounts()) {
                    String label = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
                    if (label.equals(selected) && account.getAccountType().equalsIgnoreCase("Credit")) {
                        controller.loadCreditPayment(account);
                        break;
                    }
                }
            }

            paymentStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Wallet ===
    @FXML
    private void handleWallet() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("WalletView.fxml")
            );
            Stage stage = (Stage) walletBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            WalletController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("depositWithdraw");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Help ===
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) walletBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("depositWithdraw");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Bottom Tab Navigation ===
    @FXML
    private void handleDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) walletBalanceLabel.getScene().getWindow();
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
            Stage stage = (Stage) walletBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            AccountBalanceController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDepositWithdraw() {
        // Already here
    }

    @FXML
    private void handleActivityTracker() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ActivityTrackerView.fxml")
            );
            Stage stage = (Stage) walletBalanceLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ActivityTrackerController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === Helper Method ===
    private Account findAccountByLabel(String label) {
        for (Account account : currentUser.getAccounts()) {
            String accountLabel = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
            if (accountLabel.equals(label)) {
                return account;
            }
        }
        return null;
    }
}