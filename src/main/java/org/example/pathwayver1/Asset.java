/**
 * Asset represents an item or subscription owned by the user.
 *
 * This class is used by the Assets feature to track both one-time items and
 * recurring subscription-style items. Because those two asset types behave a
 * little differently, this class stores shared fields such as name and value,
 * along with subscription-specific fields such as payment status, next due
 * date, and recurring cost.
 *
 * One-time items can usually be resold later.
 * Subscription items can build recurring payment behavior over time.
 */

package org.example.pathwayver1;

public class Asset {

    // Unique identifier for the asset
    private String assetID;
    // Display name shown in the UI
    private String name;
    // Used to distinguish one-time items from subscriptions
    private String itemType; // "One-time" or "Subscription"
    // Original purchase value of the asset
    private double purchaseValue;
    // Amount the asset can be sold for later
    private double resaleValue;
    // Recurring charge used for subscription-type assets
    private double recurringCost;
    // Whether the user currently owns the asset
    private boolean owned;
    // Next scheduled due date for subscription payments
    private String nextPaymentDue;
    // Tracks whether the current cycle is paid or unpaid
    private String paymentStatus; // "Paid" or "Unpaid"
    // Readable billing cycle text used in the UI
    private String billingCycle;
    // File name for the icon shown in the Assets screen
    private String iconFileName;
    // Base recurring cost before any late-fee increases are applied
    private double baseCost;

    // Simple static counter used to generate unique asset IDs
    private static int idCounter = 0;

    // Tracks whether a late fee has already been applied for this cycle
    private boolean lateFeeApplied = false;

    /**
     * Creates a new asset with its core values.
     *
     * If the asset is a subscription, it starts as Paid and is given an initial
     * payment due date. If it is a one-time item, the recurring-related fields
     * are set to N/A.
     */
    public Asset(String name, String itemType, double purchaseValue,
                 double resaleValue, double recurringCost, String iconFileName) {
        this.assetID = "ASSET-" + (++idCounter);
        this.name = name;
        this.itemType = itemType;
        this.purchaseValue = purchaseValue;
        this.resaleValue = resaleValue;
        this.recurringCost = recurringCost;
        this.baseCost = recurringCost;
        this.owned = true;
        this.iconFileName = iconFileName;

        if (itemType.equals("Subscription")) {
            this.paymentStatus = "Paid";

            // For this project, subscription due dates are generated shortly ahead
            // so the cycle can be tested during normal runtime.
            java.time.LocalDateTime due = java.time.LocalDateTime.now().plusMinutes(20);
            this.nextPaymentDue = String.format("%s %d, %d:%02d",
                    due.getMonth().toString().substring(0, 3),
                    due.getDayOfMonth(),
                    due.getHour(),
                    due.getMinute());
        }
        else {
            this.billingCycle = "N/A";
            this.paymentStatus = "N/A";
            this.nextPaymentDue = "N/A";
        }
    }

    // Returns the asset's unique ID.
    public String getAssetID() {
        return assetID;
    }

    // Returns the asset's display name.
    public String getName() {
        return name;
    }

    // Returns the asset type.
    public String getItemType() {
        return itemType;
    }

    // Returns the original purchase value of the asset.
    public double getPurchaseValue() {
        return purchaseValue;
    }

    // Returns the resale value of the asset.
    public double getResaleValue() {
        return resaleValue;
    }

    /**
     * Returns the current recurring cost for this asset.
     * For subscriptions, this may change if late fees are applied.
     */
    public double getRecurringCost() {
        return recurringCost;
    }

    /**
     * Updates the recurring cost.
     * This is mainly used for subscription late-fee adjustments.
     */
    public void setRecurringCost(double cost) {
        this.recurringCost = cost;
    }

    // Returns whether the asset is still owned by the user.
    public boolean isOwned() {
        return owned;
    }

    //  Updates ownership status.
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    // Returns the next payment due date string.
    public String getNextPaymentDue() {
        return nextPaymentDue;
    }

    //  Updates the next payment due date string.
    public void setNextPaymentDue(String date) {
        this.nextPaymentDue = date;
    }

    //  Returns the current payment status.
    public String getPaymentStatus() {
        return paymentStatus;
    }

    // Updates the payment status.
    public void setPaymentStatus(String status) {
        this.paymentStatus = status;
    }

    /**
     * Returns a friendly billing-cycle-style string for subscriptions.
     *
     * Instead of returning a fixed label, this method calculates how much time
     * remains until the next due date and formats it for display.
     *
     * For one-time items, this always returns N/A.
     */
    public String getBillingCycle() {
        if (!itemType.equals("Subscription")) return "N/A";

        try {
            String dueStr = nextPaymentDue;
            if (dueStr.equals("N/A")) return "N/A";

            String[] parts = dueStr.split("[, ]+");
            if (parts.length < 3) return billingCycle;

            String monthStr = parts[0];
            int day = Integer.parseInt(parts[1]);
            String[] timeParts = parts[2].split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            int month = 1;
            switch (monthStr.toUpperCase()) {
                case "JAN": month = 1; break;
                case "FEB": month = 2; break;
                case "MAR": month = 3; break;
                case "APR": month = 4; break;
                case "MAY": month = 5; break;
                case "JUN": month = 6; break;
                case "JUL": month = 7; break;
                case "AUG": month = 8; break;
                case "SEP": month = 9; break;
                case "OCT": month = 10; break;
                case "NOV": month = 11; break;
                case "DEC": month = 12; break;
            }

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime dueDate = java.time.LocalDateTime.of(now.getYear(), month, day, hour, minute);
            long minutesLeft = java.time.Duration.between(now, dueDate).toMinutes();

            if (minutesLeft <= 0) {
                return "Due now!";
            }
            else if (minutesLeft < 60)
            {
                return minutesLeft + " min remaining";
            }
            else
            {
                long hours = minutesLeft / 60;
                long mins = minutesLeft % 60;
                return hours + "h " + mins + "m remaining";
            }
        }

        catch (Exception e) {
            return billingCycle; // If parsing fails for any reason, fall back to the stored value
        }
    }

    // Returns the icon filename associated with this asset.
    public String getIconFileName() {
        return iconFileName;
    }

    // Returns whether a late fee has already been applied.
    public boolean isLateFeeApplied() {
        return lateFeeApplied;
    }

    // Updates the late-fee-applied flag.
    public void setLateFeeApplied(boolean applied) {
        this.lateFeeApplied = applied;
    }

    /**
     * Returns the base recurring cost before late fees.
     *
     * This is useful when resetting a subscription back to its normal cost after
     * the user pays it off.
     */
    public double getBaseCost() {
        return baseCost;
    }
}