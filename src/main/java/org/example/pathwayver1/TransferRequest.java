/**
 * TransferRequest represents a pending money request connected to a contact.
 * This class is used when a scenario creates a follow up transfer obligation
 * instead of forcing the payment immediately. It stores who the money is owed
 * to, how much is owed, why the transfer exists, and whether the request has
 * already been completed.
 * The object itself is intentionally simple. It mainly acts as a small data
 * model so other parts of the program, especially the scenario and payment
 * flows, can keep track of unfinished contact related payments.
 */

package org.example.pathwayver1;

public class TransferRequest {

    private String contactName; // Name of the contact tied to the request
    private double amount; // Amount that still needs to be transferred
    private String reason; // Short explanation for why the transfer exists
    private boolean completed; // Tracks whether the request has already been paid

    /**
     * Creates a new transfer request.
     * New requests begin as incomplete by default and stay that way until
     * another part of the program marks them as finished.
     */
    public TransferRequest(String contactName, double amount, String reason) {
        this.contactName = contactName;
        this.amount = amount;
        this.reason = reason;
        this.completed = false;
    }

    // Returns the contact name attached to this request
    public String getContactName() {
        return contactName;
    }

    // Returns the amount that needs to be transferred
    public double getAmount() {
        return amount;
    }

    // Returns the reason this transfer request was created
    public String getReason() {
        return reason;
    }

    // Returns whether the request has already been completed
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Updates the completion status of the request.
     * This is mainly used after a successful transfer so the request can be
     * removed from the user's pending transfer list.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}