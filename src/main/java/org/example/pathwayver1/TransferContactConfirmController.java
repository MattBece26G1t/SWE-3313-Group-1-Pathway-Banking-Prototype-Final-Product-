/**
 * TransferContactConfirmController manages the final confirmation screen for
 * sending money to a contact.
 *
 * This controller is responsible for showing who the money is being sent to,
 * validating the transfer amount, checking whether the user's Debit account
 * has enough funds, and then completing the transfer through BankingManager.
 *
 * It also supports 2 slightly different use cases:
 * 1. a normal contact transfer started by the user
 * 2. a transfer request that came from a scenario and already has a required
 *    amount attached to it
 *
 * In the second case, the amount field is prefilled and locked so the user
 * completes the specific transfer requested by the scenario.
 */

package org.example.pathwayver1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;


public class TransferContactConfirmController {

    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private TextField transferAmountField;
    @FXML private Label resultLabel;
    @FXML private Button transferButton;

    private UserAccount currentUser; // Current logged-in user
    private String selectedContact; // Name of the selected contact receiving the transfer
    private BankingManager bankingManager; // Handles the actual transfer logic once validation is done

    /*
     * If this screen was opened from a scenario created transfer request,
     * the request object is stored here so it can be marked complete
     * after a successful transfer.
     */
    private TransferRequest activeRequest = null;

    /*
     * This is only used for regular contact transfers.
     * After the user has already completed one transfer on this same screen,
     * the controller asks for extra confirmation before allowing another.
     */
    private boolean hasTransferredOnce = false;

    /**
     * Initializes the confirmation screen.
     * The result label starts hidden and only appears after validation
     * or after a successful transfer.
     */
    @FXML
    private void initialize() {
        resultLabel.setVisible(false);
    }

    /**
     * Loads the current user and fills in the sender field.
     * Contact transfers always come from the default Debit account, so this
     * method builds that label immediately and places it in the read only
     * From field.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.bankingManager = user.getBankingManager();
        Account debit = user.getAccounts().get(0);
        fromField.setText(user.getFirstName() + " - DEBIT ACC - PB " + debit.getLastFour());
    }

    // Sets the contact receiving the transfer and updates the To field
    public void setSelectedContact(String contact) {
        this.selectedContact = contact;
        toField.setText(contact);
    }

    /**
     * Handles the Transfer button.
     *
     * Main steps:
     * 1. optionally ask for confirmation if the user already transferred once
     *    from this same popup
     * 2. validate the amount field
     * 3. verify that the default Debit account has enough funds
     * 4. complete the transfer through BankingManager
     * 5. mark the transfer request complete if this transfer came from a scenario
     * 6. show a success message
     *
     * For scenario based transfer requests, the button is disabled after a
     * successful transfer so the same request cannot be paid twice from the
     * same screen.
     */
    @FXML
    private void handleTransfer() {
        resultLabel.setVisible(false);

        /*
         * For regular transfers, this extra confirmation helps prevent the user
         * from sending the same transfer again by mistake while still on the popup.
         */
        if (hasTransferredOnce) {
            java.util.Optional<ButtonType> confirm = MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Transfer", "Repeat Transfer",
                    "Are you sure you want to transfer to " + selectedContact + " again?",
                    new ButtonType("Yes"), new ButtonType("No")
            );
            if (!confirm.isPresent() || !confirm.get().getText().equals("Yes")) {
                return;
            }
        }

        String amountText = transferAmountField.getText().trim();

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

            Account debitAccount = currentUser.getAccounts().get(0);

            if (debitAccount.getBalance() < amount) {
                resultLabel.setVisible(true);
                resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
                resultLabel.setText("Insufficient funds in debit account.");
                return;
            }

            bankingManager.transferToContact(debitAccount, selectedContact, amount);

            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #16911b; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Transfer to " + selectedContact + " successful!");

            /*
             * If this popup came from a pending scenario request, mark that
             * request completed and remove it from the user's pending list.
             * The button is also disabled so the same request cannot be reused.
             */
            if (activeRequest != null) {
                transferButton.setDisable(true);
            }
            else {
                hasTransferredOnce = true;
            }

            if (activeRequest != null) {
                activeRequest.setCompleted(true);
                currentUser.removeCompletedRequests();
            }

            // Leave the success message visible briefly before clearing it
            Timeline delay = new Timeline(
                    new KeyFrame(Duration.seconds(3), e -> {
                        resultLabel.setVisible(false);
                    })
            );
            delay.play();

        }
        catch (NumberFormatException e) {
            resultLabel.setVisible(true);
            resultLabel.setStyle("-fx-text-fill: #c75436; -fx-font-size: 17px; -fx-font-weight: bold;");
            resultLabel.setText("Please enter a valid number.");
        }
    }

    /**
     * Loads a scenario-created transfer request into the popup.
     * This method stores the request object, pre-fills the required amount,
     * and locks the amount field so the user cannot change the amount tied
     * to that request.
     */
    public void prefillTransferRequest(TransferRequest request) {
        this.activeRequest = request;
        transferAmountField.setText(String.format("%.2f", request.getAmount()));
        transferAmountField.setEditable(false);
    }

    /**
     * Returns to the contact list screen.
     * This is the normal back behavior for contact transfer navigation.
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("TransferContactListView.fxml")
            );
            Stage stage = (Stage) resultLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            TransferContactListController controller = loader.getController();
            controller.loadUserData(currentUser);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Shows a short help popup explaining how the confirmation screen works
    @FXML
    private void handleHelp() {
        MainApp.showStyledPopup(
                "generalerror.drawio.png", "#367A46",
                "Help", "Transfer - Confirm Help",
                "Confirm your transfer to a contact.\n\n" +
                        "• Your name and the contact's name are shown\n" +
                        "• Enter the amount to transfer\n" +
                        "• Press Transfer to send the money\n" +
                        "• Funds come from your Debit account",
                new ButtonType("Okay")
        );
    }
}