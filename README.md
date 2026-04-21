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
