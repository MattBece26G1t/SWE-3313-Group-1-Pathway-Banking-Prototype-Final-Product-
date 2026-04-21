/*
 * This controller runs the Help screen for the app.
 * The whole point of this page is to show section specific help depending on
 * where the user came from. So if they opened Help from Dashboard, they should
 * see dashboard help. If they opened it from Deposit/Withdraw, they should see
 * deposit/withdraw help, and so on.
 * This class mainly handles:
 * 1. figuring out which section opened the Help page
 * 2. switching between FAQ help and Interface Help
 * 3. sending the user back to the correct previous screen
 *
 * The actual help text itself is not written here. That text is pulled from
 * HelpManager using the section name and help category.
 */

package org.example.pathwayver1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelpController {

    @FXML private Label sectionTitleLabel;
    @FXML private ImageView faqNormal;
    @FXML private ImageView faqSelected;
    @FXML private ImageView interfaceHelpNormal;
    @FXML private ImageView interfaceHelpSelected;
    @FXML private ScrollPane helpScrollPane;
    @FXML private VBox helpContentList;

    private UserAccount currentUser; // Current logged-in user
    private String cameFrom; // Stores which page sent the user here
    private HelpManager helpManager; // Used to pull the actual help text for the current section

    /*
     * Runs when the FXML first loads.
     * Nothing special has to happen here yet because the page still needs
     * loadUserData() and setCameFrom() before it knows what content to show.
     */
    @FXML
    private void initialize() {
    }

    /*
     * Saves the current user and grabs that user's HelpManager.
     * Without this, the controller would not be able to look up help content.
     */
    public void loadUserData(UserAccount user) {
        this.currentUser = user;
        this.helpManager = user.getHelpManager();
    }

    /*
     * This method is what gives the Help page context.
     * It stores where the user came from, changes the title at the top of the
     * page, then automatically loads the Interface Help tab by default.
     */
    public void setCameFrom(String source) {
        this.cameFrom = source;

        switch (source) {
            case "dashboard": sectionTitleLabel.setText("DASHBOARD HELP"); break;
            case "accountBalance": sectionTitleLabel.setText("ACCOUNT BALANCE HELP"); break;
            case "depositWithdraw": sectionTitleLabel.setText("DEPOSIT/WITHDRAW HELP"); break;
            case "activityTracker": sectionTitleLabel.setText("ACTIVITY TRACKER HELP"); break;
            case "scenario": sectionTitleLabel.setText("SCENARIO HELP"); break;
            case "wallet": sectionTitleLabel.setText("WALLET HELP"); break;
            case "assets": sectionTitleLabel.setText("ASSETS HELP"); break;
            case "inbox": sectionTitleLabel.setText("INBOX HELP"); break;
            case "settings": sectionTitleLabel.setText("SETTINGS HELP"); break;
            case "debitDetail": sectionTitleLabel.setText("DEBIT ACCOUNT HELP"); break;
            case "savingsDetail": sectionTitleLabel.setText("SAVINGS ACCOUNT HELP"); break;
            case "creditDetail": sectionTitleLabel.setText("CREDIT ACCOUNT HELP"); break;
            case "scenarioActivity": sectionTitleLabel.setText("SCENARIO ACTIVITY HELP"); break;
            default: sectionTitleLabel.setText("HELP"); break;
        }
        handleInterfaceHelp(); // This page opens on Interface Help first
    }

    /*
     * Switches the page to FAQ mode.
     * Besides changing the text, this also swaps the tab images so the user can
     * clearly see that FAQ is the currently selected category.
     */
    @FXML
    private void handleFAQ() {
        faqNormal.setVisible(false);
        faqSelected.setVisible(true);
        interfaceHelpNormal.setVisible(true);
        interfaceHelpSelected.setVisible(false);

        loadContent("faq");
    }

    /*
     * Switches the page to Interface Help mode.
     * Same idea as handleFAQ(), just for the other category.
     */
    @FXML
    private void handleInterfaceHelp() {
        faqNormal.setVisible(true);
        faqSelected.setVisible(false);
        interfaceHelpNormal.setVisible(false);
        interfaceHelpSelected.setVisible(true);

        loadContent("interface");
    }

    /*
     * Pulls the actual help text from HelpManager and places it on the screen.
     * cameFrom tells the manager which section to look up.
     * category tells it whether to return FAQ text or Interface Help text.
     */
    private void loadContent(String category) {
        helpContentList.getChildren().clear();

        String content = helpManager.getContent(cameFrom, category);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(580);
        contentLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-family: 'Myanmar Text'; -fx-padding: 10;");

        helpContentList.getChildren().add(contentLabel);
    }

    /*
     * Sends the user back to the page they came from.
     * First this picks the correct FXML file based on cameFrom.
     * Then it loads that page and passes currentUser back into the controller
     * so the page can rebuild itself with the right data.
     */

    @FXML
    private void handleBack() {
        try {
            String fxml;
            switch (cameFrom) {
                case "dashboard": fxml = "DashboardView.fxml"; break;
                case "accountBalance": fxml = "AccountBalanceView.fxml"; break;
                case "depositWithdraw": fxml = "DepositWithdrawView.fxml"; break;
                case "activityTracker": fxml = "ActivityTrackerView.fxml"; break;
                case "scenario": fxml = "ScenarioView.fxml"; break;
                case "wallet": fxml = "WalletView.fxml"; break;
                case "assets": fxml = "AssetsView.fxml"; break;
                case "inbox": fxml = "InboxView.fxml"; break;
                case "settings": fxml = "SettingsView.fxml"; break;
                case "debitDetail": fxml = "AccountBalanceView.fxml"; break;
                case "savingsDetail": fxml = "AccountBalanceView.fxml"; break;
                case "creditDetail": fxml = "AccountBalanceView.fxml"; break;
                case "scenarioActivity": fxml = "ScenarioView.fxml"; break;
                default: fxml = "DashboardView.fxml"; break;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml)
            );
            Stage stage = (Stage) helpScrollPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            Object controller = loader.getController();

            // Since this Help page can return to several different screens,
            // this checks which controller type got loaded before calling loadUserData().
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).loadUserData(currentUser);
            } else if (controller instanceof AccountBalanceController) {
                ((AccountBalanceController) controller).loadUserData(currentUser);
            } else if (controller instanceof DepositWithdrawController) {
                ((DepositWithdrawController) controller).loadUserData(currentUser);
            } else if (controller instanceof ActivityTrackerController) {
                ((ActivityTrackerController) controller).loadUserData(currentUser);
            } else if (controller instanceof ScenarioController) {
                ((ScenarioController) controller).loadUserData(currentUser);
            } else if (controller instanceof WalletController) {
                ((WalletController) controller).loadUserData(currentUser);
            } else if (controller instanceof AssetsController) {
                ((AssetsController) controller).loadUserData(currentUser);
            } else if (controller instanceof InboxController) {
                ((InboxController) controller).loadUserData(currentUser);
            } else if (controller instanceof SettingsController) {
                ((SettingsController) controller).loadUserData(currentUser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}