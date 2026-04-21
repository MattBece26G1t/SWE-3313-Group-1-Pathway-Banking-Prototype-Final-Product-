/**
 * HelpManager stores and serves all built-in help content for the app.
 *
 * This class is basically the library for the Help screen.
 * It creates all help entries up front, keeps them in one list, and then
 * lets other parts of the program pull the right help text by section
 * and category.
 *
 * The main reason this class exists is so the HelpController does not have to
 * giant strings directly inside the UI logic. Instead, the controller
 * just asks this class for the right content when needed.
 */

package org.example.pathwayver1;

import java.util.ArrayList;

public class HelpManager {

    // Holds every help entry used in the program
    private ArrayList<HelpEntry> entries;

    /*
     * Builds the manager and loads all hardcoded help content into memory.
     * Since the help text is static in this project, it makes sense to load
     * everything once here and then just reuse it when the Help page opens.
     */
    public HelpManager() {
        entries = new ArrayList<>();
        loadEntries();
    }

    /*
     * This method creates every help entry used by the app.
     *
     * Each section usually gets 2 entries:
     * 1. faq
     * 2. interface
     *
     * faq is more like question/answer help
     * interface explains how the actual screen works
     *
     * This is a long method because all of the help text is written directly
     * into the program instead of being pulled from a file or database.
     */
    private void loadEntries() {

        // ===== DASHBOARD =====

        // Dashboard FAQ
        entries.add(new HelpEntry("dashboard", "faq",
                "Q: What is the Dashboard?\n" +
                        "A: The Dashboard is your central navigational hub. It is the first screen you will see everytime you log in. It also shows a summary of your accounts, wallet, recent activity, and available scenarios.\n\n" +
                        "Q: What do the lock icons mean in Balance Overview?\n" +
                        "A: Lock icons represent accounts you haven't unlocked yet. You can unlock Credit or Savings accounts from the Account Balances section by making a down payment. Doing so will occupy the locked icon space with the account you have unlocked and its balance.\n\n" +
                        "Q: What is the Wallet balance on the right side?\n" +
                        "A: Your Wallet holds virtual cash separate from your bank accounts. Deposits come from the Wallet, and withdrawals go back to the Wallet. Additionally, certain Scenario or Asset behaviors can also deposit funds into your Wallet. The movement of money has to come from somewhere!\n\n" +
                        "Q: What does the notification number mean?\n" +
                        "A: The number on the inbox icon with a red dot shows how many unread messages you have. Click the inbox icon to view them.\n\n" +
                        "Q: What is the Scenario Overview?\n" +
                        "A: It previews the next available financial Scenario for you to practice making financial decisions."
        ));

        // Dashboard Interface Help
        entries.add(new HelpEntry("dashboard", "interface",
                "• The User is greeted in the Dashboard with a Welcome + [User] message! Here you are able to navigate between mostly all areas of the program.\n\n" +
                        "• Balance Overview: Shows your default Debit account balance and any unlocked accounts (Credit/Savings) with their current balances. All users by default start off with a Debit account. In order to view the full contents of your banking accounts, travel to the Deposit/Withdraw tab on the bottom of the interface.\n\n" +
                        "• Activity Overview: Displays your most recent transaction in a slightly summarized display. Transactional actions that are documented include deposits, withdrawals, transfers, or payments. In order to view all Transactional activities, travel to the Activity Tracker tab on the bottom of the interface.\n\n" +
                        "• Scenario Overview: Shows a slight preview of the next available Scenario Activity. Go to the Scenario Icon on the top left to view all the possible Scenarios available in the Scenario Browser.\n\n" +
                        "• Bottom Tabs: Navigate between Dashboard, Account Balances, Deposit/Withdraw, and Activity Tracker. These tabs are always visible when traveling between either of the four section's general display. More detailed areas of any of those four sections, hides those bottom navigational tabs and places a BACK button instead. Icon sections similarly do this as well.\n\n" +
                        "• Top Icons: Scenario (piggy bank) opens the Scenario Browser. Help (?) opens the current section your reading, there is a Help (?) icon for every area you traverse to in the application. Inbox (envelope) shows notifications, when theres a red dot with a number on the envelope, it means you received a message. Settings (gear) lets you edit your profile credentials. Logout (door) ends your session for the current logged in account.\n\n" +
                        "• Wallet Icon: Shows your wallet balance and opens the Wallet section when clicked. Think of your wallet as if your holding physical funds in your pocket.\n\n" +
                        "• Assets Icon: Opens your owned items and subscriptions."
        ));

        // ===== ACCOUNT BALANCE =====

        entries.add(new HelpEntry("accountBalance", "faq",
                "Q: How do I unlock a new account?\n" +
                        "A: Click the + icon on an empty slot, choose Credit or Savings, and make a down payment from your Debit account. Credit costs $150 and Savings costs $100.\n\n" +
                        "Q: Can I have more than one of the same account type?\n" +
                        "A: Yes! You can have up to two additional accounts in any combination of Credit and Savings.\n\n" +
                        "Q: How do I delete an account?\n" +
                        "A: Go to View Details for that account and scroll to the bottom. For Savings, you must transfer all funds to Debit first. For Credit, you must pay off all debt first.\n\n" +
                        "Q: What does the Activity/History section show?\n" +
                        "A: It shows the most recent transactional action for that specific account.\n\n" +
                        "Q: What does Pay Due mean on the Credit card?\n" +
                        "A: It shows when your next credit payment is due. If you don't pay at least the minimum payment by that date, a $10 late fee is applied."
        ));

        entries.add(new HelpEntry("accountBalance", "interface",
                "• Section for viewing all of your Banking Accounts. This page is the general overview of all your existing owned accounts. You can get more details of your Banking Account by selecting the View Details button!\n\n" +
                        "• Debit Account: Your default account. Always visible. Shows balance, account number, and recent activity. Cannot be deleted.\n\n" +
                        "• Additional Slots: Two slots for either Credit and/or Savings accounts. Shows a + icon on a empty slot. Users are only allowed three banking accounts, which includes Debit as one of them.\n\n" +
                        "• Add Account (+): Click to expand and choose Credit ($150 down payment) or Savings ($100 down payment). The down payment funds MUST come from your Debit account. No funds from your Debit account will not allow you to unlock the desired banking account.\n\n" +
                        "• View Details: Opens a detailed view of that banking account with full transaction history and additional options. Debit, Credit, and Savings accounts each have unique additional fields to them.\n\n" +
                        "• Bottom Tabs: Same navigation as Dashboard — switch between those four sections freely."
        ));

        // ===== DEPOSIT/WITHDRAW =====

        entries.add(new HelpEntry("depositWithdraw", "faq",
                "Q: Where does deposited money come from?\n" +
                        "A: All deposits come from your Wallet. You must have enough funds in your Wallet to deposit.\n\n" +
                        "Q: Where does withdrawn money go?\n" +
                        "A: Withdrawn money goes back into your Wallet. You can only withdraw from your Debit account.\n\n" +
                        "Q: Why can't I withdraw from Savings?\n" +
                        "A: Savings accounts are designed for long-term saving. To move money out of Savings, you must use the Transfer feature instead. Transfer fees may apply depending on the amount of transfer movements with Savings accounts.\n\n" +
                        "Q: What is the Transfer feature?\n" +
                        "A: Transfer lets you move money between your own accounts (Account Icon) or send money to a contact (Contact Icon).\n\n" +
                        "Q: What can I pay in the Payment section?\n" +
                        "A: You can pay off Credit card balances, subscription bills, pending debt from scenarios, and transfer requests from contacts. The option must appear first in the drop down menu of the Payment section.\n\n" +
                        "Q: What is Pending Debt?\n" +
                        "A: When a Scenario deducts money and you don't have enough in your Wallet, then in any available Credit cards, and lastly Debit account, the remaining amount becomes Pending Debt. When you earn money through Scenarios or random transfers from contacts, it automatically goes toward paying off this debt first. Additionally, if you have a Savings account, you may be able to transfer funds to your Debit account and pay off the Pending Debt. Don't trust leaving funds while in your Wallet, as earning rewarded funds into Wallet automatically dispenses everything out for payment, while having auto-pay Pending Debt enabled.\n\n" +
                        "Q: Does auto-pay Pending Debt go away?\n" +
                        "A: Yes! It goes away once all Pending Debt is resolved. You don't have to worry about funds being removed automatically from your Wallet. Make sure to cover your financial decisions next time."
        ));

        entries.add(new HelpEntry("depositWithdraw", "interface",
                "• Section to perform various transactional actions! This one of the areas where the movement of money occurs.\n\n" +
                        "• Deposit: Enter an amount and select a Debit or Savings account and press Deposit! Deposit funds MUST come from your Wallet. A confirmation popup will appear before processing.\n\n" +
                        "• Withdraw: Enter an amount and select your Debit account and press Withdraw! Withdraw funds go to your Wallet. Only Debit accounts are available for withdrawal.\n\n" +
                        "• Transfer - Account: A separate screen will appear for you to transfer. Move money between your Debit and Savings accounts. Or move money between two Savings Accounts. Enter an amount and press Transfer! Transferring FROM Savings has fees: first 3 free, next 6 cost $3 each, then $6 each after.\n\n" +
                        "• Transfer - Contact: Send money to predefined contacts. Transferring funds will always come from your Debit account. Enter an amount to transfer and press Transfer!\n\n" +
                        "• Payment: Select an entity to pay off if available (Credit card, Subscription, Pending Debt, or Transfer Request). You can choose to pay from Debit or Credit for subscriptions mainly. Otherwise, your Debit card is used for payment selections.\n\n" +
                        "• Wallet Icon: Shows your wallet balance and opens the Wallet section when clicked. Use this as a reference for when making transactional decisions. Think of your wallet as if your holding physical funds in your pocket.\n\n" +
                        "• Bottom Tabs: Same navigation as Dashboard — switch between those four sections freely."
        ));

        // ===== ACTIVITY TRACKER =====

        entries.add(new HelpEntry("activityTracker", "faq",
                "Q: What do the colored circles mean?\n" +
                        "A: Green = Positive financial decision (deposits, payments, good scenario outcomes). Red = Negative financial decision (withdrawals, late fees, bad scenario outcomes). Yellow = Neutral (transfers, account unlocks).\n\n" +
                        "Q: How is a decision rated as positive or negative?\n" +
                        "A: The system evaluates based on the type of transaction. Deposits and payments are generally positive. Withdrawals and fees are negative. Transfers are neutral. For scenarios, the feedback system evaluates your specific choice.\n\n" +
                        "Q: Can I filter my activity?\n" +
                        "A: Yes! Use the Transaction Type filter to see only Deposits, Withdrawals, Transfers, or Payments. Use the Account Type filter to see activity for specific account types.\n\n" +
                        "Q: Why does a Scenario deduction show as negative even if I made a reasonable choice?\n" +
                        "A: Any money leaving your accounts is tracked as a deduction. The scenario feedback popup gives you the educational evaluation — the Activity Tracker just records the financial movement. The system however is a bit strict. Regardless if you've made a financial or Scenario decision you believe it good, anything that results in a withdraw of funds can be perceived as a negative outcome, even when the decision was reasonable."
        ));

        entries.add(new HelpEntry("activityTracker", "interface",
                "• Area to view all of your documented financial related activities within the application!\n\n" +
                        "• Transaction List: Shows all your financial activity in reverse order (newest first). Each entry generally shows the type, amount, account, and date.\n\n" +
                        "• Colored Circles: Green (positive), Red (negative), Yellow (neutral) — indicates the financial impact of each transaction.\n\n" +
                        "• Filter by Transaction Type: Select All Transactions, Deposits, Withdrawals, Transfers, or Payments to narrow the list.\n\n" +
                        "• Filter by Account Type: Select All Accounts, Debit, Credit, or Savings to see activity for specific accounts.\n\n" +
                        "• Bottom Tabs: Same navigation as Dashboard — switch between those four sections freely."
        ));

        // ===== SCENARIO =====

        entries.add(new HelpEntry("scenario", "faq",
                "Q: What are Scenarios?\n" +
                        "A: Scenarios are guided financial situations that teach you about money management. You read a situation and choose how to respond. Your choice affects your money and you receive feedback.\n\n" +
                        "Q: What do the difficulty badges mean?\n" +
                        "A: Beginner scenarios are simpler financial decisions suitable for younger users. Advanced scenarios involve more complex situations with multiple financial factors.\n\n" +
                        "Q: What does the completion status mean?\n" +
                        "A: Available = you haven't started it. In Progress = you opened it but didn't finish. Completed = you've finished it.\n\n" +
                        "Q: Can I replay completed Scenarios?\n" +
                        "A: Yes! This goes for any completed Scenario. When all scenarios are completed, a replay option appears at the bottom of the list to reset them all.\n\n" +
                        "Q: What is Toggle Mandatory Scenario?\n" +
                        "A: When enabled, emergency financial scenarios may interrupt you during normal use. These are events that force you to make abrupt financial decisions, just like real emergencies.\n\n" +
                        "Q: What happens to my money during scenarios?\n" +
                        "A: Positive choices may earn money added to your Wallet. Negative choices deduct money when the funds are available — first from Wallet, then from any available Credit cards, then Debit, and if neither of those three have available funds, then it becomes a Pending Debt.\n\n" +
                        "Q: What is the Idea button?\n" +
                        "A: It gives you a general hint to help persuade your financial decision without giving you a direct exact answer to how you should manage your money."
        ));

        entries.add(new HelpEntry("scenario", "interface",
                "• Scenario Browser: Lists all available scenarios with their title, category, difficulty badge, and completion status.\n\n" +
                        "• Filter by Category: Show only Emergency Expense, Everyday Spending, Bills and Expenses, or Income Opportunity scenarios.\n\n" +
                        "• Filter by Completion: Show only Available, In Progress, or Completed scenarios.\n\n" +
                        "• Toggle Mandatory: Switch on/off emergency scenario interruptions. When on, emergencies can pop up every 5 minutes. These are required to complete and you cannot navigate through the interface when encountered with a mandatory Scenario. Emergency type Scenarios will only pop-up for toggled Mandatory Scenarios. You can still complete other Scenario types freely while the toggle is on.\n\n" +
                        "• Scenario Activity: Switches to the Scenario Activity once you click on a Scenario, where you will engage with its contents. Shows the Scenario description in a chat bubble with response options below. Pick a response to see the outcome.\n\n" +
                        "• Difficulty Badge: Blue = Beginner, Red = Advanced. Child-mode users only see Beginner scenarios. Full-Access include both Beginner and Advanced Scenario Activities. The badges help you determine how difficult the Scenario's contents are for you.\n\n" +
                        "• Feedback Popup: After each Scenario, you receive feedback explaining why your choice was good, cautionary, or negative, along with any money earned or deducted.\n\n" +
                        "• Back Button: Returns you to the Dashboard section."
        ));

        // ===== WALLET =====

        entries.add(new HelpEntry("wallet", "faq",
                "Q: What is the Wallet?\n" +
                        "A: The Wallet is a separate virtual cash storage. It holds money independently from your bank accounts. Think of it like physical cash in your pocket.\n\n" +
                        "Q: How does money get into my Wallet?\n" +
                        "A: Scenario rewards, random contact transfers, and withdrawals from your Debit account all add money to your Wallet.\n\n" +
                        "Q: How does money leave my Wallet?\n" +
                        "A: Depositing money into bank accounts and scenario deductions take money from your Wallet.\n\n" +
                        "Q: What is Pending Debt auto-payment?\n" +
                        "A: If you have Pending Debt and receive money in your Wallet, the system automatically uses that money to pay off the debt first before adding it to your balance. So if your Wallet balance still displays zero despite receiving rewarded funds, the history panel should explain it was a result of the auto-pay Pending Debt. Ensure all your debts are clear in order to keep your Wallet safe.\n\n" +
                        "Q: Can contacts send me money?\n" +
                        "A: Yes! Contacts like Mom, Dad, and friends may randomly send you money. You'll see a notification in your Inbox when this happens."
        ));

        entries.add(new HelpEntry("wallet", "interface",
                "• Area to view your \"physical funds\", which is handled by the application!\n\n" +
                        "• Wallet Balance: Displays your current Wallet cash amount.\n\n" +
                        "• History: Scrollable list of all Wallet events — deposits received, deductions, scenario rewards, contact transfers, and debt payments.\n\n" +
                        "• Back Button: Returns you to wherever you came from — Dashboard or Deposit/Withdraw section."
        ));

        // ===== ASSETS =====

        entries.add(new HelpEntry("assets", "faq",
                "Q: What are Assets?\n" +
                        "A: Assets are virtual items you earn through Scenario outcomes. They can be one-time purchases (like a Piggy Bank) or subscriptions (like a Gaming Subscription).\n\n" +
                        "Q: What's the difference between one-time items and subscriptions?\n" +
                        "A: One-time items have a fixed value and can be sold for a resale price. Subscriptions have recurring costs that you must pay each billing cycle.\n\n" +
                        "Q: How do subscription payments work?\n" +
                        "A: Subscriptions start as Paid. After the billing cycle (20 minutes), they become Unpaid. You must pay them from the Payment section in Deposit/Withdraw. If you don't pay, a $5 late fee is added each cycle.\n\n" +
                        "Q: Can I cancel a subscription?\n" +
                        "A: Yes, but only if it's currently Paid. If it's Unpaid, you must pay the current cycle first before canceling.\n\n" +
                        "Q: Can I sell one-time items?\n" +
                        "A: Yes! Click on an item to see its details and resale value, then click Sell. The resale money goes to your Wallet.\n\n" +
                        "Q: Why are some items highlighted in red?\n" +
                        "A: Red-tinted items are subscriptions with recurring costs."
        ));

        entries.add(new HelpEntry("assets", "interface",
                "• Area to view your \"physical\" items or subscriptions as a result from Scenario outcomes!\n\n" +
                        "• Asset Grid: Shows all owned items as clickable icons. Red tint = subscription, normal = one-time purchase.\n\n" +
                        "• Detail Panel (right side): Shows the selected item's name, type, value (one-time time) or cost (subscription), and resale value (one-time time) or next due date (subscription).\n\n" +
                        "• Sell / Cancel Sub Button: For one-time items, sells for resale value. For subscriptions, cancels the subscription (only when Paid).\n\n" +
                        "• Back Button: Returns you to the Dashboard section."
        ));

        // ===== INBOX =====

        entries.add(new HelpEntry("inbox", "faq",
                "Q: What kinds of notifications do I receive?\n" +
                        "A: Transaction confirmations, low balance warnings, late fee alerts, Scenario completion notices, contact transfer notifications, and subscription payment reminders.\n\n" +
                        "Q: What does the red dot mean?\n" +
                        "A: A red dot means the notification is unread. Click on it to read the full message and the dot disappears.\n\n" +
                        "Q: What does Mark All Read do?\n" +
                        "A: It marks every notification as read at once, removing all red dots in this section and banner on the Dashboard.\n\n" +
                        "Q: When do I get a low balance warning?\n" +
                        "A: When your Debit account drops below $50, you receive a warning notification. This only triggers once until you deposit above $50 again."
        ));

        entries.add(new HelpEntry("inbox", "interface",
                "• Area to view your notifications within the application!\n\n" +
                        "• Notification List: Scrollable list of all notifications, newest first. Each entry shows the message with a red dot for unread.\n\n" +
                        "• Filter by Read: Show All Messages, only Unread, or only Read notifications.\n\n" +
                        "• Categorize: Group notifications By Date (default) or By Source (Banking, System, Assets, Educational).\n\n" +
                        "• Mark All Read: Button to mark every notification as read at once.\n\n" +
                        "• Click a Notification: Opens a popup with the full message details including type, source, and date.\n\n" +
                        "• Back Button: Returns you to the Dashboard section."
        ));

        // ===== SETTINGS =====

        entries.add(new HelpEntry("settings", "faq",
                "Q: What can I change in Settings?\n" +
                        "A: You can edit your name, email, phone number, address, date of birth, and password. Your UserID cannot be changed.\n\n" +
                        "Q: How do I change my password?\n" +
                        "A: Click the Edit button next to the password field. You'll be taken to the password reset screen where you can create a new password. Give it a second to load, the enter your registered email page should transfer you to the reset password page.\n\n" +
                        "Q: What is the Difficulty Level?\n" +
                        "A: Your difficulty is determined by your age. Users under 12 are in Child-mode (Beginner Scenarios only). Users 12 and older are in Full-Access mode (all Scenarios available).\n\n" +
                        "Q: Can I change my difficulty?\n" +
                        "A: You can't directly change it, but changing your date of birth manually will recalculate your age and may change your difficulty level. The system will however transition you from Child-mode to Full-Access on its own once you have reached the compatible age.\n\n" +
                        "Q: What does the eye icon do?\n" +
                        "A: It toggles your password visibility — click to show your actual password, click again to hide it."
        ));

        entries.add(new HelpEntry("settings", "interface",
                "• Edit the following user credentials!\n\n" +
                        "• Name Fields: Edit your first and last name.\n\n" +
                        "• Electric Communication: Edit your email and phone number.\n\n" +
                        "• Address Fields: Edit street, city, state, zip code, and country.\n\n" +
                        "• Date of Birth: Edit your birth date. This affects your difficulty level.\n\n" +
                        "• Current Difficulty: Shows Child-mode or Full-Access based on your age.\n\n" +
                        "• UserID: Displayed but cannot be edited.\n\n" +
                        "• Password: Shows masked dots. Use the eye icon to reveal it, or Edit button to change it.\n\n" +
                        "• Save Button: Validates and saves all changes. Shows success or error message.\n\n" +
                        "• Back Button: Returns you to the Dashboard section."
        ));

        // ===== DEBIT DETAIL =====

        entries.add(new HelpEntry("debitDetail", "faq",
                "Q: Can I delete my Debit account?\n" +
                        "A: No. Your Debit account is your default account and cannot be deleted.\n\n" +
                        "Q: What transactions show in the history?\n" +
                        "A: Only transactions involving this specific Debit account — deposits, withdrawals, transfers, and payments made from this account."
        ));

        entries.add(new HelpEntry("debitDetail", "interface",
                "• Account Number: Your unique Debit account identifier.\n\n" +
                        "• Balance: Current funds in this account.\n\n" +
                        "• Transaction History: Scrollable list of all transactions for this account only.\n\n" +
                        "• Back Button: Returns to Account Balances."
        ));

        // ===== SAVINGS DETAIL =====

        entries.add(new HelpEntry("savingsDetail", "faq",
                "Q: What are the withdrawal fees?\n" +
                        "A: First 3 transfers from Savings are free. Transfers 4-9 cost $3 each. After that, each transfer costs $6. These fees discourage frequent withdrawals from savings.\n\n" +
                        "Q: Can I deposit directly into Savings?\n" +
                        "A: Yes! You can deposit from your Wallet into Savings through the Deposit section. Money withdrawn through Savings can only be transferred into your Debit account.\n\n" +
                        "Q: What happens when I delete a Savings account?\n" +
                        "A: If you have funds, you must transfer everything to your Debit account first. The transfer may include fees based on your withdrawal count. Your $100 down payment is not refunded."
        ));

        entries.add(new HelpEntry("savingsDetail", "interface",
                "• Account Number: Your unique Savings account identifier.\n\n" +
                        "• Balance: Current funds in this account.\n\n" +
                        "• Withdraw Status: Shows your current transfer fee tier — how many free/paid transfers you've used.\n\n" +
                        "• Transaction History: Scrollable list of all transactions for this account only.\n\n" +
                        "• Delete Button: Removes this Savings account. Requires transferring all funds out first.\n\n" +
                        "• Back Button: Returns to Account Balances."
        ));

        // ===== CREDIT DETAIL =====

        entries.add(new HelpEntry("creditDetail", "faq",
                "Q: What is a credit limit?\n" +
                        "A: The maximum amount you can charge to your Credit card. Your limit is $500.\n\n" +
                        "Q: What is Available Credit?\n" +
                        "A: Your credit limit minus what you owe. If your limit is $500 and you owe $200, you have $300 available.\n\n" +
                        "Q: What is the minimum payment?\n" +
                        "A: The smallest amount you must pay each billing cycle to avoid a late fee. It's 10% of your balance owed, with a minimum of $5.\n\n" +
                        "Q: What happens if I don't pay the minimum?\n" +
                        "A: A $10 late fee is added to your balance. If you pay at least the minimum, no late fee is applied, but you still owe the remaining balance.\n\n" +
                        "Q: Can I pay more than the minimum?\n" +
                        "A: Yes! Paying more reduces your balance faster and frees up available credit. Paying only minimums keeps you in debt longer.\n\n" +
                        "Q: How do charges get added to my Credit card?\n" +
                        "A: When Scenarios deduct money and your Wallet is empty, charges go to your Credit card. Subscription payments can also be charged to Credit if you use your Credit account to pay them.\n\n" +
                        "Q: What happens when I delete a Credit account?\n" +
                        "A: You must pay off all debt first. Your $150 down payment is not refunded."
        ));

        entries.add(new HelpEntry("creditDetail", "interface",
                "• Account Number: Your unique Credit account identifier.\n\n" +
                        "• Balance Owed: How much debt you currently have on this card.\n\n" +
                        "• Credit Limit: Maximum amount you can charge ($500).\n\n" +
                        "• Available Credit: How much more you can charge (limit minus owed). When Available Credits reach the limit, you can no longer use this account until you start to pay off your balance owed.\n\n" +
                        "• Minimum Payment: The least you must pay this cycle to avoid a late fee.\n\n" +
                        "• Payment Due Date: When your next payment is due. Late payments incur a $10 fee.\n\n" +
                        "• Pay Off Button: Opens the Payment screen with this Credit card pre-selected.\n\n" +
                        "• Transaction History: Scrollable list of all transactions for this account only.\n\n" +
                        "• Delete Button: Removes this Credit account. Requires paying off all debt first.\n\n" +
                        "• Back Button: Returns to Account Balances."
        ));

        // ===== SCENARIO ACTIVITY =====

        entries.add(new HelpEntry("scenarioActivity", "faq",
                "Q: Can I leave a mandatory Scenario?\n" +
                        "A: No. Mandatory emergency Scenarios must be completed before you can return to normal use.\n\n" +
                        "Q: What if I don't have money for any option?\n" +
                        "A: Every scenario has at least one option that costs nothing. If money is deducted and you can't afford it, the system takes what it can and the rest becomes Pending Debt.\n\n" +
                        "Q: Do transfer Scenarios actually move my money?\n" +
                        "A: Yes. If the Scenario involves transferring money to a contact, you'll be prompted to transfer. If you choose Not Yet, the transfer appears in your Payment section for later. Some Scenarios may also deduct or reward money from you depending on your decision.\n\n" +
                        "Q: How are Assets rewarded?\n" +
                        "A: Assets are rewarded depending on your response to a Scenario Activity. You may be rewarded with a one-time item or a subscription."
        ));

        entries.add(new HelpEntry("scenarioActivity", "interface",
                "• Chat Bubble: Displays the Scenario description and situation.\n\n" +
                        "• Response Buttons: 2 or 4 options may be available to choose from depending on the activity. Click one to submit your response. Responses that do not contain any description are not clickable.\n\n" +
                        "• Difficulty Badge: Shows Beginner (blue) or Advanced (red).\n\n" +
                        "• Idea Button: Gives you a general hint to persuade your decision.\n\n" +
                        "• Feedback Popup: After responding, shows whether your choice was positive, cautionary, or negative, along with any money earned or deducted.\n\n" +
                        "• Back Button: Returns to Scenario Browser (blocked during mandatory scenarios)."
        ));
    }

    /*
     * Returns the full list of help entries.
     * This is mostly useful if some other part of the program ever wants
     * to loop through everything instead of asking for one specific section.
     */
    public ArrayList<HelpEntry> getEntries() {
        return entries;
    }

    /*
     * Filters the help list by section and category.
     *
     * Example:
     * section = "dashboard"
     * category = "faq"
     *
     * This returns all matching entries, even though the current program
     * usually expects just one match per section/category combo.
     */
    public ArrayList<HelpEntry> getEntriesByCategory(String section, String category) {
        ArrayList<HelpEntry> results = new ArrayList<>();
        for (HelpEntry entry : entries) {
            if (entry.getTopic().equals(section) && entry.getCategory().equals(category)) {
                results.add(entry);
            }
        }
        return results;
    }

    /*
     * Returns the actual help text for one section/category pair.
     * This is the method HelpController uses the most.
     * Instead of returning the whole HelpEntry object, this just gives back
     * the content string since that is what gets shown on screen.
     * If nothing matches, it returns a fallback message instead.
     */
    public String getContent(String section, String category) {
        for (HelpEntry entry : entries) {
            if (entry.getTopic().equals(section) && entry.getCategory().equals(category)) {
                return entry.getContent();
            }
        }
        return "No help content available for this section.";
    }
}