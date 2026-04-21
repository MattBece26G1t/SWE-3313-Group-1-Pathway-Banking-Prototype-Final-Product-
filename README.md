# SWE-3313-Group-1-Pathway-Banking-Prototype-Final-Product-

## Relevant Course Information

Kennesaw State University

Intro to Software Engineering Section W04 Spring Semester 2026 CO 

Final-Product Deliverable (Deliverable 6)

Group 1: Matthew Becerra, Rumel Ahmed, Leandro Cherulli, Bradley Crasto, Kentrel Brown

Product Name: Pathway Banking – Desktop Financial Learning Application 

Version 1.0

Release Date: 4/16/2026

## Performance Adjustments in SceneBuilder (For Team Development and/or Running the Application)

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
