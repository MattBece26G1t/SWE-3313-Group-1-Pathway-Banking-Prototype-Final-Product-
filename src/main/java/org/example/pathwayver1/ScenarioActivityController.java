/**
 * ScenarioActivityController manages the screen where the user actually plays
 * through a scenario.
 *
 * This controller does more than just show the scenario text. It is also
 * responsible for processing the player's response and applying the result to
 * the rest of the program. Depending on the selected option, the controller may:
 * add money to the wallet, deduct money from the wallet or accounts, create
 * pending debt, award assets, create transfer requests, mark the scenario as
 * completed, record the result in transaction history, and display the final
 * feedback popup.
 * this class is one of the more logic heavy controllers in
 * the project. It acts like the bridge between the scenario UI and several
 * other systems such as banking, assets, notifications, and feedback.
 */

package org.example.pathwayver1;
import org.example.pathwayver1.TransferRequest;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ScenarioActivityController {

    @FXML private Label scenarioTitleLabel;
    @FXML private Label chatBubbleLabel;
    @FXML private Label response1Label;
    @FXML private Label response2Label;
    @FXML private Label response3Label;
    @FXML private Label response4Label;

    @FXML private Button response1Button;
    @FXML private Button response2Button;
    @FXML private Button response3Button;
    @FXML private Button response4Button;

    @FXML private ImageView beginnerBadge;
    @FXML private ImageView advancedBadge;

    private UserAccount currentUser; // Current logged-in user
    private Scenario currentScenario; // Scenario currently being shown on this screen

    // Managers used during scenario processing
    private ScenarioManager scenarioManager;
    private FeedbackManager feedbackManager;

    /*
     * Stores a readable explanation of where money was taken from during
     * a negative scenario result. This gets shown in the feedback popup.
     */
    private String lastDeductionSource = "";

    /**
     * Initializes the controller.
     * No setup is needed here yet because the screen depends on loadUserData()
     * and loadScenario() before it has anything meaningful to show.
     */
    @FXML
    private void initialize() {
    }

    // Stores the current user and pulls the managers needed for this screen.
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.scenarioManager = user.getScenarioManager();
        this.feedbackManager = user.getFeedbackManager();
    }

    /**
     * Loads one specific scenario into the activity screen.
     *
     * This method:
     * - marks the scenario as in progress
     * - shows the title and description
     * - displays the correct difficulty badge
     * - fills in up to four response choices
     * If the scenario has fewer than four options, the extra labels and buttons
     * are hidden so the layout still looks clean.
     */
    public void loadScenario(Scenario scenario) {
        this.currentScenario = scenario;
        scenario.setCompletionStatus("In Progress");

        scenarioTitleLabel.setText(scenario.getTitle());
        chatBubbleLabel.setText(scenario.getDescription());

        if (scenario.getDifficultyLevel().equalsIgnoreCase("Beginner")) {
            beginnerBadge.setVisible(true);
            advancedBadge.setVisible(false);
        }

        else {
            beginnerBadge.setVisible(false);
            advancedBadge.setVisible(true);
        }

        ArrayList<String> options = scenario.getOptions();

        response1Label.setText(options.get(0));
        response1Label.setVisible(true);
        response1Button.setVisible(true);

        response2Label.setText(options.get(1));
        response2Label.setVisible(true);
        response2Button.setVisible(true);

        if (options.size() >= 3) {
            response3Label.setText(options.get(2));
            response3Label.setVisible(true);
            response3Button.setVisible(true);
        }

        else {
            response3Label.setVisible(false);
            response3Button.setVisible(false);
        }

        if (options.size() >= 4) {
            response4Label.setText(options.get(3));
            response4Label.setVisible(true);
            response4Button.setVisible(true);
        }

        else {
            response4Label.setVisible(false);
            response4Button.setVisible(false);
        }
    }

    /**
     * Handles the first response button.
     * The actual logic is centralized in processResponse(), so each button only
     * passes the correct option index.
     */
    @FXML
    private void handleResponse1() {
        processResponse(0);
    }

    // Handles the second response button
    @FXML
    private void handleResponse2() {
        processResponse(1);
    }

    // Handles the third response button
    @FXML
    private void handleResponse3() {
        processResponse(2);
    }

    // Handles the fourth response button
    @FXML
    private void handleResponse4() {
        processResponse(3);
    }

    /**
     * Processes the user's selected scenario response.
     *
     * This method is the core of the scenario system. After a choice is made,
     * it performs all related updates in one place:
     * - generates feedback
     * - applies rewards or deductions
     * - awards assets when appropriate
     * - creates transfer requests for certain scenarios
     * - marks the scenario as completed
     * - records a transaction
     * - sends a notification
     * - shows the feedback popup
     *
     * The large amount of branching here comes from the fact that different
     * scenarios can affect the program in very different ways.
     */
    private void processResponse(int choice) {
        FeedbackMessage feedback = feedbackManager.generateScenarioFeedback(currentScenario, choice);
        double reward = scenarioManager.getRewardAmount(currentScenario, choice);

        // ===== Handle positive and negative money results =====
        // Apply reward to wallet
        if (reward > 0) {
            /*
             * Positive scenario results are deposited into the wallet.
             * WalletManager handles the extra check for auto-paying pending debt.
             */
            currentUser.getWalletManager().depositToWalletWithDebtCheck(reward, "Scenario Reward: " + currentScenario.getTitle(), currentUser);
        }

        else if (reward < 0) {
            String deductionSource = "";

            /*
             * Certain scenario IDs are handled differently because they create
             * transfer requests instead of taking money immediately.
             */
            String sid = currentScenario.getScenarioID();
            if (sid.equals("S014") || sid.equals("S015") || sid.equals("S021") ||
                    sid.equals("S022") || sid.equals("S023")) {
                deductionSource = "Transfer request created";
            }

            else {
                double loss = Math.abs(reward);
                double remaining = loss;

                // 1. Try to take the loss from the wallet first
                double walletBalance = currentUser.getWalletManager().getBalance();
                if (walletBalance > 0 && remaining > 0) {
                    double walletPortion = Math.min(walletBalance, remaining);
                    currentUser.getWalletManager().getWallet().removeFunds(walletPortion);
                    currentUser.getWalletManager().recordEvent(new WalletEvent(
                            "Deducted",
                            walletPortion,
                            "Scenario: " + currentScenario.getTitle(),
                            java.time.LocalDate.now().toString()
                    ));
                    remaining -= walletPortion;
                    deductionSource += "Wallet: -$" + String.format("%.2f", walletPortion);
                }

                // 2. If the wallet was not enough, try any available credit accounts next
                if (remaining > 0) {
                    for (Account acc : currentUser.getAccounts()) {

                        if (acc.getAccountType().equalsIgnoreCase("Credit") && acc.getAvailableCredit() > 0) {
                            double creditPortion = Math.min(acc.getAvailableCredit(), remaining);
                            acc.addCharge(creditPortion);
                            currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                                    "Credit Charge",
                                    "CREDIT ACC - PB " + acc.getLastFour(),
                                    creditPortion,
                                    java.time.LocalDate.now().toString(),
                                    "Negative"
                            ));
                            remaining -= creditPortion;

                            if (!deductionSource.isEmpty()) deductionSource += " | ";

                            deductionSource += "CREDIT ACC - PB " + acc.getLastFour() + ": -$" + String.format("%.2f", creditPortion);

                            if (remaining <= 0) break;
                        }
                    }
                }

                // 3. If money is still owed, use the default Debit account
                if (remaining > 0) {
                    Account debit = currentUser.getAccounts().get(0);

                    if (debit.getBalance() > 0) {
                        double debitPortion = Math.min(debit.getBalance(), remaining);
                        debit.withdraw(debitPortion);
                        currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                                "Scenario Deduction",
                                "DEBIT ACC - PB " + debit.getLastFour(),
                                debitPortion,
                                java.time.LocalDate.now().toString(),
                                "Negative"
                        ));
                        remaining -= debitPortion;

                        if (!deductionSource.isEmpty()) deductionSource += " | ";

                        deductionSource += "DEBIT ACC - PB " + debit.getLastFour() + ": -$" + String.format("%.2f", debitPortion);
                    }
                }

                // 4. Anything that still cannot be covered becomes pending debt
                if (remaining > 0) {
                    currentUser.addPendingDebt(remaining);

                    currentUser.getNotificationManager().addNotification(new Notification(
                            "You have accumulated $" + String.format("%.2f", remaining) + " in pending debt from scenario outcomes. Pay it off in the Payment section.",
                            "Alert",
                            "Banking",
                            java.time.LocalDate.now().toString()
                    ));

                    if (!deductionSource.isEmpty()) deductionSource += " | ";

                    deductionSource += "Pending Debt: $" + String.format("%.2f", remaining);
                }
            }

            lastDeductionSource = deductionSource;
        }

        // ===== Asset rewards tied to specific scenarios =====
        // Asset rewards from scenarios
        String scenarioID = currentScenario.getScenarioID();

        // One-time items
        if (scenarioID.equals("S001") && choice == 0) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Piggy Bank", "One-time", 20.0, 10.0, 0, "piggybank_asset.drawio.png"));
        }

        else if (scenarioID.equals("S003") && choice == 0) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Savings Journal", "One-time", 15.0, 7.0, 0, "journal_asset.drawio.png"));
        }

        else if (scenarioID.equals("S009") && choice == 1) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Repair Kit", "One-time", 5.0, 2.0, 0, "repairkit_asset.drawio.png"));
        }

        else if (scenarioID.equals("S010") && choice == 1) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Online Seller Badge", "One-time", 0, 0, 0, "sellerbadge_asset.drawio.png"));
        }

        else if (scenarioID.equals("S017") && choice == 1) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Headphones", "One-time", 25.0, 12.0, 0, "headphones_asset.drawio.png"));
        }

        else if (scenarioID.equals("S017") && choice == 2) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Headphones", "One-time", 25.0, 12.0, 0, "headphones_asset.drawio.png"));
        }

        // Subscription reward items
        if (scenarioID.equals("S004") && choice == 2) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Gaming Subscription", "Subscription", 10.0, 0, 5.0, "gaming_sub.drawio.png"));
        }

        else if (scenarioID.equals("S011") && (choice == 0 || choice == 2)) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Streaming Subscription", "Subscription", 8.0, 0, 8.0, "streaming_sub.drawio.png"));
        }

        else if (scenarioID.equals("S012") && choice != 1) {
            currentUser.getAssetManager().addAsset(new Asset(
                    "Electric Utility", "Subscription", 12.0, 0, 12.0, "electric_util.drawio.png"));
        }

        else if (scenarioID.equals("S012") && choice == 1) {
            Asset unpaidElectric = new Asset(
                    "Electric Utility", "Subscription", 12.0, 0, 12.0, "electric_util.drawio.png");
            unpaidElectric.setPaymentStatus("Unpaid");
            currentUser.getAssetManager().addAsset(unpaidElectric);
        }

        else if (scenarioID.equals("S013")) {
            if (choice == 1) {
                Asset unpaidWater = new Asset(
                        "Water Utility", "Subscription", 8.0, 0, 8.0, "water_util.drawio.png");
                unpaidWater.setPaymentStatus("Unpaid");
                currentUser.getAssetManager().addAsset(unpaidWater);
            }

            else {
                currentUser.getAssetManager().addAsset(new Asset(
                        "Water Utility", "Subscription", 8.0, 0, 8.0, "water_util.drawio.png"));
            }
        }

        else if (scenarioID.equals("S020") && (choice == 0 || choice == 1)) {
            double cost = (choice == 0) ? 10.0 : 20.0;
            currentUser.getAssetManager().addAsset(new Asset(
                    "Phone Plan", "Subscription", cost, 0, cost, "phoneplan_sub.drawio.png"));
        }

        // ===== Transfer requests created by specific scenarios =====
        // Contact transfer requests from scenarios
        if (scenarioID.equals("S014") && choice == 0) {
            TransferRequest request = new TransferRequest("Alex", 20.0, "Repaying loan");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S014") && choice == 2) {
            TransferRequest request = new TransferRequest("Alex", 10.0, "Partial repayment");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S015") && choice == 0) {
            TransferRequest request = new TransferRequest("Sister", 10.0, "School supplies help");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S015") && choice == 2) {
            TransferRequest request = new TransferRequest("Sister", 5.0, "Partial school supplies help");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S021") && choice == 0) {
            TransferRequest request = new TransferRequest("Mom", 15.0, "Grocery help");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S021") && choice == 1) {
            TransferRequest request = new TransferRequest("Mom", 10.0, "Partial grocery help");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S022") && choice == 0) {
            TransferRequest request = new TransferRequest("Jordan", 12.0, "Emergency ride");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S022") && choice == 1) {
            TransferRequest request = new TransferRequest("Jordan", 6.0, "Partial emergency ride");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        else if (scenarioID.equals("S023") && choice == 0) {
            TransferRequest request = new TransferRequest("Dad", 5.0, "Parking meter");
            currentUser.addTransferRequest(request);
            showTransferPrompt(request);
        }

        // ===== Final updates after the scenario response is fully processed =====
        // Mark scenario as completed
        scenarioManager.markCompleted(currentScenario);

        // Record transaction
        currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                "Scenario",
                currentScenario.getTitle(),
                reward,
                java.time.LocalDate.now().toString(),
                feedback.getFeedbackType().equals("Positive") ? "Positive" :
                        feedback.getFeedbackType().equals("Negative") ? "Negative" : "Neutral"
        ));

        // Notify
        currentUser.getNotificationManager().addNotification(new Notification(
                "Scenario '" + currentScenario.getTitle() + "' completed. " + feedback.getMessageText(),
                "Scenario",
                "Educational",
                java.time.LocalDate.now().toString()
        ));

        // Show feedback popup
        showFeedbackPopup(feedback);
    }

    /**
     * Shows a popup for scenario created transfer requests.
     * The user can either:
     * - transfer the money immediately
     * - leave it for later, which keeps the request in the payment system
     * This method also updates lastDeductionSource so the feedback popup can
     * explain what happened financially.
     */
    private void showTransferPrompt(TransferRequest request) {
        java.util.Optional<ButtonType> result = MainApp.showStyledPopup(
                "generalerror.drawio.png", "#D66242",
                "Transfer Requested", "Transfer to " + request.getContactName(),
                "You owe " + request.getContactName() + " $" + String.format("%.2f", request.getAmount()) +
                        ". Would you like to transfer now?",
                new ButtonType("Transfer Now"), new ButtonType("Not Yet")
        );

        if (result.isPresent() && result.get().getText().equals("Transfer Now")) {
            Account debit = currentUser.getAccounts().get(0);

            if (debit.getBalance() >= request.getAmount()) {
                currentUser.getBankingManager().transferToContact(debit, request.getContactName(), request.getAmount());
                request.setCompleted(true);
                currentUser.removeCompletedRequests();
                lastDeductionSource = "Transferred $" + String.format("%.2f", request.getAmount()) + " to " + request.getContactName();

                request.setCompleted(true);
                currentUser.removeCompletedRequests();

                MainApp.showStyledPopup(
                        "depositicon.drawio.png", "#EBF7A6",
                        "Transfer Complete", "Transfer Successful",
                        "Transferred $" + String.format("%.2f", request.getAmount()) + " to " + request.getContactName() + ".",
                        new ButtonType("Okay")
                );
            }

            else {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Transfer Failed", "Insufficient Funds",
                        "Not enough funds in your debit account. You can pay later from the Payment section.",
                        new ButtonType("Okay")
                );
                // Keep the request — it stays in payment dropdown
            }
        }

        else {
            currentUser.getNotificationManager().addNotification(new Notification(
                    "Reminder: You owe " + request.getContactName() + " $" + String.format("%.2f", request.getAmount()) +
                            ". Pay from the Payment section in Deposit/Withdraw.",
                    "Alert",
                    "Banking",
                    java.time.LocalDate.now().toString()
            ));
            lastDeductionSource = "Pending transfer: $" + String.format("%.2f", request.getAmount()) + " to " + request.getContactName();
            // Keep the request — it stays in payment dropdown
        }
    }

    /**
     * Displays the final feedback popup after a scenario is completed.
     *
     * The popup includes:
     * - the outcome text
     * - a header color based on positive/negative/cautionary feedback
     * - reward information or deduction details when relevant
     *
     * After the popup is dismissed, the user is returned to the scenario browser.
     */
    private void showFeedbackPopup(FeedbackMessage feedback) {
        String headerColor;
        if (feedback.getFeedbackType().equals("Positive")) {
            headerColor = "#A4E0B2";
        }

        else if (feedback.getFeedbackType().equals("Negative")) {
            headerColor = "#D66242";
        }

        else {
            headerColor = "#EDAC45";
        }

        /*
         * This tries to recover the reward tied to the feedback message so the
         * popup can display either earned money or deduction details.
         */
        double reward = scenarioManager.getRewardAmount(currentScenario,
                currentScenario.getOutcomes().indexOf(feedback.getMessageText()));

        String rewardText = "";

        if (reward > 0) {
            rewardText = "\n\n+ $" + String.format("%.2f", reward) + " added to Wallet.";
        }

        else if (reward < 0 && !lastDeductionSource.isEmpty()) {
            rewardText = "\n\nDeductions:\n" + lastDeductionSource.replace(" | ", "\n");
        }

        Alert alert = new Alert(Alert.AlertType.NONE, "", new ButtonType("Okay"));
        alert.setTitle("Scenario Feedback");
        alert.setHeaderText(feedback.getFeedbackType() + " Outcome");

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("images/feedbackicon.drawio.png"),
                        52, 52, true, true
                )
        ));

        Label feedbackLabel = new Label(feedback.getMessageText() + rewardText);
        feedbackLabel.setWrapText(true);
        feedbackLabel.setPrefWidth(400);
        feedbackLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(feedbackLabel);
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(scrollPane);
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: " + headerColor + ";"
        );

        alert.showAndWait();

        returnToScenarioBrowser();
    }

    // Returns the user to the Scenario browser after finishing an activity
    private void returnToScenarioBrowser() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ScenarioView.fxml")
            );
            Stage stage = (Stage) scenarioTitleLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            ScenarioController controller = loader.getController();
            controller.loadUserData(currentUser);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a small hint popup.
     * Right now the hint text is general rather than scenario specific.
     * Intended for further development but cut short due to time constraints
     */
    @FXML
    private void handleIdea() {
        String hint = "";
        ArrayList<Double> rewards = currentScenario.getRewards();
        double bestReward = -999;
        int bestIndex = 0;

        for (int i = 0; i < rewards.size(); i++) {
            if (rewards.get(i) > bestReward) {
                bestReward = rewards.get(i);
                bestIndex = i;
            }
        }

        hint = "Think about which option helps you save or grow your money. Consider the long-term impact of your choice, not just what feels good right now.";

        MainApp.showStyledPopup(
                "ideaicon.drawio.png", "#A4E0B2",
                "Idea", "Need a nudge?",
                hint,
                new ButtonType("Okay")
        );
    }

    /**
     * Handles the Back button.
     * Mandatory scenarios cannot be exited until they are completed. For normal
     * scenarios, this simply returns the user to the scenario browser.
     */
    @FXML
    private void handleBack() {
        if (currentScenario != null && currentScenario.isMandatory() &&
                currentUser.isMandatoryScenarioEnabled() &&
                !currentScenario.getCompletionStatus().equals("Completed")) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Cannot Leave", "Mandatory Scenario",
                    "You must complete this emergency scenario before leaving.",
                    new ButtonType("Okay")
            );
            return;
        }
        returnToScenarioBrowser();
    }

    /**
     * Opens the Help screen for scenario activity.
     * Just like the Back button, this is blocked during an unfinished mandatory
     * scenario. For non-mandatory cases, the controller resets the scenario back
     * to Available if it was still only marked In Progress, then opens Help.
     */
    @FXML
    private void handleHelp() {

        if (currentScenario != null && currentScenario.isMandatory() &&
                currentUser.isMandatoryScenarioEnabled() &&
                !currentScenario.getCompletionStatus().equals("Completed")) {
            MainApp.showStyledPopup(
                    "generalerror.drawio.png", "#D66242",
                    "Cannot Leave", "Mandatory Scenario",
                    "You must complete this emergency scenario before leaving.",
                    new ButtonType("Okay")
            );
            return;
        }

        /*
         * If the user opens Help before finishing a normal scenario, restore the
         * status so it does not stay stuck as In Progress unnecessarily.
         */
        if (currentScenario != null && currentScenario.getCompletionStatus().equals("In Progress")) {
            currentScenario.setCompletionStatus("Available");
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) scenarioTitleLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("scenarioActivity");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}