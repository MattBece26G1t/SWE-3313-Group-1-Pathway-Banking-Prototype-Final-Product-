# SWE-3313-Group-1-Pathway-Banking-Prototype-Final-Product-

## Relevant Course Information

Kennesaw State University

Intro to Software Engineering Section W04 Spring Semester 2026 CO 

Final-Product Deliverable (Deliverable 6)

Group 1: Matthew Becerra, Rumel Ahmed, Leandro Cherulli, Bradley Crasto, Kentrel Brown

Product Name: Pathway Banking – Desktop Financial Learning Application 

Version 1.0

Release Date: 4/16/2026

## Performance Adjustments Notes in SceneBuilder (For Team Development and/or Running the Application)

There were performance complications involving the project's SceneBuilder, where editing in SceneBuilder and running the GUI application posed a significant slow down in performance. One method that helped to run the application smoother was by editing the Scenebuilder.cfg file. Find where ever located in your files the Scenebuilder.cfg and open it in your Notepad application or Visual Studio Code. 

For example, my directory path to locating this file was this: 



    C:\Users\name\AppData\Local\SceneBuilder\app

Upon finding and opening your Scenebuilder.cfg file, it present the similar contents in your Notepad or Visual Studio Code:


    [Application]
    app.classpath=$APPDIR\scenebuilder-25.0.0-all.jar
    app.mainclass=com.oracle.javafx.scenebuilder.app.SceneBuilderApp
    app.classpath=$APPDIR\scenebuilder-25.0.0-SNAPSHOT-all.jar
    
    [JavaOptions]
    java-options=-Djpackage.app-version=25.0.0
    java-options=-Djavafx.allowjs=true
    java-options=--add-opens=javafx.fxml/javafx.fxml=ALL-UNNAMED
    java-options=--enable-native-access=javafx.graphics
    java-options=--sun-misc-unsafe-memory-access=allow
    java-options=-Djava.library.path=runtime\bin;runtime\lib

Add this line to the very end of the file and save:

    java-options=-Xmx2048m


<ins>Note:</ins>

Including the line increased the memory capacity of Scenebuilder from its usualy deafult 256MB storage to 2048MB (2GB), preventing and/or minimizing lag, freezing, or crashing while working or running with FXML files that have to many components or images.

<ins>Personal Note:</ins>

Because everyone's operating system is different, the application primairly consists of multiple PNG backdrops and elements, as a method to optimize performance while working in Scenebuilder. Desiging visual properties was discovered to be a major source of slowing down the work space/interface. Instead, as a work around to not overload the IDE with to many components, any visual components such as icons, drawings, and backdrops were designed outside of Scenebuilder, using Draw.io. Scenebuilder would only be used to give these designed elements the impression of functionality, by first importing the designs and then stacking built-in Scenebuilder elements over it, such as buttons, labels, and comboboxes on to the scene. Pre-designing the visual elements that shape the Pathway Banking identity, before implementing in Scenebuilder saved alot of performance issues encountered. Also sparing the effort of a team member's operating system can handle.


## Project Overview

Pathway Banking is a prototype desktop financial learning application designed to introduce users of all ages, particularly focused on younger users from ages 5 and up, to the fundamentals of personal finance and money management. Built as a simulated banking environment, the application provides a safe, offline space where users can interact with realistic financial tools and concepts without the consequences of using real money. The experience is intended to nearly replicate the experience of using an existing banking application, with additions that makes this project have a entirely unique educational identity. Additional elements that are used to emulate financial scenarios and actions that are not commonly found in realistic banking systems.

The application simulates the experience of managing a personal banking portfolio, complete with Debit, Savings, and Credit accounts, a virtual Wallet, and a catalog of financial scenarios that challenge the user to make informed decisions. Users are encouraged to explore how deposits, withdrawals, transfers, and payments work, while learning about concepts such as credit limits, interest-free billing cycles, late fees, minimum payments, subscription management, and debt accumulation.

Pathway Banking's identity apart from other traditional financial education tools is its commitment to adaptive difficulty and real time consequences, without overly restricting the user based on their decesions and has the liberty to make any sort of financial decision. The system will mainly tell the user how to reflect on their action, without overtly stepping in for control to correct the user's decision. The application features a Child-mode for younger users (users that are 12) that filters out advanced content, ensuring an age-adjusted experience, while Full-Access mode (for users over 12) unlocks the complete range of financial scenarios and mechanics for older users. Every financial action, whether it is a good decision or a poor one, is tracked, recorded, and reflected across the entire application. Users can observe how their choices impact their account balances, wallet, credit standing, and asset portfolio in real time, reinforcing the relationship between decisions and outcomes.

The core philosophy behind Pathway Banking is that financial literacy is best learned through experience. Rather than presenting users with static lessons or textbook definitions and numerous amounts of text, the application places them in scenario-driven situations where every choice has a some sort of financial outcome. These financial situations can vary between deciding how to spend birthday money, handling an emergency expense, or managing a recurring subscription. Each interaction is designed to reinforce fundamental practical money skills in an engaging and approachable way.

Additionally, from a course perspective that the project was actualized from, the application was built to reflect the full software development lifecycle, from initial requirements gathering and system design to implementation, testing, and now delivery. The project's Software Design Document served as the main architectural blueprint throughout development, outlining seven key subsystems: User Authentication, Dashboard and Navigation, Banking Operations, Educational Features, Wallet and Assets, System Services, and Data Persistence.



## Features

Pathway Banking offers a wide range of financial tools and educational features, all designed to simulate a realistic banking experience within a controlled learning environment.

### User Authentication

The application supports full account creation through a guided multi-step registration process, secure login with lockout protection after multiple failed attempts, account recovery through either a registered email or phone number lookup, and password reset functionality using registered email and DOB. Users are assigned a unique UserID and can manage their credentials directly from within the application's Settings.

### Dashboard

The Dashboard serves as the central hub for all user activity. It provides a real time overview of the user's Debit account balance, Wallet balance, most recent transaction, and next available scenario cropped summary. Unlocked accounts such as Credit and Savings are displayed with their current balances. The Dashboard also features a notification counter that alerts users to unread messages in their Inbox, and offers quick navigation to every section of the application.

### Account Management

Users begin with a default Debit account and can unlock up to two additional accounts: Credit and Savings, by making down payments from their Debit balance. Credit accounts come with a $500 credit limit, minimum payment requirements, and a billing cycle that applies late fees for missed payments. Savings accounts are designed for long term holding, with a tiered fee structure that discourages frequent withdrawals. Money from Savings can only be withdrawn using the Transfer Account feature. Users can view detailed account information, transaction history, and delete accounts when appropriate conditions are met.

### Deposit and Withdraw

Users can deposit funds from their Wallet into any Debit or Savings account, and withdraw funds from their Debit account back into the Wallet. Savings accounts are intentionally excluded from direct withdrawals to reinforce the habit of preserving savings. Users must use the Transfer feature instead, which carries progressive fees the more frequently it is used.

### Transfer System

The Transfer feature allows users to move money between their own accounts or send money to simulated contacts. Banking account-to-account transfers between Debit and Savings accounts are subject to a tiered fee structure when transferring out of Savings — the first three transfers are free, the next six cost $3 each, and all transfers after that cost $6 each. Contact transfers allow users to send money to nine predefined contacts including family members and friends, always drawing from the Debit account.

### Payment System

The Payment section consolidates all financial obligations into one interface. Users can pay off Credit card balances, subscription bills, pending debt accumulated from scenario outcomes, and transfer requests generated by contact-related scenarios. Payments for Credit cards can only be made from the Debit account, while subscription payments can be made from either Debit or Credit — teaching users that charging subscriptions to credit creates additional debt.

### Wallet

The Wallet functions as a separate virtual cash reserve, independent from the user's bank accounts. All deposits draw from the Wallet, all withdrawals return to the Wallet, and all scenario rewards are deposited into the Wallet. The Wallet also features an automatic debt payment system. When pending debt exists and new funds enter the Wallet, the system automatically applies those funds toward the outstanding balance before they become available.

### Scenario System

The application features 23 predefined financial scenarios across four categories: Everyday Spending, Emergency Expense, Bills and Expenses, and Income Opportunity. Each scenario presents a real-world financial situation with multiple response options, and every choice carries a tangible financial consequence. Positive outcomes may earn money or reward the user with virtual assets, while negative outcomes may deduct funds from the Wallet, charge Credit accounts, reduce Debit balances, or accumulate as pending debt. Some scenarios are designated as mandatory emergencies that can interrupt the user during normal activity when the Toggle Mandatory feature is enabled. These emergency scenarios loop indefinitely, ensuring users are regularly tested on quick financial decision making. After completing a scenario, users receive detailed feedback explaining why their choice was positive, cautionary, or negative.

### Asset System

Users can earn virtual assets through scenario outcomes, including one-time items like a Piggy Bank, Savings Journal, Repair Kit, and Headphones, as well as recurring subscription items like Gaming, Streaming, Electric Utility, Water Utility, and Phone Plan services. One-time items can be sold for a resale value that is deposited back into the Wallet. Subscriptions carry recurring costs on a 20-minute billing cycle. Which if the subscription is left unpaid, a $5 late fee is added each cycle. Subscriptions cannot be cancelled while in an unpaid state, reinforcing the importance of staying on top of recurring bills.

### Activity Tracker

Every financial action taken within the application is logged and displayed in the Activity Tracker. Each entry is color-coded with green for positive financial decisions, red for negative decisions, and yellow for neutral actions. Users can filter their activity by transaction type and account type to review specific areas of their financial behavior.

### Inbox and Notifications

The Inbox collects all system generated notifications, from transaction confirmations and low balance warnings to late fee alerts, scenario completion notices, and random contact transfer announcements. Notifications are marked as unread with a red indicator and can be filtered by read status or grouped by source. Users can also mark all notifications as read at once.

### Settings

Users can edit their personal information including name, email, phone number, address, and date of birth. Changes to date of birth may affect the user's difficulty level, toggling between Child-mode and Full-Access mode. Password changes are handled through a dedicated reset flow accessible from within Settings.

### Help System

Every section of the application includes a dedicated Help screen with two tabs, FAQ and Interface Help. The FAQ tab addresses common questions users may have about the section's functionality and underlying financial concepts. The Interface Help tab provides a detailed breakdown of every element visible on screen and what it does. Transfer and Payment sub-windows also feature their own help popups accessible from within those smaller interfaces.

### Data Persistence

All user data is saved between sessions, including account balances, wallet balance, transaction history, wallet events, notifications, scenario completion status, and owned assets. Data is automatically saved when the user logs out or closes the application, and is fully restored upon the next login.



## Tech Stack

Pathway Banking was built using a focused set of tools and technologies selected for their reliability, accessibility, and compatibility with desktop application development.

### Java 21

The core programming language for the entire application. All backend logic, data management, financial mechanics, and controller behavior are written in Java, leveraging its object-oriented structure to organize the application into clearly defined classes and subsystems.

### JavaFX 21

Provided the graphical user interface framework. Every screen in the application is rendered through JavaFX's scene graph system, utilizing FXML files for layout structure and Java controllers for interactive behavior. JavaFX's built-in support for styled alerts, combo boxes, scroll panes, image views, and timeline animations made it possible to create a polished and responsive user experience without relying on external UI libraries.

### SceneBuilder 25

Used as the visual layout editor for all FXML files. Each of the application's 18 screens was designed in SceneBuilder, where backdrop images, buttons, labels, text fields, and other interactive elements were positioned and configured before being wired to their corresponding Java controllers.

### Maven

Handled project build management and dependency resolution. The project's pom.xml defines all required JavaFX modules and third party libraries, ensuring consistent builds across different development environments.

### Draw.io

Played a critical role in the application's visual identity. All backdrop images, icons, popup graphics, asset sprites, scenario badges, and container elements were designed in Draw.io and exported as transparent PNG files. This approach allowed the team to create a fully custom visual aesthetic while keeping SceneBuilder's workload minimal. Reducing lag and performance issues that arose from designing complex visuals directly within the IDE.

### IntelliJ IDEA

Served as the primary integrated development environment throughout the project. All Java source files, FXML resources, and image assets were managed within IntelliJ's project structure, and the application was compiled and tested through its built-in run configurations.



## Prerequisites

Before running Pathway Banking, ensure the following software is installed and properly configured on your system.

### IntelliJ IDEA 

The recommended IDE for opening, building, and running the project. The Community Edition is free and fully supports Maven-based JavaFX projects. When opening the project for the first time, IntelliJ will automatically detect the pom.xml file and prompt you to import the project as a Maven project, accept this prompt to ensure all dependencies are resolved correctly.
 
### Java Development Kit (JDK) 21 or higher 

Required to compile and run the application. The project was developed and tested against JDK 21, though later versions should remain compatible. You can verify your installed version by opening a terminal or command prompt and running "java --version". If JDK 21 is not installed, it can be downloaded from Oracle's official website or through some other open-source distribution. 

### Apache Maven

Required for dependency management and building the project. Maven reads the pom.xml file included in the repository and automatically downloads all necessary JavaFX modules and third party libraries. You can verify Maven is installed by running "mvn --version" in your terminal. If you are using IntelliJ IDEA, Maven is typically bundled with the IDE and does not require a separate installation.

### SceneBuilder 25 

Only required if you intend to edit the application's FXML layout files! 

It's not needed to simply run the application. If you do plan to work with the FXML files, it is strongly recommended to apply the performance adjustment described at the top of this document by adding "java-options=-Xmx2048m" to your SceneBuilder.cfg file, as the application's FXML files contain a significant number of image based components that may cause lag or freezing without the additional memory allocation.

## How to Run

Follow these steps to clone the repository and run Pathway Banking on to your local machine.

**Step 1 - Clone the Repository:** Download the repository as a ZIP file directly from GitHub by clicking the green "Code" button and selecting "Download ZIP." Extract the contents to location of your choice

**Step 2 - Open the Project in Intellij IDEA:** Launch IntelliJ IDEA and select "Open" from the welcome screen. Navigate to the folder where you extracted the project and select it. IntelliJ will detect the pom.xml file and prompt you to import the project as a Maven project. Click on depending what is presented, "Import" or "Load Maven Project," to allow IntelliJ to download all required dependencies automatically.

**Step 3 - Verify Project Structure:** Once the project has loaded, confirm the project structure is intact. View the project panel on the left side of IntelliJ and confirm its file strcture matches similarly to the presentation of the GitHub files

**Step 4 - Configure the Run Configuration:** In most cases, IntelliJ will automatically detect the main class. If it does not, you can set it up manually. Go to "Run" in the top menu bar, select "Edit Configurations," click the "+" button to add a new Application configuration, and set the Main Class to org.example.pathwayver1.Launcher. Click "Apply" and then "OK."

**Step 5 - Run the Application:** Click the green Run button in the top right corner of IntelliJ, or press Shift+F10. Maven will compile the project and launch the application. The Pathway Banking login screen should appear in a 900x600 window.

**Step 6 - Register a New Pathway Banking Account:** Since this is your first time running the application, there will be no existing user data. Click the "Register Here!" hyperlink on the login screen and follow the guided multi-step registration process to create your account. Upon completion, you will be assigned a unique UserID and password that you will use to log in for all future sessions.

**Step 7 - Explore the Application:** After logging in, you will land on the Dashboard. From here you can navigate freely between all sections of the application using the bottom navigation tabs, top icon buttons, and interactive elements throughout each screen. Your progress is automatically saved when you log out or close the application.


## Project Structure

The project follows a standard Maven directory layout with all source code, resources, and configuration files organized under the **src** folder.

### Root Directory

The root of the repository contains **pom.xml**, which defines all project dependencies and build configurations, and **.gitignore**, which excludes IDE settings, build outputs, and user save data from version control.

### Java Source Files

All Java source files are located in **src/main/java/org/example/pathwayver1/**. This directory contains every class that powers the application. 

- **MainApp.java** is the primary entry point that extends JavaFX's Application class, initializes the login screen, manages global timers for late fee checking and mandatory scenario interrupts, and handles application wide save operations. 

- **Launcher.java** is a simple wrapper class that calls MainApp to avoid module system complications with JavaFX.

The source files are organized by function. The model and data classes include: 

- **UserAccount.java** for user profile and credential management 
- **Account.java** for individual banking accounts with support for Debit, Savings, and Credit types
- **Wallet.java** and **WalletManager.java** for virtual cash storage and event tracking
- **WalletEvent.java** for recording wallet activity
- **TransactionRecord.java** for logging all financial actions
- **BankingManager.java** for processing deposits, withdrawals, transfers, payments, late fee checks, and random contact transfers
- **Scenario.java** and **ScenarioManager.java** for the educational scenario library and completion tracking
- **FeedbackMessage.java** and **FeedbackManager.java** for generating and storing scenario outcome feedback
- **Asset.java** and **AssetManager.java** for managing owned items and recurring subscriptions
- **Notification.java** and **NotificationManager.java** for the inbox messaging system
- **TransferRequest.java** for tracking contact transfer obligations created by scenarios
- **HelpEntry.java** and **HelpManager.java** for storing all FAQ and interface help content
- **FileHandler.java** for low-level file read and write operations
- **DataManager.java** for saving and loading all user data across eight persistent text files
  
The controller classes include: 

- **LoginController.java** for login with lockout protection
- **RegistrationController.java** for the multi-step account creation process
- **RecoverIDController.java** for account recovery through email or phone lookup
- **ResetPassController.java** for password reset with support for both login and settings entry points
- **DashboardController.java** for the central hub displaying all account and activity data
- **AccountBalanceController.java** for viewing, unlocking, and managing banking accounts
- **DepositWithdrawController.java** for deposits, withdrawals, transfers, and payments
- **TransferAccountController.java** for account-to-account transfers with savings fee logic and delete mode support
- **TransferContactListController.java** for browsing the simulated contact list
- **TransferContactConfirmController.java** for confirming contact transfers with prefill support for scenario-generated requests
- **PaymentController.java** for processing credit card, subscription, pending debt, and transfer request payments
- **ActivityTrackerController.java** for displaying filtered and color-coded transaction history
- **WalletController.java** for viewing wallet balance and event history
- **ScenarioController.java** for browsing, filtering, and toggling mandatory scenarios
- **ScenarioActivityController.java** for presenting scenario situations, processing responses, applying financial consequences, and awarding assets
- **AssetsController.java** for viewing owned items and managing sales or subscription cancellations
- **InboxController.java** for viewing, filtering, and reading notifications
- **SettingsController.java** for editing user profile information and initiating password changes
- **HelpController.java** for displaying FAQ and interface help content for every section
- **DebitDetailController.java** for viewing debit account details and transaction history
- **SavingsDetailController.java** for viewing savings account details with withdraw fee status and account deletion
- **CreditDetailController.java** for viewing credit account details with balance owed, credit limit, minimum payment, and payment initiation
  
### FXML Files

All FXML layout files are located in **src/main/resources/org/example/pathwayver1/**. Each FXML file corresponds to a screen in the application and defines the layout, positioning, and visual structure of that screen. 

The main 900x600 screens include:

- **LoginView.fxml**
- **RegistrationView.fxml**
- **RecoverIDView.fxml**
- **ResetPassView.fxml**
- **DashboardView.fxml**
- **AccountBalanceView.fxml**
- **DepositWithdrawView.fxml**
- **ActivityTrackerView.fxml**
- **WalletView.fxml**
- **ScenarioView.fxml**
- **ScenarioActivityView.fxml**
- **AssetsView.fxml**
- **InboxView.fxml**
- **SettingsView.fxml**
- **HelpView.fxml**
- **DebitDetailView.fxml**
- **SavingsDetailView.fxml**
- **CreditDetailView.fxml**

The smaller popup windows include: 

- **TransferAccountView.fxml** at 750x500
- **TransferContactListView.fxml** at 750x500
- **TransferContactConfirmView.fxml** at 750x500
- **PaymentView.fxml** at 675x530

### Image Assets

All PNG image files are stored in **src/main/resources/org/example/pathwayver1/images/**. This directory contains every visual element used throughout the application: screen backdrops, popup icons, account card graphics, toggle indicators, difficulty badges, notification indicators, inbox containers, asset item icons, and all other graphical elements. 

Every image was designed in Draw.io and exported as a transparent PNG to maintain a consistent visual identity while minimizing SceneBuilder's rendering workload.

### Data Persistence

When the application runs for the first time, it creates a **pathway_data** directory in the project root containing eight text files: 

- **users.txt** for user account credentials and profile data
- **accounts.txt** for banking account balances and configurations
- **wallet.txt** for wallet balances, transactions.txt for transaction history
- **notifications.txt** for inbox messages, assets.txt for owned items and subscription states
- **scenarios.txt** for scenario completion statuses
- **wallet_events.txt** for wallet activity logs

This directory is excluded from version control through the **.gitignore file**, as it contains user-specific session data that is generated at runtime.

### Module Configuration

The **module-info.java** file located in **src/main/java/** declares the application's module dependencies, granting access to JavaFX controls, FXML loading, and graphics rendering, and opens the application package for reflection based FXML injection.



## Financial Mechanics

Pathway Banking simulates a complete personal finance environmenet where every dollar is tracked and every decision carries weight. The financial systems within the application were designed to mirror real world banking principles, while remaining approachable enough for younger users to grasp through hands on interaction.

### Starting Balances

When a new user registers an account, they begin with $245 in their Wallet and $55 in a default Debit account, totaling $300 in available funds. This starting amount is intentionally established, enough to explore the application's features and unlock additional accounts, but limited enough that careless spending has noticeable consequences.

### Wallet

The Wallet acts as the user's virtual cash on hand, separate from all banking accounts. It is the central hub through which most money flows. Deposits into bank accounts are funded from the Wallet, withdrawals from the Debit account return money to the Wallet, and all scenario rewards are deposited directly into the Wallet. 

When a user carries pending debt from scenario outcomes, any money entering the Wallet is automatically applied toward that debt before becoming available. Reinforcing the reality that obligations can take priority over spending power.

### Debit Account

The Debit account is the user's default and permanent banking account. It cannot be deleted. Money can be deposited into it from the Wallet and withdrawn back to the Wallet freely. The Debit account serves as the primary funding source for account unlock down payments, contact transfers, and credit card payments. 

When the Debit balance drops below $50, the user receives a low balance warning notification in their Inbox. This warning triggers once per drop below the threshold and resets when the balance is restored above $50, allowing it to trigger again on future drops.

### Savings Account

Savings accounts are unlocked by making a $100 down payment from the Debit account. They are designed to encourage long term saving by making it intentionally inconvenient to withdraw funds. 

Users cannot withdraw directly from Savings, they must use the Transfer feature to move money to their Debit account. 
This transfer is subject to a tiered fee structure: the first 3 transfers are free, transfers 4 through 9 cost $3 each, and all transfers after that cost $6 each. 

If the user's Savings balance is less than or equal to the fee amount, the fee is waived entirely to prevent the user from being trapped with inaccessible funds. 

Users can deposit directly into Savings from the Wallet at any time without restriction. When deleting a Savings account, all remaining funds must be transferred to the Debit account first. The $100 down payment is not refunded, teaching users that financial decisions carry permanent costs.

### Credit Account
Credit accounts are unlocked by making a $150 down payment from the Debit account. 

Each Credit account comes with a $500 credit limit, representing the maximum amount of debt the user can carry on that card. The available credit is calculated as the credit limit minus the current balance owed. 

Charges are added to the Credit account automatically when scenario outcomes deduct money and the user's Wallet cannot cover the cost. The system charges the Credit card before touching the Debit account. 

Users can also choose to pay subscriptions using their Credit card, which adds to their balance owed.

### Minimum Payment and Late Fees

Each Credit account has a minimum payment requirement calculated as 10% of the balance owed, with a floor of $5. 

Every 20 minutes, a billing cycle check occurs. If the user has paid at least the minimum payment during that cycle, no late fee is applied and a notification confirms the minimum was met. 

If the minimum payment was not met and a balance is still owed, a $10 late fee is added to the balance owed and a notification alerts the user. 

Regardless of whether the minimum was met, the cycle resets, the minimum payment met flag clears and the due date pushes forward 20 minutes. This system teaches users that paying only the minimum keeps them safe from penalties but does not eliminate their debt, while failing to pay even the minimum results in compounding fees.

### Subscription Billing

Subscriptions acquired through scenario outcomes operate on a 20-minute billing cycle. 

When a subscription is first obtained, its status is set to Paid and a due date is set 20 minutes in the future. 

When the due date passes, the subscription transitions to Unpaid and a new 20-minute grace period begins. 

If the user pays the full subscription amount during this grace period, the status returns to Paid and the cycle resets. If the grace period expires while the subscription is still Unpaid, a $5 late fee is added to the subscription's recurring cost and the due date pushes forward another 20 minutes. 

This late fee stacks with each missed cycle, meaning a $5 subscription that goes unpaid for three cycles would cost $20 to pay off. 

However, once the user pays, the recurring cost resets to the original base amount. The late fees are a one time penalty, not a permanent price increase. Subscriptions cannot be cancelled while in an Unpaid state, forcing the user to settle their bill before they can walk away.

### Pending Debt

When a scenario deducts money and the user cannot cover the full amount through their Wallet, Credit cards, or Debit account, the remaining balance becomes pending debt. 

This debt sits in the background and is automatically paid off whenever new funds enter the Wallet: scenario rewards, random contact transfers, and withdrawals all pass through the Wallet and trigger the auto-payment check. 

Users can also manually pay down their pending debt from the Payment section in Deposit and Withdraw. When a user unlocks a new Credit account while carrying pending debt, the outstanding amount is automatically charged to the new card and a notification explains what happened, simulating how real world debt follows you regardless of which accounts you open.

### Deduction Priority

When a scenario outcome results in a financial loss, the application follows a strict deduction order. 

The Wallet is checked first, if it has enough funds, the full amount is taken from the Wallet. 

If the Wallet cannot cover the total, whatever is available is taken and the remaining balance moves to Credit cards. 

If multiple Credit cards exist, each is charged up to its available credit in the order they were unlocked, with partial charges splitting across cards when necessary. 

If all Credit cards are maxed out, the Debit account is tapped next. 

If the Debit account also cannot cover the remaining balance, whatever is left becomes pending debt. 

Transfer-related scenarios bypass this system entirely. The money only moves when the user completes the actual transfer, either immediately through the prompt or later through the Payment section.

### Random Contact Transfers

Every minute while the application is running, there is a chance that one of the user's nine simulated contacts will send them a random transfer between $5 and $25. These transfers are deposited directly into the Wallet and trigger the auto-payment check for pending debt. A notification appears in the Inbox identifying which contact sent the money and how much was received.
