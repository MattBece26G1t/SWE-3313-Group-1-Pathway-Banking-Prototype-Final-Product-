/*
 * DataManager handles saving and loading basically all user data for the app.
 *
 * Everything here writes to simple text files inside pathway_data.
 * So instead of using a real database, the project saves each part of the user
 * into its own file, then rebuilds the user from those files later.
 *
 * Main idea:
 * - save user info
 * - save accounts
 * - save wallet
 * - save transactions
 * - save notifications
 * - save assets
 * - save scenarios
 * - save wallet history
 *
 * Then on startup, load it all back in.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class DataManager {

    private String userAccountFile = "pathway_data/users.txt";
    private String accountFile = "pathway_data/accounts.txt";
    private String walletFile = "pathway_data/wallet.txt";
    private String transactionFile = "pathway_data/transactions.txt";
    private String notificationFile = "pathway_data/notifications.txt";
    private String assetFile = "pathway_data/assets.txt";
    private String scenarioFile = "pathway_data/scenarios.txt";
    private String walletEventFile = "pathway_data/wallet_events.txt";

    // Constructor makes sure every data file exists before anything tries to use it.
    public DataManager() {
        FileHandler fh = new FileHandler(userAccountFile);

        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(accountFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(walletFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(transactionFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(notificationFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(assetFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(scenarioFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }

        fh = new FileHandler(walletEventFile);
        if (!fh.fileExists()) {
            fh.createFile();
        }
    }

    // ===== SAVE METHODS =====

    /*
     * Saves the main profile/login info for one user.
     * If the user already exists in the file, replace that line.
     * If not, add a new one.
     */
    public void saveUserAccount(UserAccount user) {
        FileHandler handler = new FileHandler(userAccountFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();
        boolean found = false;

        String line = user.getUserID() + "|" +
                user.getPassword() + "|" +
                user.getFirstName() + "|" +
                user.getLastName() + "|" +
                user.getEmail() + "|" +
                user.getPhoneNumber() + "|" +
                user.getPhoneNumberRaw() + "|" +
                user.getStreet() + "|" +
                user.getCity() + "|" +
                user.getState() + "|" +
                user.getZipCode() + "|" +
                user.getCountry() + "|" +
                user.getBMonth() + "|" +
                user.getBDay() + "|" +
                user.getBYear() + "|" +
                user.getPendingDebt() + "|" +
                user.isSavingsTransferInfoShown() + "|" +
                user.isMandatoryScenarioEnabled();

        for (String l : lines) {
            if (l.startsWith(user.getUserID() + "|")) {
                updated.add(line);
                found = true;
            }
            else {
                updated.add(l);
            }
        }

        if (!found) {
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    /*
     * Saves all accounts that belong to one user.
     * Old account lines for that user get removed first,
     * then the current account list gets written back fresh.
     */
    public void saveAccounts(UserAccount user) {
        FileHandler handler = new FileHandler(accountFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        // Remove the user's old saved accounts first
        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        // Write the current account list back out
        for (Account account : user.getAccounts()) {
            String line = user.getUserID() + "|" +
                    account.getAccountNumber() + "|" +
                    account.getAccountType() + "|" +
                    account.getBalance() + "|" +
                    account.getWithdrawCount() + "|" +
                    account.getCreditLimit() + "|" +
                    account.getBalanceOwed() + "|" +
                    account.getPaymentDueDate() + "|" +
                    account.getMinimumPayment() + "|" +
                    account.isMinimumPaymentMet() + "|" +
                    account.isLowBalanceNotified();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    // Saves the user's wallet balance.
    public void saveWallet(UserAccount user) {
        FileHandler handler = new FileHandler(walletFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        String line = user.getUserID() + "|" + user.getWalletManager().getBalance();
        updated.add(line);

        handler.writeLines(updated);
    }

    // Saves the user's transaction history.
    public void saveTransactions(UserAccount user) {
        FileHandler handler = new FileHandler(transactionFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        for (TransactionRecord record : user.getBankingManager().getTransactionHistory()) {
            String line = user.getUserID() + "|" +
                    record.getTransactionType() + "|" +
                    record.getAssociatedAccount() + "|" +
                    record.getAmount() + "|" +
                    record.getDate() + "|" +
                    record.getResultStatus();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    /*
     * Saves every notification the user has.
     * the message text replaces "|" with "~".
     * that is just to avoid breaking the file format since "|" is the separator.
     */
    public void saveNotifications(UserAccount user) {
        FileHandler handler = new FileHandler(notificationFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        for (Notification notif : user.getNotificationManager().getNotifications()) {
            String line = user.getUserID() + "|" +
                    notif.getMessageText().replace("|", "~") + "|" +
                    notif.getNotificationType() + "|" +
                    notif.getSourceSubsystem() + "|" +
                    notif.getDate() + "|" +
                    notif.isRead();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    // Saves all owned assets for the user
    public void saveAssets(UserAccount user) {
        FileHandler handler = new FileHandler(assetFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        for (Asset asset : user.getAssetManager().getOwnedAssets()) {
            String line = user.getUserID() + "|" +
                    asset.getName() + "|" +
                    asset.getItemType() + "|" +
                    asset.getPurchaseValue() + "|" +
                    asset.getResaleValue() + "|" +
                    asset.getRecurringCost() + "|" +
                    asset.getBaseCost() + "|" +
                    asset.getNextPaymentDue() + "|" +
                    asset.getPaymentStatus() + "|" +
                    asset.getIconFileName();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    // Saves scenario completion status for this user
    public void saveScenarios(UserAccount user) {
        FileHandler handler = new FileHandler(scenarioFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        for (Scenario scenario : user.getScenarioManager().getAllScenarios()) {
            String line = user.getUserID() + "|" +
                    scenario.getScenarioID() + "|" +
                    scenario.getCompletionStatus();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    /*
     * Saves wallet event history.
     * Same function as notifications here. If the event source contains "|",
     * replace it first so the text file does not break.
     */
    public void saveWalletEvents(UserAccount user) {
        FileHandler handler = new FileHandler(walletEventFile);
        ArrayList<String> lines = handler.readLines();
        ArrayList<String> updated = new ArrayList<>();

        for (String l : lines) {
            if (!l.startsWith(user.getUserID() + "|")) {
                updated.add(l);
            }
        }

        for (WalletEvent event : user.getWalletManager().getWalletEvents()) {
            String line = user.getUserID() + "|" +
                    event.getEventType() + "|" +
                    event.getAmount() + "|" +
                    event.getSource().replace("|", "~") + "|" +
                    event.getDate();
            updated.add(line);
        }

        handler.writeLines(updated);
    }

    // Convenience method that saves everything for one user at once.
    public void saveAll(UserAccount user) {
        saveUserAccount(user);
        saveAccounts(user);
        saveWallet(user);
        saveTransactions(user);
        saveNotifications(user);
        saveAssets(user);
        saveScenarios(user);
        saveWalletEvents(user);
    }

    // ===== LOAD METHODS =====

    /*
     * Loads every user from the users file.
     * After the basic user is rebuilt, the rest of that user's data
     * gets loaded from the other files.
     */
    public ArrayList<UserAccount> loadAllUsers() {
        ArrayList<UserAccount> users = new ArrayList<>();
        FileHandler handler = new FileHandler(userAccountFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 15) {
                    UserAccount user = new UserAccount(
                            parts[2], parts[3],
                            parts[4],
                            parts[5],
                            parts[7], parts[8], parts[9],
                            parts[10], parts[11],
                            Integer.parseInt(parts[12]),
                            Integer.parseInt(parts[13]),
                            Integer.parseInt(parts[14]),
                            parts[0], parts[1]
                    );

                    // Restore the exact phone formatting the user originally typed
                    user.setPhoneNumberRaw(parts[6]);

                    // Newer fields are only loaded if they exist
                    if (parts.length >= 16) {
                        user.addPendingDebt(Double.parseDouble(parts[15]));
                    }
                    if (parts.length >= 17) {
                        user.setSavingsTransferInfoShown(Boolean.parseBoolean(parts[16]));
                    }
                    if (parts.length >= 18) {
                        user.setMandatoryScenarioEnabled(Boolean.parseBoolean(parts[17]));
                    }

                    // Rebuild everything else tied to this user
                    loadAccounts(user);
                    loadWallet(user);
                    loadTransactions(user);
                    loadNotifications(user);
                    loadAssets(user);
                    loadScenarios(user);
                    loadWalletEvents(user);

                    users.add(user);
                }
            }

            catch (Exception e) {
                System.out.println("Skipping corrupt user entry: " + e.getMessage());
            }
        }
        return users;
    }

    // Loads all accounts that belong to the given user.
    public void loadAccounts(UserAccount user) {
        FileHandler handler = new FileHandler(accountFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 11 && parts[0].equals(user.getUserID())) {
                    Account account = new Account(
                            Double.parseDouble(parts[3]),
                            parts[1],
                            parts[2]
                    );

                    // Rebuild the withdrawal counter the simple way
                    int withdrawCount = Integer.parseInt(parts[4]);
                    for (int i = 0; i < withdrawCount; i++) {
                        account.incrementWithdrawCount();
                    }

                    // Restore credit related fields too
                    account.setCreditLimit(Double.parseDouble(parts[5]));
                    account.setBalanceOwed(Double.parseDouble(parts[6]));
                    account.setPaymentDueDate(parts[7]);
                    account.setMinimumPayment(Double.parseDouble(parts[8]));
                    account.setMinimumPaymentMet(Boolean.parseBoolean(parts[9]));
                    account.setLowBalanceNotified(Boolean.parseBoolean(parts[10]));

                    user.addAccount(account);
                }
            }
            catch (Exception e) {
                System.out.println("Skipping corrupt account entry: " + e.getMessage());
            }
        }
    }

    // Loads the user's wallet balance
    private void loadWallet(UserAccount user) {
        FileHandler handler = new FileHandler(walletFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(user.getUserID())) {
                    double balance = Double.parseDouble(parts[1]);
                    user.setWalletManager(new WalletManager(new Wallet(balance)));
                }
            }

            catch (Exception e) {
                System.out.println("Skipping corrupt wallet entry: " + e.getMessage());
            }
        }
    }

    // Loads the user's transaction history
    private void loadTransactions(UserAccount user) {
        FileHandler handler = new FileHandler(transactionFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(user.getUserID())) {
                    TransactionRecord record = new TransactionRecord(
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            parts[4],
                            parts[5]
                    );
                    user.getBankingManager().recordTransaction(record);
                }
            }

            catch (Exception e) {
                System.out.println("Skipping corrupt transaction entry: " + e.getMessage());
            }
        }
    }

    // Loads notifications for the given user
    private void loadNotifications(UserAccount user) {
        FileHandler handler = new FileHandler(notificationFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(user.getUserID())) {
                    Notification notif = new Notification(
                            parts[1].replace("~", "|"),
                            parts[2],
                            parts[3],
                            parts[4]
                    );
                    if (Boolean.parseBoolean(parts[5])) {
                        notif.setRead(true);
                    }
                    user.getNotificationManager().addNotification(notif);
                }
            }

            catch (Exception e) {
                System.out.println("Skipping corrupt notification entry: " + e.getMessage());
            }
        }
    }

    // Loads assets for the given user
    private void loadAssets(UserAccount user) {
        FileHandler handler = new FileHandler(assetFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 10 && parts[0].equals(user.getUserID())) {
                    Asset asset = new Asset(
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[4]),
                            Double.parseDouble(parts[5]),
                            parts[9]
                    );
                    asset.setNextPaymentDue(parts[7]);
                    asset.setPaymentStatus(parts[8]);
                    user.getAssetManager().addAsset(asset);
                }
            } catch (Exception e) {
                System.out.println("Skipping corrupt asset entry: " + e.getMessage());
            }
        }
    }

    /*
     * Loads saved scenario completion states.
     * The actual scenarios already exist in ScenarioManager.
     * This just updates their completion status.
     */
    private void loadScenarios(UserAccount user) {
        FileHandler handler = new FileHandler(scenarioFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(user.getUserID())) {
                    Scenario scenario = user.getScenarioManager().getScenarioByID(parts[1]);
                    if (scenario != null) {
                        scenario.setCompletionStatus(parts[2]);
                    }
                }
            } catch (Exception e) {
                System.out.println("Skipping corrupt scenario entry: " + e.getMessage());
            }
        }
    }

    // Loads wallet history entries for the user.
    private void loadWalletEvents(UserAccount user) {
        FileHandler handler = new FileHandler(walletEventFile);
        ArrayList<String> lines = handler.readLines();

        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");

                if (parts.length >= 5 && parts[0].equals(user.getUserID())) {
                    WalletEvent event = new WalletEvent(
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3].replace("~", "|"),
                            parts[4]
                    );
                    user.getWalletManager().recordEvent(event);
                }
            }

            catch (Exception e) {
                System.out.println("Skipping corrupt wallet event entry: " + e.getMessage());
            }
        }
    }
}