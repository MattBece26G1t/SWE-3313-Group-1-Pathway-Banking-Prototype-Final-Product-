/**
 * WalletController manages the Wallet screen of the application.
 * This controller is mainly responsible for showing the user's current wallet
 * balance and displaying the wallet's event history in a readable list.
 * The history includes things like received funds, withdrawals, scenario
 * rewards, and debt related wallet activity.
 * This screen can be opened from more than one place, so the controller also
 * tracks where the user came from. That way, the Back button can return the
 * user to the correct previous screen.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class WalletController {

    @FXML private Label walletBalanceLabel;
    @FXML private ScrollPane walletScrollPane;
    @FXML private VBox walletHistoryList;

    private UserAccount currentUser; // Current logged in user
    private String cameFrom; // Keeps track of which screen opened the wallet page

    /**
     * Initializes the Wallet screen.
     * No special setup is needed here because the screen depends on
     * loadUserData() to know what user data should be shown.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Loads the wallet data for the current user.
     * This method updates the visible wallet balance and then builds the
     * wallet history list underneath it.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        walletBalanceLabel.setText(String.format("%.2f", user.getWalletManager().getBalance()));
        loadHistory();
    }

    /**
     * Stores which screen opened the wallet page.
     * This is used later by handleBack() so the user can be returned to the
     * correct place instead of always being sent to the same screen.
     */
    public void setCameFrom(String source) {
        this.cameFrom = source;
    }

    /**
     * Rebuilds the wallet history list.
     * The wallet events are shown newest first, so the method loops through
     * the stored event list in reverse order. If there is no wallet history
     * yet, a fallback message is shown instead.
     */
    private void loadHistory() {
        walletHistoryList.getChildren().clear();

        ArrayList<WalletEvent> events = currentUser.getWalletManager().getWalletEvents();

        for (int i = events.size() - 1; i >= 0; i--) {
            WalletEvent event = events.get(i);
            HBox entry = createHistoryEntry(event);
            walletHistoryList.getChildren().add(entry);
        }

        if (walletHistoryList.getChildren().isEmpty()) {
            Label empty = new Label("No wallet activity yet.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");
            walletHistoryList.getChildren().add(empty);
        }
    }

    /**
     * Creates one visual row for a wallet event.
     * Each row shows the event type, amount, source, and date in one line.
     * The method returns the finished HBox so it can be added to the history list.
     */
    private HBox createHistoryEntry(WalletEvent event) {
        HBox entry = new HBox(10);
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPrefHeight(40);
        entry.setPrefWidth(600);
        entry.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8; -fx-padding: 8;");

        String text = event.getEventType() + " $" + String.format("%.2f", event.getAmount())
                + " — " + event.getSource()
                + " — " + event.getDate();

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'Myanmar Text';");

        entry.getChildren().add(textLabel);
        return entry;
    }

    /**
     * Returns the user to the correct previous screen.
     * If the wallet page was opened from Deposit/Withdraw, the controller sends
     * the user back there. Otherwise, it defaults to returning to the Dashboard
     */
    @FXML
    private void handleBack() {
        try {
            if ("depositWithdraw".equals(cameFrom)) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("DepositWithdrawView.fxml")
                );
                Stage stage = (Stage) walletScrollPane.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                DepositWithdrawController controller = loader.getController();
                controller.loadUserData(currentUser);
            }

            else {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("DashboardView.fxml")
                );
                Stage stage = (Stage) walletScrollPane.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                DashboardController controller = loader.getController();
                controller.loadUserData(currentUser);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Opens the Help screen for the wallet section
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) walletScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("wallet");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}