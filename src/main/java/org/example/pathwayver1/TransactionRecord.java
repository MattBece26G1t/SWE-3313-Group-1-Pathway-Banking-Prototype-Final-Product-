/**
 * TransactionRecord represents one saved financial activity in the program.
 *
 * Each record stores the basic information needed to describe a completed
 * transaction, such as what type of action happened, which account or target
 * it was associated with, how much money was involved, the date, and whether
 * the result should be treated as positive, negative, or neutral in the UI.
 *
 * This class is intentionally simple. It mainly acts as a data container so
 * other parts of the program, especially BankingManager and the activity/history
 * screens, can store and display transaction information consistently.
 */

package org.example.pathwayver1;

public class TransactionRecord {

    // General category of transaction, such as Deposit, Withdrawal, Transfer, or Payment
    private String transactionType;
    // Dollar amount tied to the transaction
    private double amount;
    // Account label or other related target tied to the transaction
    private String associatedAccount;
    // Date the transaction was recorded
    private String date;
    // Used by the UI to classify the result as Positive, Negative, or Neutral
    private String resultStatus;

    /**
     * Creates one transaction record with all of its saved values.
     * This constructor is used whenever the program wants to log a completed
     * financial action into transaction history.
     */
    public TransactionRecord(String transactionType, String associatedAccount, double amount,
                             String date, String resultStatus) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.associatedAccount = associatedAccount;
        this.date = date;
        this.resultStatus = resultStatus;
    }

    // Returns the transaction type
    public String getTransactionType() {
        return transactionType;
    }

    // Returns the amount involved in the transaction
    public double getAmount() {
        return amount;
    }

    // Returns the account label or related target associated with the transaction
    public String getAssociatedAccount() {
        return associatedAccount;
    }

    // Returns the saved transaction date
    public String getDate() {
        return date;
    }

    /**
     * Returns the result classification used by the UI.
     * This is what the Activity Tracker uses for its color coding.
     */

    public String getResultStatus() {
        return resultStatus;
    }
}