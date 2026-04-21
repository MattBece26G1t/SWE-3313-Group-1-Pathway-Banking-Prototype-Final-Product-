# SWE-3313-Group-1-Pathway-Banking-Prototype-Final-Product-
Intro to Software Engineering Section W04 Spring Semester 2026 CO 

Final-Product Deliverable (Deliverable 6)

Group 1: Matthew Becerra, Rumel Ahmed, Leandro Cherulli, Bradley Crasto, Kentrel Brown

Product Name: Pathway Banking – Desktop Financial Learning Application 

Version 1.0

Release Date: 4/16/2026

## Performance adjustments in SceneBuilder (for team development and/or running the application)

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
