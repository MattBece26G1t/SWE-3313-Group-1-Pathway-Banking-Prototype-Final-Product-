/**
 * Wallet represents the user's separate cash balance in the application.
 * It stores one balance value
 * and provides the basic operations needed to add or remove funds from that
 * balance.
 * In the overall project design, the Wallet is kept separate from the user's
 * bank accounts. This class only handles the raw balance
 * change itself, while higher level features such as wallet history,
 * pending debt checks, and scenario related rewards are handled by
 * WalletManager.
 */

package org.example.pathwayver1;

public class Wallet {

    private double balance; // Current amount of money stored in the wallet

    // Creates an empty wallet with a starting balance of 0
    public Wallet() {
        this.balance = 0.0;
    }

    /**
     * Creates a wallet with a specified starting balance.
     * This constructor restores saved wallet data.
     */
    public Wallet(double balance) {
        this.balance = balance;
    }

    // Returns the current wallet balance
    public double getBalance() {
        return balance;
    }

    /**
     * Adds funds to the wallet.
     * The amount must be greater than zero. If not, the method throws an
     * exception so the calling code can handle the invalid input properly.
     */
    public void addFunds(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        balance += amount;
    }

    /**
     * Removes funds from the wallet if enough money is available.
     * The method returns:
     * - true if the withdrawal succeeds
     * - false if the wallet does not contain enough money
     * An exception is still thrown for invalid amounts such as zero or negative
     * values, since those are input errors rather than balance related failures.
     */
    public boolean removeFunds(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }
}