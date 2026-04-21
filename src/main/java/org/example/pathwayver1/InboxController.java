/**
 * InboxController manages the Inbox screen of the application.
 *
 * This screen is responsible for showing the user's notifications and letting
 * them interact with those messages in a few different ways. Specifically,
 * the controller allows the user to:
 * view all notifications,
 * filter notifications by read status,
 * organize notifications by date or by source,
 * open a notification to view its full details,
 * and mark all notifications as read.
 *
 * The controller also handles navigation back to the Dashboard and to the
 * Help screen for this section.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class InboxController {

    @FXML private ComboBox<String> readFilter;
    @FXML private ComboBox<String> categorizeFilter;
    @FXML private ScrollPane inboxScrollPane;
    @FXML private VBox inboxList;

    private UserAccount currentUser; // Current logged-in user
    private NotificationManager notificationManager; // Shortcut to the user's notification manager

    /**
     * Initializes the filter controls when the screen is first loaded.
     * This method sets up the available filter options, chooses default values,
     * and connects both dropdowns to reload the notification list whenever the
     * user changes a filter.
     */
    @FXML
    private void initialize() {
        readFilter.getItems().addAll("All Messages", "Unread", "Read");
        readFilter.setValue("All Messages");

        categorizeFilter.getItems().addAll("By Date", "By Source");
        categorizeFilter.setValue("By Date");

        readFilter.setOnAction(e -> loadNotifications());
        categorizeFilter.setOnAction(e -> loadNotifications());
    }

    /**
     * Loads the user-specific data needed for the Inbox screen.
     * Once the current user and notification manager are stored, the screen
     * immediately builds the visible notification list.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.notificationManager = user.getNotificationManager();
        loadNotifications();
    }

    /**
     * Rebuilds the notification list based on the active filter settings.
     *
     * This method clears the current list and then repopulates it using one of
     * two display styles:
     * 1. By Date, where messages are simply shown newest first
     * 2. By Source, where messages are grouped under source headers
     *
     * It also applies the read status filter before displaying each entry.
     */
    private void loadNotifications() {
        inboxList.getChildren().clear();

        String readFilterValue = readFilter.getValue();
        String categorizeValue = categorizeFilter.getValue();
        ArrayList<Notification> notifications = notificationManager.getNotifications();

        if (categorizeValue.equals("By Source")) {
            // Build a simple list of unique sources first
            ArrayList<String> sources = new ArrayList<>();
            for (Notification notif : notifications) {
                if (!sources.contains(notif.getSourceSubsystem())) {
                    sources.add(notif.getSourceSubsystem());
                }
            }

            // For each source, add a header and then show matching notifications under it
            for (String source : sources) {
                Label sourceHeader = new Label("— " + source + " —");
                sourceHeader.setStyle("-fx-text-fill: #c49063; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Myanmar Text'; -fx-padding: 10 0 5 0;");
                inboxList.getChildren().add(sourceHeader);

                for (int i = notifications.size() - 1; i >= 0; i--) {
                    Notification notif = notifications.get(i);

                    if (!notif.getSourceSubsystem().equals(source)) continue;
                    if (readFilterValue.equals("Unread") && notif.isRead()) continue;
                    if (readFilterValue.equals("Read") && !notif.isRead()) continue;

                    HBox entry = createNotificationEntry(notif);
                    inboxList.getChildren().add(entry);
                }
            }
        }

        else {
            // Default layout: newest notifications first
            for (int i = notifications.size() - 1; i >= 0; i--) {
                Notification notif = notifications.get(i);

                if (readFilterValue.equals("Unread") && notif.isRead()) continue;
                if (readFilterValue.equals("Read") && !notif.isRead()) continue;

                HBox entry = createNotificationEntry(notif);
                inboxList.getChildren().add(entry);
            }
        }

        // Show a fallback message if nothing matched the current filters
        if (inboxList.getChildren().isEmpty()) {
            Label empty = new Label("No messages to display.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");
            inboxList.getChildren().add(empty);
        }
    }

    /**
     * Creates one visual row for a notification in the inbox list.
     *
     * Each row includes:
     * - a background image container
     * - the notification message text
     * - a red dot indicator if the message is still unread
     *
     * Clicking the row marks the notification as read, opens the full detail
     * popup, and then refreshes the inbox list so the red indicator disappears.
     */
    private HBox createNotificationEntry(Notification notif) {
        HBox entry = new HBox();
        entry.setAlignment(Pos.CENTER_LEFT);
        entry.setPrefHeight(50);
        entry.setPrefWidth(650);
        entry.setStyle("-fx-cursor: hand;");

        // Stack the PNG background with overlays
        javafx.scene.layout.StackPane stack = new javafx.scene.layout.StackPane();
        stack.setPrefWidth(650);
        stack.setPrefHeight(50);

        // PNG container background
        ImageView container = new ImageView(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("images/inboxcontainer.drawio.png")
                )
        );
        container.setFitWidth(650);
        container.setFitHeight(50);
        container.setPreserveRatio(false);

        // Overlay HBox for message text and red dot
        HBox overlay = new HBox(10);
        overlay.setAlignment(Pos.CENTER_LEFT);
        overlay.setPadding(new javafx.geometry.Insets(0, 60, 0, 15));

        Label textLabel = new Label(notif.getMessageText());
        textLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px; -fx-font-family: 'Myanmar Text';");
        textLabel.setPrefWidth(520);
        textLabel.setMaxWidth(520);

        Circle readIndicator = new Circle(6);
        if (!notif.isRead()) {
            readIndicator.setFill(Color.RED);
        }
        else {
            readIndicator.setFill(Color.TRANSPARENT);
        }

        overlay.getChildren().addAll(textLabel, readIndicator);

        stack.getChildren().addAll(container, overlay);
        entry.getChildren().add(stack);

        entry.setOnMouseClicked(e -> {
            notif.setRead(true);
            showNotificationDetail(notif);
            loadNotifications();
        });

        return entry;
    }

    /**
     * Opens a popup showing the full details of one notification.
     * This popup includes the notification type, source, date, and full message
     * text. A scroll pane is used so longer messages can still be displayed
     * cleanly inside the dialog.
     */
    private void showNotificationDetail(Notification notif) {
        Alert alert = new Alert(Alert.AlertType.NONE, "", new ButtonType("Okay"));
        alert.setTitle("Notification");
        alert.setHeaderText(notif.getNotificationType());

        alert.setGraphic(new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("images/inboxopen.drawio.png"),
                        52, 52, true, true
                )
        ));

        Label detailLabel = new Label(
                "Type: " + notif.getNotificationType() + "\n" +
                        "Source: " + notif.getSourceSubsystem() + "\n" +
                        "Date: " + notif.getDate() + "\n\n" +
                        notif.getMessageText()
        );
        detailLabel.setWrapText(true);
        detailLabel.setPrefWidth(400);
        detailLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(detailLabel);
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(scrollPane);
        dialogPane.setStyle(
                "-fx-background-color: #2b2b3d;" +
                        "-fx-font-family: 'Cascadia Mono';"
        );
        dialogPane.lookup(".header-panel").setStyle(
                "-fx-background-color: #D66242;"
        );

        alert.showAndWait();
    }

    /**
     * Marks every notification as read, then refreshes the screen so the
     * unread indicators disappear immediately.
     */
    @FXML
    private void handleMarkAllRead() {
        notificationManager.markAllAsRead();
        loadNotifications();
    }

    // Returns the user to the Dashboard screen.
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("DashboardView.fxml")
            );
            Stage stage = (Stage) inboxScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            DashboardController controller = loader.getController();
            controller.loadUserData(currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the Help screen and labels this request as coming from the Inbox
     * section so the correct help content is shown.
     */
    @FXML
    private void handleHelp() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("HelpView.fxml")
            );
            Stage stage = (Stage) inboxScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            HelpController controller = loader.getController();
            controller.loadUserData(currentUser);
            controller.setCameFrom("inbox");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}