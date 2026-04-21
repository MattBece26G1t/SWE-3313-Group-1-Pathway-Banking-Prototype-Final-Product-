/**
 * This controller handles the Assets screen.
 *
 * The main job here is to show the user's earned items and subscriptions,
 * let them click an asset to see more detail, and then either sell the item
 * or cancel the subscription when allowed.
 *
 * This screen is split into two main parts:
 * 1. the asset grid on the left
 * 2. the detail panel on the right
 *
 * Once an asset is selected, the detail panel updates to show its info.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class AssetsController {

    @FXML private ScrollPane assetScrollPane;
    @FXML private VBox assetGrid;
    @FXML private ImageView assetDetailIcon;
    @FXML private Label assetNameLabel;
    @FXML private Label assetTypeLabel;
    @FXML private Label assetValueLabel;
    @FXML private Label assetResaleLabel;
    @FXML private Button sellButton;

    private UserAccount currentUser; // Logged-in user for this screen
    private AssetManager assetManager; // Shortcut to the user's asset manager
    private Asset selectedAsset; // Whatever asset the user most recently clicked on

    /**
     * Runs when the FXML first loads.
     * At startup, nothing is selected yet, so the detail area should be blank.
     */
    @FXML
    private void initialize() {
        clearDetailPanel();
    }

    /**
     * Called after navigation to this screen.
     * Stores the current user, grabs their asset manager,
     * and builds the asset list on screen.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.assetManager = user.getAssetManager();
        loadAssets();
    }

    /**
     * Rebuilds the asset grid from scratch.
     * If the user has no assets yet, we show a friendly message instead.
     * If they do have assets, we create one clickable box per asset.
     */
    private void loadAssets() {
        assetGrid.getChildren().clear();

        ArrayList<Asset> assets = assetManager.getOwnedAssets();

        if (assets.isEmpty()) {
            Label empty = new Label("No assets owned yet. Complete scenarios to earn items!");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");
            empty.setWrapText(true);
            assetGrid.getChildren().add(empty);
            return;
        }

        // FlowPane works nicely here since assets can wrap into rows naturally
        FlowPane grid = new FlowPane(10, 10);
        grid.setPrefWidth(450);
        grid.setStyle("-fx-background-color: transparent;");

        for (Asset asset : assets) {
            VBox itemBox = new VBox(5);
            itemBox.setAlignment(Pos.CENTER);
            itemBox.setPrefWidth(100);
            itemBox.setPrefHeight(100);
            itemBox.setStyle("-fx-cursor: hand; -fx-padding: 5;");

            // Subscriptions get a stronger color so they stand out from one-time items
            if (asset.getItemType().equals("Subscription"))
            {
                itemBox.setStyle(itemBox.getStyle() + "-fx-background-color: rgba(214,98,66,0.3); -fx-background-radius: 8;");
            }
            else {
                itemBox.setStyle(itemBox.getStyle() + "-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");
            }

            // Try to load the icon image for this asset
            try {
                ImageView icon = new ImageView(new Image(
                        getClass().getResourceAsStream("images/" + asset.getIconFileName())
                ));
                icon.setFitWidth(60);
                icon.setFitHeight(60);
                icon.setPreserveRatio(true);
                itemBox.getChildren().add(icon);
            }
            // If the image can't be found, show a simple fallback instead
            catch (Exception e) {
                Label placeholder = new Label("?");
                placeholder.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
                itemBox.getChildren().add(placeholder);
            }

            // Small label under the icon so the user knows what they are clicking
            Label nameLabel = new Label(asset.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-family: 'Myanmar Text';");
            nameLabel.setWrapText(true);
            nameLabel.setAlignment(Pos.CENTER);
            itemBox.getChildren().add(nameLabel);

            // Clicking an item updates the detail panel on the right
            itemBox.setOnMouseClicked(e -> {
                selectedAsset = asset;
                showDetail(asset);
            });

            grid.getChildren().add(itemBox);
        }

        assetGrid.getChildren().add(grid);
    }

    /**
     * Fills the right-side detail panel with information about the selected asset.
     * This is where the screen decides how to label the asset based on whether
     * it is a one-time item or a subscription.
     */
    private void showDetail(Asset asset) {
        try {
            assetDetailIcon.setImage(new Image(
                    getClass().getResourceAsStream("images/" + asset.getIconFileName())
            ));
            assetDetailIcon.setVisible(true);
        }
        // If the image is missing, just hide the big preview icon
        catch (Exception e) {
            assetDetailIcon.setVisible(false);
        }

        assetNameLabel.setText(asset.getName());
        assetTypeLabel.setText(asset.getItemType());
        if (asset.getItemType().equals("Subscription")) {
            assetValueLabel.setText("Cost: $" + String.format("%.2f", asset.getRecurringCost()) + "/cycle");
        }
        else {
            assetValueLabel.setText("Worth: $" + String.format("%.2f", asset.getPurchaseValue()));
        }

        if (asset.getItemType().equals("Subscription")) {
            assetResaleLabel.setText("Next Due: " + asset.getNextPaymentDue());
        }
        else {
            assetResaleLabel.setText("Resale: $" + String.format("%.2f", asset.getResaleValue()));
        }

        sellButton.setVisible(true);

        // Subscriptions use the same button, but the wording changes
        if (asset.getItemType().equals("Subscription") && asset.getPaymentStatus().equals("Unpaid")) {
            sellButton.setText("CANCEL SUB");
            sellButton.setDisable(true);
        }
        else if (asset.getItemType().equals("Subscription")) {
            sellButton.setText("CANCEL SUB");
            sellButton.setDisable(false);
        }
        else {
            sellButton.setText("SELL");
            sellButton.setDisable(false);
        }
    }

    /**
     * Clears the detail panel so it looks empty again.
     * This is useful when the screen first opens or after an asset is removed.
     */
    private void clearDetailPanel() {
        assetNameLabel.setText("");
        assetTypeLabel.setText("");
        assetValueLabel.setText("");
        assetResaleLabel.setText("");
        assetDetailIcon.setImage(null);
        sellButton.setVisible(false);
    }

    /**
     * Handles the action button on the detail panel.
     * For one-time items, this sells the asset and sends the resale money to the wallet.
     * For subscriptions, this cancels the subscription if the current cycle is paid.
     */
    @FXML
    private void handleSell() {
        if (selectedAsset == null) return;

        if (selectedAsset.getItemType().equals("Subscription")) {
            // If the current billing cycle is still unpaid, the user cannot cancel yet
            if (selectedAsset.getPaymentStatus().equals("Unpaid")) {
                MainApp.showStyledPopup(
                        "generalerror.drawio.png", "#D66242",
                        "Cannot Cancel", "Payment Required",
                        "You must pay off your current billing cycle before canceling this subscription.",
                        new ButtonType("Okay")
                );
                return;
            }

            // Confirm subscription cancellation before removing it
            java.util.Optional<ButtonType> result = MainApp.showStyledPopup(
                    "sellicon.drawio.png", "#E8F783",
                    "Cancel Subscription", "Confirm Cancellation",
                    "Are you sure you want to cancel " + selectedAsset.getName() + "? No refund will be given.",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                currentUser.getAssetManager().getOwnedAssets().remove(selectedAsset);

                // Record what happened in the transaction history
                currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                        "Subscription Cancelled",
                        selectedAsset.getName(),
                        0,
                        java.time.LocalDate.now().toString(),
                        "Neutral"
                ));

                // Also send a notification so the user can see the event in the Inbox
                currentUser.getNotificationManager().addNotification(new Notification(
                        "You cancelled your " + selectedAsset.getName() + " subscription.",
                        "Asset",
                        "Assets",
                        java.time.LocalDate.now().toString()
                ));

                selectedAsset = null;
                clearDetailPanel();
                loadAssets();
            }
        }

        else {
            // One-time assets are sold for their resale value
            java.util.Optional<ButtonType> result = MainApp.showStyledPopup(
                    "sellicon.drawio.png", "#E8F783",
                    "Sell Asset", "Confirm Sale",
                    "Are you sure you want to sell " + selectedAsset.getName() + " for $" +
                            String.format("%.2f", selectedAsset.getResaleValue()) + "?",
                    new ButtonType("Yes"), new ButtonType("No")
            );

            if (result.isPresent() && result.get().getText().equals("Yes")) {
                double resale = selectedAsset.getResaleValue();
                // Remove the asset from ownership
                currentUser.getAssetManager().getOwnedAssets().remove(selectedAsset);
                // Deposit the money into the user's wallet
                currentUser.getWalletManager().depositToWallet(resale, "Sold: " + selectedAsset.getName());

                // Keep the sale in the banking activity history too
                currentUser.getBankingManager().recordTransaction(new TransactionRecord(
                        "Asset Sale",
                        selectedAsset.getName(),
                        resale,
                        java.time.LocalDate.now().toString(),
                        "Positive"
                ));

                // Notify the user about the completed sale
                currentUser.getNotificationManager().addNotification(new Notification(
                        "You sold " + selectedAsset.getName() + " for $" + String.format("%.2f", resale) + ".",
                        "Asset",
                        "Assets",
                        java.time.LocalDate.now().toString()
                ));

                selectedAsset = null;
                clearDetailPanel();
                loadAssets();
            }
        }
    }

    // === Bottom Tab Navigation ===

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) assetScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) assetScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("assets");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}