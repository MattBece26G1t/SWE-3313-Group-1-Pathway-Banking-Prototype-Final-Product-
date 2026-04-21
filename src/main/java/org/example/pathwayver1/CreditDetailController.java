/*
 * CreditDetailController handles the detailed view for one credit account.
 *
 * This screen shows the account number, balance owed, due date, minimum payment,
 * available credit, and that account's transaction history.
 *
 * From here, the user can also:
 * - open the payment popup
 * - delete the credit account if the balance is fully paid off
 * - go back to the account balance page
 * - open the help screen
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

public class CreditDetailController {

    @FXML private Label accountNumLabel;
    @FXML private Label balanceLabel;
    @FXML private Label paymentDueDateLabel;
    @FXML private Label minimumPaymentLabel;
    @FXML private Label availableCreditLabel;
    @FXML private Label creditLimitLabel;
    @FXML private ScrollPane historyScrollPane;
    @FXML private VBox historyList;

    private UserAccount currentUser; // Current logged-in user
    private Account creditAccount;  // The specific credit account this page is showing

    @FXML
    private void initialize() {
    }

    /*
     * Loads the selected credit account into the screen.
     * This is called right after switching into the detail view.
     */
    public void loadUserData(UserAccount user, Account account) {
        this.currentUser = user;
        this.creditAccount = account;

        accountNumLabel.setText("ACC#: " + account.getAccountNumber());
        balanceLabel.setText(String.format("%.2f", account.getBalanceOwed()));
        paymentDueDateLabel.setText(account.getPaymentDueDate());
        minimumPaymentLabel.setText(String.format("%.2f", account.getMinimumPayment()));
        creditLimitLabel.setText(String.format("%.2f", account.getCreditLimit()));
        availableCreditLabel.setText(String.format("%.2f", account.getAvailableCredit()));

        loadHistory();
    }

    /*
     * Builds the transaction history list for just this credit account.
     * It filters the user's full banking history down to records that match
     * this account's last four digits.
     */
    private void loadHistory() {
        historyList.getChildren().clear();

        ArrayList<TransactionRecord> records = currentUser.getBankingManager().getTransactionHistory();

        // Go backwards so the newest items show first
        for (int i = records.size() - 1; i >= 0; i--) {
            TransactionRecord record = records.get(i);

            if (record.getAssociatedAccount().contains(creditAccount.getLastFour())) {
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

        // Fallback message if nothing has happened on this account yet
        if (historyList.getChildren().isEmpty()) {
            Label empty = new Label("No transaction history.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Myanmar Text';");
            historyList.getChildren().add(empty);
        }
    }

    /*
     * Opens the payment popup and preloads it with this credit account.
     * When the popup closes, this detail page refreshes so updated balances show.
     */

    @FXML
    private void handlePay() {
        try {
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
            controller.loadCreditPayment(creditAccount);

            // Refresh this screen after the payment window closes
            paymentStage.setOnHidden(e -> {
                loadUserData(currentUser, creditAccount);
            });

            paymentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Deletes the credit account, but only if the user does not owe anything.
     * If there is still a balance, the account stays locked in place.
     */
    @FXML
    private void handleDelete() {
        double balance = creditAccount.getBalanceOwed();

        if (balance > 0) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Delete Credit Account", "Cannot Delete",
                    "All payments must be covered first before deletion.",
                    new ButtonType("Okay")
            );
        }

        else {
            java.util.Optional<ButtonType> result = MainApp.showStyledPopup(
                    "deletecredit.drawio.png", "#C3BCEB",
                    "Delete Credit Account", "Delete Account",
                    "Are you sure you'd like to delete this credit account?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                currentUser.getAccounts().remove(creditAccount);

                MainApp.showStyledPopup(
                        "deletecredit.drawio.png", "#C3BCEB",
                        "Account Deleted", "Account Deleted",
                        "CREDIT ACC - PB " + creditAccount.getLastFour() + " deleted.",
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

    // Sends the user back to the main account balance page.
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the help page and tags it as coming from the credit detail screen.
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
            controller.setCameFrom("creditDetail");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}