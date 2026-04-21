/**
 * PaymentController manages the payment popup used in the Deposit/Withdraw area.
 *
 * This controller is designed to handle several payment situations using the
 * same screen. Depending on what opened the popup, the payment target can be:
 * a credit account, a subscription, or pending debt.
 *
 * The controller's main job is to:
 * - load the correct payment details into the popup
 * - limit which account can be used as the payment source
 * - validate the amount entered by the user
 * - apply the payment to the correct object
 * - record the result in transaction history and notifications when needed
 *
 * Because the popup is reused for different payment types, the field
 * entityType is important. It tells handlePay() which logic branch to run.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PaymentController {

    @FXML private Label entityNameLabel;
    @FXML private Label amountDueLabel;
    @FXML private Label paymentDueDateLabel;
    @FXML private Label detailLabel1;
    @FXML private Label detailLabel2;
    @FXML private Label resultLabel;
    @FXML private TextField paymentAmountField;
    @FXML private ComboBox<String> paymentSourceSelector;

    private UserAccount currentUser; // Current logged-in user

    // Tells the controller what kind of payment this popup is handling
    // Expected values here are: credit, subscription, or pendingDebt
    private String entityType;

    /**
     * Initializes the popup.
     * The result label starts hidden so the screen stays clean until the user
     * submits a payment or triggers a validation error.
     */
    @FXML
    private void initialize() {
        resultLabel.setVisible(false);
    }

    /**
     * Stores the current user.
     * This has to be called before payment specific loading methods because the
     * popup needs access to the user's accounts, assets, notifications, and
     * banking history.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
    }

    /**
     * Loads the popup for a credit account payment.
     * For credit payments, the source account is forced to the user's debit
     * account. That matches the project rule that credit cards are paid from
     * Debit rather than from another credit account.
     */
    public void loadCreditPayment(Account creditAccount) {
        this.entityType = "credit";
        entityNameLabel.setText("CREDIT ACC - PB " + creditAccount.getLastFour());
        amountDueLabel.setText("Amount Due: $" + String.format("%.2f", creditAccount.getBalanceOwed()));
        paymentDueDateLabel.setText("Payment Due Date: " + creditAccount.getPaymentDueDate());
        detailLabel1.setText("Minimum Payment: $" + String.format("%.2f", creditAccount.getMinimumPayment()));
        detailLabel2.setText("Status: " + (creditAccount.getBalanceOwed() > 0 ? "Unpaid" : "Paid"));

        paymentSourceSelector.getItems().clear();
        Account debit = currentUser.getAccounts().get(0);
        String debitLabel = "DEBIT ACC - PB " + debit.getLastFour();
        paymentSourceSelector.getItems().add(debitLabel);
        paymentSourceSelector.setValue(debitLabel);
        paymentSourceSelector.setDisable(true);
    }

    /**
     * Loads the popup for a subscription payment.
     * Subscriptions are more flexible than credit payments because the user is
     * allowed to pay from either Debit or Credit. This method fills the source
     * selector with all eligible accounts.
     */
    public void loadSubscriptionPayment(String itemName, double amountDue, String dueDate, String billingCycle, String status) {
        this.entityType = "subscription";
        entityNameLabel.setText(itemName);
        amountDueLabel.setText("Amount Due: $" + String.format("%.2f", amountDue));
        paymentDueDateLabel.setText("Payment Due Date: " + dueDate);
        detailLabel1.setText("Billing Cycle: " + billingCycle);
        detailLabel2.setText("Status: " + status);

        paymentSourceSelector.getItems().clear();
        for (Account account : currentUser.getAccounts()) {
            if (account.getAccountType().equalsIgnoreCase("Debit") ||
                    account.getAccountType().equalsIgnoreCase("Credit")) {
                String label = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
                paymentSourceSelector.getItems().add(label);
            }
        }
        paymentSourceSelector.setValue(paymentSourceSelector.getItems().get(0));
        paymentSourceSelector.setDisable(false);
    }

    /**
     * Loads the popup for pending debt payment.
     * Pending debt is treated similarly to credit payment in that the source is
     * restricted to Debit only.
     */
    public void loadPendingDebtPayment(double debtAmount) {
        this.entityType = "pendingDebt";
        entityNameLabel.setText("Pending Debt");
        amountDueLabel.setText("Amount Due: $" + String.format("%.2f", debtAmount));
        paymentDueDateLabel.setText("Accumulated from scenario outcomes");
        detailLabel1.setText("");
        detailLabel2.setText("Status: " + (debtAmount > 0 ? "Unpaid" : "Paid"));

        // Only debit allowed for pending debt
        paymentSourceSelector.getItems().clear();
        Account debit = currentUser.getAccounts().get(0);
        String debitLabel = "DEBIT ACC - PB " + debit.getLastFour();
        paymentSourceSelector.getItems().add(debitLabel);
        paymentSourceSelector.setValue(debitLabel);
        paymentSourceSelector.setDisable(true);
    }

    /**
     * Handles the Pay button.
     *
     * This method does most of the work in the controller. It:
     * - validates the entered amount
     * - finds the selected payment source account
     * - checks whether the source account can cover the payment
     * - branches into credit, subscription, or pending debt payment logic
     * - records the payment in the user's history
     * - shows the success or error result on screen
     *
     * The exact behavior depends on entityType, which was set earlier by one
     * of the load...Payment() methods.
     */
    @FXML
    private void handlePay() {
        resultLabel.setVisible(false);

        String amountText = paymentAmountField.getText().trim();

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                resultLabel.setText("Please enter a valid amount.");
                return;
            }

            String sourceLabel = paymentSourceSelector.getValue();

            if (sourceLabel == null) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                resultLabel.setText("Please select an account to pay from.");
                return;
            }

            // Match the selected label back to the actual Account object
            Account sourceAccount = null;

            for (Account acc : currentUser.getAccounts()) {
                String label = acc.getAccountType().toUpperCase() + " ACC - PB " + acc.getLastFour();

                if (label.equals(sourceLabel)) {
                    sourceAccount = acc;
                    break;
                }
            }

            if (sourceAccount == null) return;

            // Before checking the payment target, make sure the source account itself can cover the amount
            if (sourceAccount.getAccountType().equalsIgnoreCase("Debit")) {
                if (sourceAccount.getBalance() < amount) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("Insufficient funds in debit account.");
                    return;
                }
            }

            else if (sourceAccount.getAccountType().equalsIgnoreCase("Credit")) {
                if (sourceAccount.getAvailableCredit() < amount) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("Insufficient available credit.");
                    return;
                }
            }

            // ===== CREDIT PAYMENT LOGIC =====
            if (entityType.equals("credit")) {
                Account creditAcc = null;

                // Find the credit account being paid by matching its last four digits in the title label
                for (Account acc : currentUser.getAccounts()) {
                    if (acc.getAccountType().equalsIgnoreCase("Credit") &&
                            entityNameLabel.getText().contains(acc.getLastFour())) {
                        creditAcc = acc;
                        break;
                    }
                }

                if (creditAcc == null) return;

                // Prevent paying more than what is owed
                if (amount > creditAcc.getBalanceOwed()) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("Amount exceeds balance owed.");
                    return;
                }

                sourceAccount.withdraw(amount);
                creditAcc.makePayment(amount);

                // Debit is reduced, then the credit account processes the payment
                currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                        "Payment",
                        "CREDIT ACC - PB " + creditAcc.getLastFour(),
                        amount,
                        java.time.LocalDate.now().toString(),
                        "Positive"
                ));

            }

            // ===== SUBSCRIPTION PAYMENT LOGIC =====
            else if (entityType.equals("subscription"))
            {
                String assetName = entityNameLabel.getText();
                Asset subAsset = null;

                // Find the matching subscription asset by name
                for (Asset asset : currentUser.getAssetManager().getOwnedAssets()) {
                    if (asset.getName().equals(assetName)) {
                        subAsset = asset;
                        break;
                    }
                }

                if (subAsset == null) return;

                // If the asset is already paid, no second payment should be accepted
                if (subAsset.getPaymentStatus().equals("Paid")) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("This subscription is already paid for this cycle.");
                    return;
                }

                // Subscription payments must match the full recurring cost
                if (amount > subAsset.getRecurringCost()) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("Amount exceeds subscription cost.");
                    return;
                }

                if (amount < subAsset.getRecurringCost()) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("You must pay the full amount of $" + String.format("%.2f", subAsset.getRecurringCost()) + ".");
                    return;
                }

                // Paying from Debit removes cash now
                if (sourceAccount.getAccountType().equalsIgnoreCase("Debit")) {
                    sourceAccount.withdraw(amount);
                }

                // Paying from Credit adds a charge instead of subtracting a balance
                else if (sourceAccount.getAccountType().equalsIgnoreCase("Credit")) {
                    sourceAccount.addCharge(amount);
                }

                // Reset subscription status after successful payment
                subAsset.setPaymentStatus("Paid");
                subAsset.setRecurringCost(subAsset.getBaseCost());
                subAsset.setLateFeeApplied(false);

                // Push due date forward to start the next cycle
                java.time.LocalDateTime due = java.time.LocalDateTime.now().plusMinutes(20);
                String formatted = String.format("%s %d, %d:%02d",
                        due.getMonth().toString().substring(0, 3),
                        due.getDayOfMonth(),
                        due.getHour(),
                        due.getMinute());
                subAsset.setNextPaymentDue(formatted);

                currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                        "Payment",
                        "Subscription: " + assetName,
                        amount,
                        java.time.LocalDate.now().toString(),
                        "Positive"
                ));
            }

            // ===== PENDING DEBT PAYMENT LOGIC =====
            else if (entityType.equals("pendingDebt")) {
                double pendingDebt = currentUser.getPendingDebt();

                // Do not allow paying more than the current pending debt
                if (amount > pendingDebt) {
                    resultLabel.setVisible(true);
                    resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
                    resultLabel.setText("Amount exceeds pending debt.");
                    return;
                }

                sourceAccount.withdraw(amount);
                currentUser.addPendingDebt(-amount);

                currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                        "Debt Payment",
                        "Pending Debt",
                        amount,
                        java.time.LocalDate.now().toString(),
                        "Positive"
                ));

                currentUser.getNotificationManager().addNotification(new Notification(
                        "Pending debt payment of $" + String.format("%.2f", amount) + ". Remaining debt: $" + String.format("%.2f", currentUser.getPendingDebt()) + ".",
                        "Transaction",
                        "Banking",
                        java.time.LocalDate.now().toString()
                ));
            }

            // If the method reaches this point, the payment was successful
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #16911b; -fx-font-size: 14px; -fx-font-weight: bold;");
            resultLabel.setText("Payment successful!");

            // Give the user a moment to see the success message before closing the popup
            javafx.animation.Timeline delay = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                        Stage stage = (Stage) resultLabel.getScene().getWindow();
                        stage.close();
                    })
            );
            delay.play();

        }
        catch (NumberFormatException e) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 14px; -fx-font-weight: bold;");
            resultLabel.setText("Please enter a valid number.");
        }
    }

    // Closes the payment popup without making any changes.
    @FXML
    private void handleBack() {
        Stage stage = (Stage) resultLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows a short help popup for the payment screen.
     * This help text summarizes the payment rules for the different payment
     * types supported by this popup.
     */
    @FXML
    private void handleHelp() {
        MainApp.showStyledPopup(
                "generalerror.drawio.png", "#367A46",
                "Help", "Payment Help",
                "Pay off balances and bills.\n\n" +
                        "• Credit Card: Pay at least the minimum payment to avoid $10 late fees. Paying more reduces debt faster.\n" +
                        "• Subscriptions: Must pay the full cycle amount. Late subscriptions add $5 per missed cycle.\n" +
                        "• Pending Debt: Pay any amount toward debt from scenario outcomes.\n" +
                        "• Select which account to pay FROM using the dropdown\n" +
                        "• Credit cards can only be paid from Debit\n" +
                        "• Subscriptions can be paid from Debit or Credit",
                new ButtonType("Okay")
        );
    }
}