/**
 * WalletManager handles wallet related operations beyond the raw wallet balance itself.
 * While the Wallet class only stores the balance and supports basic add/remove
 * operations, this class adds the higher level behavior the rest of the program
 * needs. That includes:
 * recording wallet history,
 * processing deposits and withdrawals with event logging,
 * and automatically applying wallet deposits toward pending debt when necessary.
 * This class acts as the main backend helper for the Wallet feature.
 */

package org.example.pathwayver1;

import java.util.ArrayList;
import java.time.LocalDate;

public class WalletManager {

    private Wallet wallet; // The actual wallet object that stores the balance
    private ArrayList<WalletEvent> walletEvents; // History of wallet actions shown on the Wallet screen

    /**
     * Creates a WalletManager with a new empty wallet.
     * This constructor is useful when a wallet starts at zero and does not need
     * a custom starting balance.
     */
    public WalletManager() {
        this.wallet = new Wallet();
        this.walletEvents = new ArrayList<>();
    }

    /**
     * Creates a WalletManager using an already existing wallet object.
     * This is especially useful when loading saved wallet data, since the wallet
     * may already have a balance that needs to be preserved.
     */
    public WalletManager(Wallet wallet) {
        this.wallet = wallet;
        this.walletEvents = new ArrayList<>();
    }

    //  Returns the wallet object managed by this class
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Returns the current wallet balance.
     * This is just a convenience method so other classes do not have to call
     * getWallet().getBalance() every time.
     */
    public double getBalance() {
        return wallet.getBalance();
    }

    /**
     * Adds money to the wallet and records the action in wallet history.
     * This method is used for normal wallet deposits, such as received funds,
     * transfers, or rewards that should go directly into the wallet.
     */
    public void depositToWallet(double amount, String source) {
        wallet.addFunds(amount);
        recordEvent(new WalletEvent(
                "Received",
                amount,
                source,
                LocalDate.now().toString()
        ));
    }

    /**
     * Removes money from the wallet and records the action if it succeeds.
     * The method returns:
     * - true if the wallet had enough money and the withdrawal succeeded
     * - false if the wallet did not have enough money
     * This method does not throw an error for insufficient funds because that
     * situation is treated as a normal failed transaction, not a system error.
     */
    public boolean withdrawFromWallet(double amount, String destination) {
        if (wallet.removeFunds(amount)) {
            recordEvent(new WalletEvent(
                    "Withdrawn",
                    amount,
                    destination,
                    LocalDate.now().toString()
            ));
            return true;
        }
        return false;
    }

    /**
     * Adds money to the wallet, records the deposit, and then checks whether
     * the user currently has pending debt.
     * If pending debt exists, this method automatically uses as much of the new
     * wallet balance as possible to pay that debt down. That means the full
     * deposited amount may not remain in the wallet after the method finishes.
     * This behavior is used for situations where the program wants incoming
     * funds to immediately reduce unresolved debt before the user can spend
     * the money elsewhere.
     */
    public void depositToWalletWithDebtCheck(double amount, String source, UserAccount user) {
        wallet.addFunds(amount);
        recordEvent(new WalletEvent(
                "Received",
                amount,
                source,
                LocalDate.now().toString()
        ));

        // If the user has pending debt, use wallet funds to pay it down automatically
        if (user.getPendingDebt() > 0) {
            double debt = user.getPendingDebt();
            double payment = Math.min(wallet.getBalance(), debt);

            if (payment > 0) {
                wallet.removeFunds(payment);
                user.addPendingDebt(-payment);
                recordEvent(new WalletEvent(
                        "Debt Payment",
                        payment,
                        "Auto-payment toward pending debt",
                        LocalDate.now().toString()
                ));
            }
        }
    }

    /**
     * Adds one event to the wallet history list
     * The list is capped at 100 entries so it stays manageable and does not
     * grow indefinitely during long sessions or repeated saves
     */
    public void recordEvent(WalletEvent event) {
        walletEvents.add(event);

        if (walletEvents.size() > 100) {
            walletEvents.remove(0);
        }
    }

    // Returns the full wallet event history
    public ArrayList<WalletEvent> getWalletEvents() {
        return walletEvents;
    }
}