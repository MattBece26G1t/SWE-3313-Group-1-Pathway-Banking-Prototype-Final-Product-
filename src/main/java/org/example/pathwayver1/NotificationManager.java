/**
 * NotificationManager handles the creation and storage of notifications for a user.
 *
 * This class works like the backend for the Inbox feature. Its job is to keep
 * track of all notification objects, provide helper methods for read/unread
 * behavior, and generate common banking-related notifications without forcing
 * other classes to build those messages manually every time.
 *
 * In other words, instead of each controller or manager writing notification
 * text from scratch, they can call this class and let it create a consistent
 * message format.
 */

package org.example.pathwayver1;

import java.util.ArrayList;
import java.time.LocalDate;

public class NotificationManager {

    // Stores all notifications currently associated with the user
    private ArrayList<Notification> notifications;

    //  Creates a notification manager with an empty inbox
    public NotificationManager() {
        this.notifications = new ArrayList<>();
    }

    /**
     * Adds a notification to the user's list.
     * The list is capped at 100 entries so it does not continue growing forever
     * during long sessions or repeated saves/loads.
     */
    public void addNotification(Notification notification) {
        notifications.add(notification);
        if (notifications.size() > 100) {
            notifications.remove(0);
        }
    }

    // Returns the full notification list
    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    /**
     * Returns only the notifications that have not been read yet.
     * This is useful when the program needs to show unread only content
     * or calculate indicators such as the red alert badge on the dashboard.
     */
    public ArrayList<Notification> getUnreadNotifications() {
        ArrayList<Notification> unread = new ArrayList<>();
        for (Notification n : notifications) {
            if (!n.isRead()) {
                unread.add(n);
            }
        }
        return unread;
    }

    /**
     * Counts how many unread notifications currently exist.
     * This is mainly used for the dashboard badge and inbox summary behavior.
     */
    public int getUnreadCount() {
        int count = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) {
                count++;
            }
        }
        return count;
    }

    // Marks one specific notification as read
    public void markAsRead(Notification notification) {
        notification.setRead(true);
    }

    /**
     * Marks every stored notification as read.
     * This supports the "Mark All Read" button on the Inbox screen.
     */
    public void markAllAsRead() {
        for (Notification n : notifications) {
            n.setRead(true);
        }
    }

    /**
     * Searches for a notification by its generated ID.
     * Returns null if no match is found.
     */
    public Notification getNotificationByID(String id) {
        for (Notification n : notifications) {
            if (n.getNotificationID().equals(id)) {
                return n;
            }
        }
        return null;
    }

    /**
     * Creates and stores a deposit confirmation notification.
     * This is a convenience method so banking code can report deposits
     * without rebuilding the message text each time.
     */
    public void notifyDeposit(String accountLabel, double amount) {
        addNotification(new Notification(
                "Deposit of $" + String.format("%.2f", amount) + " to " + accountLabel + " was successful.",
                "Transaction",
                "Banking",
                LocalDate.now().toString()
        ));
    }

    // Creates and stores a withdrawal confirmation notification.
    public void notifyWithdrawal(String accountLabel, double amount) {
        addNotification(new Notification(
                "Withdrawal of $" + String.format("%.2f", amount) + " from " + accountLabel + " was successful.",
                "Transaction",
                "Banking",
                LocalDate.now().toString()
        ));
    }

    /**
     * Creates and stores a transfer confirmation notification.
     * The transfer can be between accounts or to another target such as a contact,
     * so the method accepts the source and destination as label strings.
     */
    public void notifyTransfer(String from, String to, double amount) {
        addNotification(new Notification(
                "Transfer of $" + String.format("%.2f", amount) + " from " + from + " to " + to + " was successful.",
                "Transaction",
                "Banking",
                LocalDate.now().toString()
        ));
    }

    /**
     * Creates and stores a low-balance warning.
     * This is used when the debit account drops below the warning threshold.
     */
    public void notifyLowBalance(String accountLabel, double balance) {
        addNotification(new Notification(
                "Low balance warning — " + accountLabel + " dropped below $50.00. Current balance: $" + String.format("%.2f", balance) + ".",
                "Alert",
                "Banking",
                LocalDate.now().toString()
        ));
    }

    // Creates and stores a notification after a new account has been unlocked
    public void notifyAccountUnlock(String type) {
        addNotification(new Notification(
                "Congratulations! You unlocked a new " + type + " Account.",
                "System",
                "Banking",
                LocalDate.now().toString()
        ));
    }

    // Creates and stores a notification after an account has been deleted
    public void notifyAccountDeleted(String accountLabel) {
        addNotification(new Notification(
                accountLabel + " has been deleted.",
                "System",
                "Banking",
                LocalDate.now().toString()
        ));
    }
}