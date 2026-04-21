/**
 * Notification represents one message shown in the application's inbox system.
 *
 * This class is used to store the information needed for a single notification,
 * including its message text, type, source section, date, and whether the
 * message has already been read.
 *
 * The object itself is intentionally simple. It mainly works as a data holder
 * so NotificationManager and InboxController can create, store, display, and
 * update notifications consistently throughout the program.
 */

package org.example.pathwayver1;

public class Notification {

    private String notificationID;  // Unique ID for the notification
    private String messageText; // Main text the user sees in the inbox and detail popup
    private String notificationType; // General category of the notification, such as Transaction, Alert, or System
    private String sourceSubsystem; // Keeps track of which part of the program created the notification
    private String date; // Date the notification was created
    private boolean read; // Marks whether the user has already opened/read this message

    // Simple counter used to generate notification IDs
    private static int idCounter = 0;

    /**
     * Creates a new notification with its core information.
     * A unique ID is generated automatically, and new notifications begin
     * as unread by default.
     */
    public Notification(String messageText, String notificationType, String sourceSubsystem, String date) {
        this.notificationID = "NOTIF-" + (++idCounter);
        this.messageText = messageText;
        this.notificationType = notificationType;
        this.sourceSubsystem = sourceSubsystem;
        this.date = date;
        this.read = false;
    }

    // Returns the notification's generated ID
    public String getNotificationID() {
        return notificationID;
    }

    // Returns the main message text
    public String getMessageText() {
        return messageText;
    }

    // Returns the general type/category of the notification
    public String getNotificationType() {
        return notificationType;
    }

    // Returns the subsystem that created the notification
    public String getSourceSubsystem() {
        return sourceSubsystem;
    }

    // Returns the date attached to the notification
    public String getDate() {
        return date;
    }

    // Returns whether the notification has been read
    public boolean isRead() {
        return read;
    }

    /**
     * Updates the read status.
     * This is mainly used by the inbox screen when the user opens a message
     * or chooses to mark everything as read.
     */
    public void setRead(boolean read) {
        this.read = read;
    }
}