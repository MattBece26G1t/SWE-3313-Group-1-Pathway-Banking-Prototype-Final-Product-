/**
 * BankingManager is the main backend helper for money movement in the app.
 *
 * This class handles the bigger account actions like deposits, withdrawals,
 * payments, transfers, account unlocks, late fees, and a few system driven
 * money events like random contact transfers.
 * It also keeps the transaction history in one place, and sends notifications
 * when something important happens.
 */

package org.example.pathwayver1;

import java.util.ArrayList;
import java.time.LocalDate;

public class BankingManager {

    // Stores the running transaction history for this user
    private ArrayList<TransactionRecord> transactionHistory;
    // Simple built-in contact list used for transfer features
    private ArrayList<String> simulatedContacts;
    // Optional notification manager so banking actions can send alerts/messages
    private NotificationManager notificationManager;

    /**
     * Sets up a fresh banking manager with an empty transaction history
     * and a predefined contact list.
     */
    public BankingManager() {
        this.transactionHistory = new ArrayList<>();

        this.simulatedContacts = new ArrayList<>();
        simulatedContacts.add("Mom");
        simulatedContacts.add("Dad");
        simulatedContacts.add("Sister");
        simulatedContacts.add("Brother");
        simulatedContacts.add("Alex");
        simulatedContacts.add("Jordan");
        simulatedContacts.add("Taylor");
        simulatedContacts.add("Morgan");
        simulatedContacts.add("Casey");
    }

    /**
     * Lets the manager talk to the notification system.
     *
     * This is usually linked in through UserAccount.
     */
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    /**
     * Handles a deposit from the wallet into a selected account.
     *
     * The method checks:
     * - the amount is valid
     * - the wallet can actually cover it
     *
     * If everything works, the account is credited, a transaction is recorded,
     * and a notification can be sent.
     */
    public boolean processDeposit(Account account, double amount, WalletManager walletManager) {
        if (amount <= 0) {
            return false;
        }
        String accountLabel = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();

        // Deposits in this project always come from the wallet first
        if (!walletManager.withdrawFromWallet(amount, "Deposit to " + accountLabel)) {
            return false;
        }
        account.deposit(amount);

        recordTransaction(new TransactionRecord(
                "Deposit",
                accountLabel,
                amount,
                LocalDate.now().toString(),
                "Positive"
        ));

        if (notificationManager != null) {
            notificationManager.notifyDeposit(accountLabel, amount);

            // If the balance has recovered enough, allow future low-balance warnings again
            if (account.getBalance() >= 50.0) {
                account.setLowBalanceNotified(false);
            }
        }

        return true;
    }

    /**
     * Handles a withdrawal from an account back into the wallet.
     *
     * This checks that:
     * - the amount is valid
     * - the selected account has enough money
     *
     * If successful, the wallet gets the withdrawn amount, the action is logged,
     * and low-balance warnings may be triggered for Debit accounts.
     */
    public boolean processWithdrawal(Account account, double amount, WalletManager walletManager) {
        if (amount <= 0) {
            return false;
        }
        if (account.getBalance() < amount) {
            return false;
        }
        account.withdraw(amount);
        walletManager.depositToWallet(amount, "Withdrawal from " + account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour());

        String accountLabel = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
        recordTransaction(new TransactionRecord(
                "Withdrawal",
                accountLabel,
                amount,
                LocalDate.now().toString(),
                "Negative"
        ));

        if (notificationManager != null) {
            notificationManager.notifyWithdrawal(accountLabel, amount);

            // Debit account warnings only fire once until the balance recovers
            if (isLowBalance(account) && !account.isLowBalanceNotified() && account.getAccountType().equalsIgnoreCase("Debit")) {
                notificationManager.notifyLowBalance(accountLabel, account.getBalance());
                account.setLowBalanceNotified(true);
            }
        }

        return true;
    }

    /**
     * Records a payment action in the history.
     * the real payment logic is handled elsewhere, mostly in PaymentController and Account methods.
     */
    public boolean processPayment(Account account, double amount) {
        if (amount <= 0) {
            return false;
        }

        String accountLabel = account.getAccountType().toUpperCase() + " ACC - PB " + account.getLastFour();
        recordTransaction(new TransactionRecord(
                "Payment",
                accountLabel,
                amount,
                LocalDate.now().toString(),
                "Positive"
        ));
        return true;
    }

    /**
     * Transfers money from one account to another account owned by the user.
     * This checks the amount, removes money from the source account,
     * adds it to the destination account, records the move, and may send alerts.
     */
    public boolean processTransfer(Account fromAccount, Account toAccount, double amount) {
        if (amount <= 0) {
            return false;
        }

        if (fromAccount.getBalance() < amount) {
            return false;
        }

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        String fromLabel = fromAccount.getAccountType().toUpperCase() + " ACC - PB " + fromAccount.getLastFour();
        String toLabel = toAccount.getAccountType().toUpperCase() + " ACC - PB " + toAccount.getLastFour();
        recordTransaction(new TransactionRecord(
                "Transfer",
                fromLabel + " to " + toLabel,
                amount,
                LocalDate.now().toString(),
                "Neutral"
        ));

        if (notificationManager != null) {
            notificationManager.notifyTransfer(fromLabel, toLabel, amount);

            if (isLowBalance(fromAccount) && !fromAccount.isLowBalanceNotified() && fromAccount.getAccountType().equalsIgnoreCase("Debit")) {
                notificationManager.notifyLowBalance(fromLabel, fromAccount.getBalance());
                fromAccount.setLowBalanceNotified(true);
            }
        }

        return true;
    }

    /**
     * Transfers money from the user's Debit account to one of the predefined contacts.
     * This is used for regular contact transfers and for some scenario outcomes.
     */
    public boolean transferToContact(Account fromAccount, String contactName, double amount) {
        if (amount <= 0) {
            return false;
        }

        if (fromAccount.getBalance() < amount) {
            return false;
        }

        fromAccount.withdraw(amount);

        String fromLabel = fromAccount.getAccountType().toUpperCase() + " ACC - PB " + fromAccount.getLastFour();
        recordTransaction(new TransactionRecord(
                "Transfer",
                fromLabel + " to " + contactName,
                amount,
                LocalDate.now().toString(),
                "Neutral"
        ));

        if (notificationManager != null) {
            notificationManager.notifyTransfer(fromLabel, contactName, amount);

            if (isLowBalance(fromAccount) && !fromAccount.isLowBalanceNotified() && fromAccount.getAccountType().equalsIgnoreCase("Debit")) {
                notificationManager.notifyLowBalance(fromLabel, fromAccount.getBalance());
                fromAccount.setLowBalanceNotified(true);
            }
        }
        return true;
    }

    /**
     * Adds a transaction to the history list.
     * The list is capped at 100 records so it does not grow forever.
     */
    public void recordTransaction(TransactionRecord record) {
        transactionHistory.add(record);
        if (transactionHistory.size() > 100) {
            transactionHistory.remove(0);
        }
    }

    // Returns the current transaction history.
    public ArrayList<TransactionRecord> getTransactionHistory() {
        return transactionHistory;
    }

    // Returns the built-in contact list
    public ArrayList<String> getSimulatedContacts() {
        return simulatedContacts;
    }

    // Simple helper for checking whether an account is under the low-balance threshold
    public boolean isLowBalance(Account account) {
        return account.getBalance() < 50.0;
    }

    /**
     * Unlocks a new Credit or Savings account for the user.
     * The downpayment always comes out of the user's default Debit account.
     * If the new account is Credit, some default credit info is also set up.
     * If the user had pending scenario debt, that debt may get pushed onto
     * the new Credit account automatically.
     */
    public boolean unlockAccount(UserAccount user, String type, double cost) {
        Account debitAccount = user.getAccounts().get(0);
        if (debitAccount.getBalance() < cost) {
            return false;
        }
        debitAccount.withdraw(cost);

        Account newAccount = new Account(0);
        newAccount.setAccountType(type);

        if (type.equalsIgnoreCase("Credit")) {
            newAccount.setCreditLimit(500);

            java.time.LocalDateTime due = java.time.LocalDateTime.now().plusMinutes(20);
            String formatted = String.format("%s %d, %d:%02d",
                    due.getMonth().toString().substring(0, 3),
                    due.getDayOfMonth(),
                    due.getHour(),
                    due.getMinute());
            newAccount.setPaymentDueDate(formatted);
        }

        user.addAccount(newAccount);

        // If the user already had unresolved debt from scenarios,
        // try moving it onto the newly unlocked Credit account
        if (type.equalsIgnoreCase("Credit") && user.getPendingDebt() > 0) {
            double debt = user.getPendingDebt();
            if (debt <= newAccount.getAvailableCredit()) {
                newAccount.addCharge(debt);
                user.clearPendingDebt();

                recordTransaction(new TransactionRecord(
                        "Pending Debt",
                        type.toUpperCase() + " ACC - PB " + newAccount.getLastFour(),
                        debt,
                        java.time.LocalDate.now().toString(),
                        "Negative"
                ));

                if (notificationManager != null) {
                    notificationManager.addNotification(new Notification(
                            "Outstanding debt of $" + String.format("%.2f", debt) + " from previous scenarios has been applied to your new Credit Account.",
                            "Alert",
                            "Banking",
                            java.time.LocalDate.now().toString()
                    ));
                }
            }
        }

        recordTransaction(new TransactionRecord(
                "Account Unlock",
                type.toUpperCase() + " ACC - PB " + newAccount.getLastFour(),
                cost,
                LocalDate.now().toString(),
                "Neutral"
        ));

        if (notificationManager != null) {
            notificationManager.notifyAccountUnlock(type);
            if (isLowBalance(debitAccount) && !debitAccount.isLowBalanceNotified() && debitAccount.getAccountType().equalsIgnoreCase("Debit")) {
                String debitLabel = "DEBIT ACC - PB " + debitAccount.getLastFour();
                notificationManager.notifyLowBalance(debitLabel, debitAccount.getBalance());
                debitAccount.setLowBalanceNotified(true);
            }
        }

        return true;
    }

    /**
     * Checks for overdue credit payments and overdue subscriptions.
     * This method does a lot of backend maintenance:
     * - applies credit late fees if minimum payments were missed
     * - updates credit due dates for the next cycle
     * - marks subscriptions as unpaid when they pass due
     * - adds subscription late fees if they stay unpaid too long
     */
    public void checkLateFees(UserAccount user) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Check credit accounts
        for (Account account : user.getAccounts()) {
            if (account.getAccountType().equalsIgnoreCase("Credit") && account.getBalanceOwed() > 0) {
                try {
                    String dueStr = account.getPaymentDueDate();
                    if (dueStr.equals("N/A")) continue;

                    String[] parts = dueStr.split("[, ]+");
                    if (parts.length < 3) continue;

                    String monthStr = parts[0];
                    int day = Integer.parseInt(parts[1]);
                    String[] timeParts = parts[2].split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);

                    int month = parseMonth(monthStr);
                    int year = now.getYear();

                    java.time.LocalDateTime dueDate = java.time.LocalDateTime.of(year, month, day, hour, minute);

                    if (now.isAfter(dueDate)) {
                        if (!account.isMinimumPaymentMet() && account.getBalanceOwed() > 0) {
                            // Missed minimum payment, so apply the late fee
                            account.setBalanceOwed(account.getBalanceOwed() + 10);

                            if (notificationManager != null) {
                                notificationManager.addNotification(new Notification(
                                        "Late fee of $10.00 applied to CREDIT ACC - PB " + account.getLastFour() + ". You did not meet the minimum payment. New balance owed: $" + String.format("%.2f", account.getBalanceOwed()) + ".",
                                        "Alert",
                                        "Banking",
                                        java.time.LocalDate.now().toString()
                                ));
                            }

                            recordTransaction(new TransactionRecord(
                                    "Late Fee",
                                    "CREDIT ACC - PB " + account.getLastFour(),
                                    10.0,
                                    java.time.LocalDate.now().toString(),
                                    "Negative"
                            ));
                        }

                        else if (account.isMinimumPaymentMet() && account.getBalanceOwed() > 0) {
                            // Minimum was paid, so no fee this cycle
                            if (notificationManager != null) {
                                notificationManager.addNotification(new Notification(
                                        "Minimum payment met for CREDIT ACC - PB " + account.getLastFour() + ". No late fee applied. Remaining balance: $" + String.format("%.2f", account.getBalanceOwed()) + ".",
                                        "System",
                                        "Banking",
                                        java.time.LocalDate.now().toString()
                                ));
                            }
                        }

                        // Reset for next cycle regardless
                        account.setMinimumPaymentMet(false);

                        // Push the due date forward for the next round
                        java.time.LocalDateTime newDue = now.plusMinutes(20);
                        String formatted = String.format("%s %d, %d:%02d",
                                newDue.getMonth().toString().substring(0, 3),
                                newDue.getDayOfMonth(),
                                newDue.getHour(),
                                newDue.getMinute());
                        account.setPaymentDueDate(formatted);
                    }
                }
                catch (Exception e) { // Quiet fail here so one bad date string does not break the whole cycle check
                }
            }
        }

        // Check subscriptions that are Paid but past due — mark as Unpaid
        for (Asset asset : user.getAssetManager().getOwnedAssets()) {
            if (asset.getItemType().equals("Subscription") && asset.getPaymentStatus().equals("Paid")) {
                try {
                    String dueStr = asset.getNextPaymentDue();
                    if (dueStr.equals("N/A")) continue;

                    String[] parts = dueStr.split("[, ]+");
                    if (parts.length < 3) continue;

                    String monthStr = parts[0];
                    int day = Integer.parseInt(parts[1]);
                    String[] timeParts = parts[2].split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);

                    int month = parseMonth(monthStr);
                    int year = now.getYear();

                    java.time.LocalDateTime dueDate = java.time.LocalDateTime.of(year, month, day, hour, minute);

                    if (now.isAfter(dueDate)) {
                        asset.setPaymentStatus("Unpaid");

                        // Push due date forward to give grace period before late fee
                        java.time.LocalDateTime newDue = now.plusMinutes(20);
                        String formatted = String.format("%s %d, %d:%02d",
                                newDue.getMonth().toString().substring(0, 3),
                                newDue.getDayOfMonth(),
                                newDue.getHour(),
                                newDue.getMinute());
                        asset.setNextPaymentDue(formatted);

                        if (notificationManager != null) {
                            notificationManager.addNotification(new Notification(
                                    "Payment due for " + asset.getName() + ". Amount: $" + String.format("%.2f", asset.getRecurringCost()) + ".",
                                    "Alert",
                                    "Assets",
                                    java.time.LocalDate.now().toString()
                            ));
                        }
                    }
                } catch (Exception e) { // Ignore malformed date data and continue checking the rest
                }
            }
        }

        // Check subscriptions that are Unpaid and past due — apply late fee
        for (Asset asset : user.getAssetManager().getOwnedAssets()) {
            if (asset.getItemType().equals("Subscription") && asset.getPaymentStatus().equals("Unpaid")) {
                try {
                    String dueStr = asset.getNextPaymentDue();
                    if (dueStr.equals("N/A")) continue;

                    String[] parts = dueStr.split("[, ]+");
                    if (parts.length < 3) continue;

                    String monthStr = parts[0];
                    int day = Integer.parseInt(parts[1]);
                    String[] timeParts = parts[2].split(":");
                    int hour = Integer.parseInt(timeParts[0]);
                    int minute = Integer.parseInt(timeParts[1]);

                    int month = parseMonth(monthStr);
                    int year = now.getYear();

                    java.time.LocalDateTime dueDate = java.time.LocalDateTime.of(year, month, day, hour, minute);


                    if (now.isAfter(dueDate)) {
                        asset.setRecurringCost(asset.getRecurringCost() + 5);

                        java.time.LocalDateTime newDue = now.plusMinutes(20);
                        String formatted = String.format("%s %d, %d:%02d",
                                newDue.getMonth().toString().substring(0, 3),
                                newDue.getDayOfMonth(),
                                newDue.getHour(),
                                newDue.getMinute());
                        asset.setNextPaymentDue(formatted);

                        if (notificationManager != null) {
                            notificationManager.addNotification(new Notification(
                                    "Late fee of $5.00 applied to " + asset.getName() + ". New cost: $" + String.format("%.2f", asset.getRecurringCost()) + ".",
                                    "Alert",
                                    "Assets",
                                    java.time.LocalDate.now().toString()
                            ));
                        }

                        recordTransaction(new TransactionRecord(
                                "Late Fee",
                                "Subscription: " + asset.getName(),
                                5.0,
                                java.time.LocalDate.now().toString(),
                                "Negative"
                        ));
                    }
                } catch (Exception e) { // Same idea here, keep the rest of the checks running
                }
            }
        }
    }

    // Small helper for converting short month labels into month numbers.
    private int parseMonth(String monthStr) {
        switch (monthStr.toUpperCase()) {
            case "JAN": return 1;
            case "FEB": return 2;
            case "MAR": return 3;
            case "APR": return 4;
            case "MAY": return 5;
            case "JUN": return 6;
            case "JUL": return 7;
            case "AUG": return 8;
            case "SEP": return 9;
            case "OCT": return 10;
            case "NOV": return 11;
            case "DEC": return 12;
            default: return 1;
        }
    }

    /**
     * Occasionally simulates a random contact sending money to the user.
     *
     * This is part of the app's more dynamic behavior.
     * If the event happens, the money goes into the wallet first, and pending
     * debt can eat into it automatically through WalletManager.
     */
    public void randomContactTransfer(UserAccount user) {
        java.util.Random random = new java.util.Random();
        int chance = random.nextInt(100);

        // 50% chance each time this method runs
        if (chance < 50) {
            String[] contacts = {"Mom", "Dad", "Sister", "Brother", "Alex", "Jordan", "Taylor", "Morgan", "Casey"};
            String contact = contacts[random.nextInt(contacts.length)];
            double amount = 5 + random.nextInt(21); // $5 to $25

            user.getWalletManager().depositToWalletWithDebtCheck(amount, "Transfer from " + contact, user);

            if (notificationManager != null) {
                notificationManager.addNotification(new Notification(
                        contact + " sent you $" + String.format("%.2f", amount) + "!",
                        "Transfer",
                        "Banking",
                        java.time.LocalDate.now().toString()
                ));
            }

            recordTransaction(new TransactionRecord(
                    "Received Transfer",
                    "From " + contact,
                    amount,
                    java.time.LocalDate.now().toString(),
                    "Positive"
            ));
        }
    }
}