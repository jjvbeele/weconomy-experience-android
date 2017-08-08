package com.teachwithapps.weconomyexperience.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.util.Returnable;

import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

public class FireDatabaseTransactions {

    private static final String TAG = FireDatabaseTransactions.class.getName();

    private FireDatabaseHelper fireDatabaseHelper;

    public FireDatabaseTransactions() {
        this.fireDatabaseHelper = new FireDatabaseHelper();
    }

    /**
     * Registers a new game in the firebase
     * The full game data is placed in "games"
     * The game key is placed in "hub"
     *
     * @param gameData
     */
    public void registerNewGame(GameData gameData) {
        String key = fireDatabaseHelper.pushRecord(
                "games",
                gameData
        );

        gameData.setId(key);

        fireDatabaseHelper.addRecord(
                "hub",
                key,
                key
        );
    }

    public void removeHubGame(GameData gameData) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "hub",
                        gameData.getId()
                }
        );
    }

    /**
     * Retrieves a list of hub game keys
     **/
    public void observeHubGames(Returnable<List<String>> callback) {
        fireDatabaseHelper.observeRecordArray(
                String.class,
                "hub",
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Error retrieving hub data", data.toException());
                    }
                }, true);
    }

    /**
     * Retrieves game data from "games" by game key
     *
     * @param gameKey
     * @param callback
     */
    public void observeGame(String gameKey, Returnable<GameData> callback) {
        fireDatabaseHelper.observeRecordFireData(
                GameData.class,
                new String[]{"games", gameKey},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        if(error != null) {
                            Log.e(TAG, "Error retrieving game data", error.toException());

                        } else {
                            Log.e(TAG, "Error retrieving game data, unknown error");
                        }
                    }
                },
                false
        );
    }
}
