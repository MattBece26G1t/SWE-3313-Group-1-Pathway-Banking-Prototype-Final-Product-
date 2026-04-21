/**
 * Account represents one banking account owned by a user.
 *
 * In this project, the same class is used for multiple account types such as
 * Debit, Savings, and Credit. Because of that, it stores both general balance
 * data and extra credit-specific fields like credit limit, balance owed,
 * minimum payment, and payment due date.
 *
 * This class mainly handles direct account-level actions such as deposits,
 * withdrawals, charges, and payments, while higher-level workflow decisions
 * are usually handled by the BankingManager.
 */

package org.example.pathwayver1;

public class Account {

    // Main stored balance for standard accounts like Debit and Savings
    private double balance;
    // Generated account number shown throughout the UI
    private String accountNumber;
    // Used to identify whether this account is Debit, Savings, or Credit
    private String accountType;

    // Tracks how many times money has been moved out of this account
    // This is especially useful for Savings transfer fee logic
    private int withdrawCount = 0;

    // Prevents the low-balance warning from being spammed repeatedly
    private boolean lowBalanceNotified = false;

    // ===== Credit account fields =====
    private double creditLimit = 0;
    private double balanceOwed = 0;
    private String paymentDueDate = "N/A";
    private double minimumPayment = 0;

    // Tracks whether a late fee has already been applied for the current cycle
    private boolean lateFeeApplied = false;

    // Tracks whether the user met the required minimum payment this cycle
    private boolean minimumPaymentMet = false;

    //Creates a default Debit account with a generated account number.
    public Account(double balance) {
        this.balance = balance;
        this.accountNumber = generateAccountNumber();
        this.accountType = "Debit";
    }

    /**
     * Creates a default Debit account with a provided account number.
     * This is mainly useful when rebuilding saved account data from storage.
     */
    public Account(double balance, String accountNumber) {
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountType = "Debit";
    }

    /**
     * Creates an account with a provided balance, account number, and type.
     * This constructor is useful when loading an existing account from file data.
     */
    public Account(double balance, String accountNumber, String accountType) {
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
    }

    //  Adds money to this account if the amount is valid.
    public void deposit(double amount){
        if (amount <= 0){
            System.out.println("Invalid deposit amount.");
            return;
        }
        balance+= amount;
    }

    /**
     * Removes money from this account if the amount is valid
     * and enough funds are available.
     */
    public void withdraw(double amount){
        if (amount <= 0){
            System.out.println("Invalid withdrawal amount.");
            return;
        }
        if (amount > balance){
            System.out.println("Not enough funds.");
            return;
        }
        balance -= amount;
    }
    /**
     * Returns the account's regular balance.
     * For Credit accounts, this is not the same thing as balance owed.
     */
    public double getBalance(){
        return balance;
    }

    // Returns the full formatted account number.
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Generates a Pathway Banking-style account number.
     * Format example: PB-1234 5678 9012 3456
     */
    private String generateAccountNumber() {
        java.util.Random rand = new java.util.Random();
        String num = "PB-";
        for (int i = 0; i < 16; i++) {
            num += rand.nextInt(10);
            if (i == 3 || i == 7 || i == 11) {
                num += " ";
            }
        }
        return num;
    }

    /**
     * Returns the last four visible characters of the account number.
     * This is mainly used for ComboBox labels and shortened account displays.
     */
    public String getLastFour() {
        return accountNumber.substring(accountNumber.length() - 4);
    }

    // Returns the account type.
    public String getAccountType() {
        return accountType;
    }
    // Updates the account type.
    public void setAccountType(String type) {
        this.accountType = type;
    }

    // Returns how many withdrawals/transfers out of this account have been counted.
    public int getWithdrawCount() {
        return withdrawCount;
    }

    /**
     * Increments the withdrawal counter.
     * This is used mainly for Savings fee progression.
     */
    public void incrementWithdrawCount() {
        withdrawCount++;
    }

    // Returns whether a low balance warning has already been sent for this account.
    public boolean isLowBalanceNotified() {
        return lowBalanceNotified;
    }

    // Updates the low-balance notification flag.
    public void setLowBalanceNotified(boolean notified) {
        this.lowBalanceNotified = notified;
    }

    // === Credit Account Methods ===

    //  Returns the credit limit for this account.
    public double getCreditLimit() {
        return creditLimit;
    }

    // Sets the credit limit for this account.
    public void setCreditLimit(double limit) {
        this.creditLimit = limit;
    }

    // Returns the current balance owed on this credit account.
    public double getBalanceOwed() {
        return balanceOwed;
    }

    // Sets the amount currently owed on this credit account.
    public void setBalanceOwed(double owed) {
        this.balanceOwed = owed;
    }

    //  Returns how much credit is still available.
    public double getAvailableCredit() {
        return creditLimit - balanceOwed;
    }

    // Returns the payment due date string for this credit account.
    public String getPaymentDueDate() {
        return paymentDueDate;
    }

    //  Sets the payment due date string for this credit account.
    public void setPaymentDueDate(String date) {
        this.paymentDueDate = date;
    }

    // Returns the current minimum payment due.
    public double getMinimumPayment() {
        return minimumPayment;
    }

    // Sets the minimum payment due.
    public void setMinimumPayment(double min) {
        this.minimumPayment = min;
    }

    /**
     * Adds a charge to this credit account if the amount is valid
     * and enough available credit remains.
     *
     * Returns true if the charge succeeded, otherwise false.
     */
    public boolean addCharge(double amount) {
        if (amount <= 0) return false;
        if (amount > getAvailableCredit()) return false;
        balanceOwed += amount;

        // Minimum payment is 10% of the owed balance,
        // but never less than $5 unless the total owed is below $5
        minimumPayment = balanceOwed * 0.1;
        if (minimumPayment < 5 && balanceOwed > 0) {
            minimumPayment = Math.min(5, balanceOwed);
        }
        return true;
    }

    /**
     * Applies a payment to this credit account.
     *
     * If the payment meets or exceeds the minimum payment,
     * the minimumPaymentMet flag is updated for the current cycle.
     */
    public void makePayment(double amount) {
        if (amount <= 0) return;
        if (amount >= minimumPayment) {
            minimumPaymentMet = true;
        }
        // Do not allow the payment to exceed what is actually owed
        if (amount > balanceOwed) {
            amount = balanceOwed;
        }
        balanceOwed -= amount;

        // Recalculate the next minimum payment after the balance changes
        minimumPayment = balanceOwed * 0.1;
        if (minimumPayment < 5 && balanceOwed > 0) {
            minimumPayment = Math.min(5, balanceOwed);
        }
    }

    // Returns whether a late fee has already been applied this cycle.
    public boolean isLateFeeApplied() {
        return lateFeeApplied;
    }

    // Updates the late fee applied flag
    public void setLateFeeApplied(boolean applied) {
        this.lateFeeApplied = applied;
    }

    // Returns whether the minimum payment requirement was met this cycle
    public boolean isMinimumPaymentMet() {
        return minimumPaymentMet;
    }

    // Updates whether the minimum payment requirement was met
    public void setMinimumPaymentMet(boolean met) {
        this.minimumPaymentMet = met;
    }

}