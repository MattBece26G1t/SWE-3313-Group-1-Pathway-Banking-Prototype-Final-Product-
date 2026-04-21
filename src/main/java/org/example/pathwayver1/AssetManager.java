/**
 * AssetManager handles the collection of assets owned by a user.
 *
 * This class is responsible for:
 * - storing owned assets
 * - adding and removing assets
 * - finding assets by ID
 * - separating recurring subscription assets from one-time items
 * - processing subscription payments through the user's wallet
 *
 * It acts as the main backend helper for the Assets feature.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class AssetManager {

    // Stores all assets the user currently owns
    private ArrayList<Asset> ownedAssets;

    // Creates an empty asset manager.
    public AssetManager() {
        this.ownedAssets = new ArrayList<>();
    }

    // Adds a newly earned or acquired asset to the user's collection.
    public void addAsset(Asset asset) {
        ownedAssets.add(asset);
    }

    /**
     * Removes an asset by its ID.
     *
     * This is useful when deleting or clearing a specific asset without
     * needing the exact object reference.
     */
    public void removeAsset(String assetID) {
        ownedAssets.removeIf(a -> a.getAssetID().equals(assetID));
    }

    /**
     * Sells an owned asset and returns the resale amount.
     *
     * Return values:
     * - positive value: successful sale, resale amount returned
     * - 0: asset is not owned, so nothing can be sold
     * - -1: subscription cannot be sold because the current cycle is unpaid
     *
     * One-time assets are removed from the owned list after sale.
     * Subscription assets must be paid off before they can be canceled/sold.
     */
    public double sellAsset(Asset asset) {
        if (!asset.isOwned()) {
            return 0;
        }

        if (asset.getItemType().equals("Subscription") &&
                asset.getPaymentStatus().equals("Unpaid")) {
            return -1; // Signal that they must pay off first
        }

        double resale = asset.getResaleValue();
        asset.setOwned(false);
        ownedAssets.remove(asset);
        return resale;
    }

    // Returns all currently owned assets.
    public ArrayList<Asset> getOwnedAssets() {
        return ownedAssets;
    }

    /**
     * Finds and returns an asset by its unique ID.
     *
     * Returns null if no matching asset is found.
     */
    public Asset getAssetByID(String id) {
        for (Asset a : ownedAssets) {
            if (a.getAssetID().equals(id)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Returns only the assets that behave like recurring subscriptions.
     *
     * This is useful when checking for bills or recurring charges.
     */
    public ArrayList<Asset> getRecurringAssets() {
        ArrayList<Asset> recurring = new ArrayList<>();
        for (Asset a : ownedAssets) {
            if (a.getItemType().equals("Subscription")) {
                recurring.add(a);
            }
        }
        return recurring;
    }

    /**
     * Processes one recurring subscription payment using the user's wallet.
     *
     * behavior:
     * - If the asset is not a subscription, return false
     * - If the wallet has enough money, deduct the cost, mark it paid,
     *   and push the due date forward
     * - If the wallet does not have enough money, mark it unpaid
     *
     * Returns true when the payment succeeds, otherwise false.
     */
    public boolean processRecurringPayment(Asset asset, Wallet wallet) {
        if (!asset.getItemType().equals("Subscription")) {
            return false;
        }

        double cost = asset.getRecurringCost();
        if (wallet.getBalance() >= cost) {
            wallet.removeFunds(cost);
            asset.setPaymentStatus("Paid");

            // Move the next due date forward so the next cycle can be tracked
            java.time.LocalDateTime due = java.time.LocalDateTime.now().plusHours(4);
            String formatted = String.format("%s %d, %d:%02d",
                    due.getMonth().toString().substring(0, 3),
                    due.getDayOfMonth(),
                    due.getHour(),
                    due.getMinute());
            asset.setNextPaymentDue(formatted);
            return true;
        }
        // If the payment cannot be covered, the subscription remains due
        else
        {
            asset.setPaymentStatus("Unpaid");
            return false;
        }
    }
}