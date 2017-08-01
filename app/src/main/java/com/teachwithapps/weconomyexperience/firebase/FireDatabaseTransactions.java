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

    public void registerNewGame(GameData gameData) {
        String key = fireDatabaseHelper.pushRecord(
                new String[]{"hub"},
                gameData
        );

        gameData.setId(key);
    }

    public void removeHubGame(GameData gameData) {
        fireDatabaseHelper.removeRecord(
                new String[]{"hub"},
                gameData.getId()
        );
    }

    public void queryHubGames(Returnable<List<GameData>> callback) {

        fireDatabaseHelper.getRecordArray(
                GameData.class,
                new String[]{"hub"},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Error retrieving hub data", data.toException());
                    }
                });
    }
}
