# weconomy-experience-android
android app for http://guts4roses.org/simulation/

## Creating a separate project
In order to use this code in your own project, apply the following steps in order.
1. Fork the project to your own github account
2. Clone the project to your local drive: `git clone git@github.com:your_account/your_forked_project.git`
3. Open the project in Android Studio
4. Rename every occurence of the package name (org.guts4roses.weconomyexperience) with your own package name
5. Set up your ssh keys as described by github

After setting up the project, you can continue to set up firebase for development and production.

## Setting up Firebase
This project is set up to allow a separate firebase for development and production. If you wish to use only one firebase, you can remove the following line from your gradle app file: `applicationId "com.teachwithapps.debug.weconomyexperience"`, or change the applicationId to match your own development firebase database.

To set up firebase, apply the following steps in order.
1. Create a new firebase project, using the applicationId (or package name) you set up when creating the project
2. If you want a separate development firebase, create it under a different account
3. Generate a sha1 key in your project for debug and production, and apply it to your firebase(s)
4. Download the google-services.json files from your firebases
5. Place the google-services.json file in the project /app/src/dev and /app/src/prod folders, with the development firebase under /dev and the production firebase under /prod
6. Import the bare-firebase.json file into your firebases. This is the bare database setup that the app needs to function.
7. In the secured table, add your userId and add an admin record with string "true", to give yourself admin access

## Short code documentation

### Firebase communication
The communication with firebase involves three important classes: `FireDatabaseHelper`, `FireDatabaseTransactions` and any `FireData` model classes. `FireDatabaseHelper` contains some generic abstract methods that are re-used often by `FireDatabaseTransactions`, which is a concrete implementation of specific database transactions that the app needs. Examples include observing changes in player, instruction and 
goal data. The simples method call stack looks like this:
```
FireDtabaseTransactions fdt = new FireDatabaseTransactions();
fdt.observePlayers(callback);
```
Which calls an abstract method observeChildren in `FireDatabaseHelper`, and passes the callback further on. FireDatabaseHelper makes the connection with the firebase, and when a response is received, it passes back to `FireDatabaseTransactions`, which handles the error or passes the result back to the callback.

Most firebase transactions are handled by an observer pattern, so that any change will immediately have an effect on all connected clients, and data remains as persistent as possible. All changed data are reflected into the accompanying model classes, which all exted the `FireData` class. This class handles the id (or name) of the model, and couples it to the key that marks the dataset in the json.

Currently there are no locks, so when one client changes the data while another is in the process of changing it, and the last client pushes the change, the changes of the first client will be undone.

### Model classes
The following classes are the model classes that reflect the database values in the firebase.
* GameData, holds meta-data about the game such as which library and the version number for updates that require a game-breaking change
* GoalData, holds data about a goal that a player created
* InstructionData, holds data about an instruction coming from the library, such as what kind of input it needs and output it generates
* PlayerData, holds data about a player, such as their name and a unique color
* ScheduledInstructionData, holds data about which players are scheduled to an instruction. The reference to the instruction is tied in post-firebase transaction
* SelectedGoalData, holds data about which player selects which goal to be met for the game, and whether or not it is realised. The reference to the goal and player is tied in post-firebase transaction



