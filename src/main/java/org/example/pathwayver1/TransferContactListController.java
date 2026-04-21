/**
 * TransferContactListController manages the contact selection screen used before
 * sending money to another person.
 * This controller has a fairly focused role in the transfer flow. It builds the
 * list of available contacts, displays each contact as a selectable button, and
 * sends the user to the confirmation screen after a contact is chosen.
 * In other words, this screen does not actually transfer money itself. Its main
 * purpose is to act as the middle step between opening the contact transfer
 * feature and entering the final transfer amount on the confirmation page.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TransferContactListController {

    @FXML private ScrollPane contactScrollPane;
    @FXML private VBox contactList;

    private UserAccount currentUser; // Current logged in user

    /**
     * Initializes the screen.
     * No extra startup work is needed here because the actual contact list is
     * built after loadUserData() is called.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Stores the current user and then loads the visible contact list.
     * The user reference is needed so the next screen in the flow can still
     * access the correct account data after navigation.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        loadContacts();
    }

    /**
     * Builds the list of available transfer contacts.
     * Each contact is displayed as its own button. When the user clicks one of
     * those buttons, the controller opens the confirmation screen and passes
     * along both the current user and the selected contact name.
     * The contact list is currently hardcoded,
     * since the app uses a predefined set of simulated contacts.
     */
    private void loadContacts() {
        contactList.getChildren().clear();

        String[] contacts = {"Mom", "Dad", "Sister", "Brother", "Alex", "Jordan", "Taylor", "Morgan", "Casey"};

        for (String contact : contacts) {
            Button contactButton = new Button(contact);
            contactButton.setPrefWidth(450);
            contactButton.setPrefHeight(40);
            contactButton.setStyle("-fx-font-size: 14px; -fx-font-family: 'Myanmar Text';");

            contactButton.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("TransferContactConfirmView.fxml")
                    );
                    Stage stage = (Stage) contactScrollPane.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));

                    TransferContactConfirmController controller = loader.getController();
                    controller.loadUserData(currentUser);
                    controller.setSelectedContact(contact);
                }

                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            contactList.getChildren().add(contactButton);
        }
    }

    // Closes the contact transfer popup without selecting a contact
    @FXML
    private void handleBack() {
        Stage stage = (Stage) contactScrollPane.getScene().getWindow();
        stage.close();
    }

    // Shows a short help popup explaining how the contact transfer flow works
    @FXML
    private void handleHelp() {
        MainApp.showStyledPopup(
                "generalerror.drawio.png", "#367A46",
                "Help", "Transfer - Contact Help",
                "Send money to your contacts.\n\n" +
                        "• Select a contact from the list\n" +
                        "• You'll be taken to a confirmation screen\n" +
                        "• Transfers always come from your Debit account\n" +
                        "• The money leaves your account immediately",
                new ButtonType("Okay")
        );
    }
}