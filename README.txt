
Academic Project Developed by:
  Gonçalo Correia
  João Custódio
  Diogo Leonardo
  Francisco Bastos

This project's main goal was to develop a mobile app that allows users to transfer "money" between each other, while also keeping track and organizing everything with Jira and Bitbucket by sprints. The team of four was divided into two teams of two, and each team had the responsibility to complete one sprint per week. At the end of each week, the team would organize the work done and plan the upcoming sprints.

Firebase Configuration and Keys
For the application to work correctly, you need to configure the Firebase Cloud Messaging (FCM) server key as an environment variable. Follow the steps below to set it up:

Steps to Configure the Environment Variable
1. Obtain the FCM Server Key:
  Go to the Firebase Console.
  In your project, navigate to Cloud Messaging settings.
  Copy the FCM Server Key.
2. Create an Environment Variable:
  For Windows:
    Open the "Start" menu and type "Environment Variables".
    Click on "Edit the system environment variables".
    Under "User variables" or "System variables", click on New.
    Name the variable: FCM_SERVER_KEY
    Paste the FCM key you copied as the value of the variable.
  For macOS/Linux:

  Open a terminal and add the variable to your ~/.bash_profile (or ~/.zshrc if you're using Zsh).
    export FCM_SERVER_KEY="your_fcm_key_here"
  After adding the line, run:
    source ~/.bash_profile  # or source ~/.zshrc

4. Run the Project:
  Now that the environment variable is set, you can run the application normally in Android Studio.
  The app will automatically read the environment variable during runtime.
