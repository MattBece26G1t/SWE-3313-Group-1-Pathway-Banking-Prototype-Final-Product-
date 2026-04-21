/**
 * WalletEvent represents one recorded action involving the user's wallet.
 * This class is used to store the information shown in the Wallet history
 * screen. Each event records what kind of wallet action happened, how much
 * money was involved, where that action came from, and the date it occurred.
 * The actual wallet logic is handled by WalletManager, while WalletController
 * uses objects of this class to display the wallet history to the user.
 */

package org.example.pathwayver1;

public class WalletEvent {

    // General label for the wallet action, such as Received, Withdrawn, or Debt Payment
    private String eventType;
    // Dollar amount tied to the event
    private double amount;
    // Short description of where the wallet event came from
    private String source;
    // Date the event was recorded
    private String date;

    /**
     * Creates one wallet event with its full saved details.
     * This constructor is used whenever WalletManager wants to record a new
     * wallet related action into the wallet history list.
     */
    public WalletEvent(String eventType, double amount, String source, String date) {
        this.eventType = eventType;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    // Returns the type of wallet event
    public String getEventType() {
        return eventType;
    }

    // Returns the amount involved in the wallet event
    public double getAmount() {
        return amount;
    }

    // Returns the source or explanation attached to the wallet event
    public String getSource() {
        return source;
    }

    // Returns the date the wallet event was recorded
    public String getDate() {
        return date;
    }
}