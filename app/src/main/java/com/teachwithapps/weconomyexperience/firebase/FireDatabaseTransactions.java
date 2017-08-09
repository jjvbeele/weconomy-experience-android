package com.teachwithapps.weconomyexperience.firebase;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
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
     * The game id is placed in "hub"
     *
     * @param gameData
     */
    public void registerNewGame(GameData gameData) {
        String id = fireDatabaseHelper.pushRecord(
                "game_id_",
                "games",
                gameData
        );

        gameData.setId(id);

        fireDatabaseHelper.pushRecord(
                "hub_id_",
                "hub",
                id
        );

        //No custom library? Create a copy of the default library
        if (gameData.getInstructionLibraryKey() == null) {
            final String instructionLibraryKey = fireDatabaseHelper.pushRecord(
                    "library_id_",
                    "instruction_libraries",
                    gameData.getId()
            );

            fireDatabaseHelper.observeRecordFireDataArray(
                    InstructionData.class,
                    new String[]{
                            "insruction_libraries",
                            "default_library"
                    },
                    new Returnable<List<InstructionData>>() {
                        @Override
                        public void onResult(List<InstructionData> dataList) {
                            for (InstructionData instructionData : dataList) {
                                fireDatabaseHelper.addRecord(
                                        new String[]{
                                                "instruction_libraries",
                                                instructionLibraryKey
                                        },
                                        instructionData.getId(),
                                        instructionData
                                );
                            }
                        }
                    },
                    new Returnable<DatabaseError>() {
                        @Override
                        public void onResult(DatabaseError data) {
                            Log.e(TAG, "Can't copy default library", data.toException());
                        }
                    },
                    false
            );
        }
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
     * Retrieves a list of hub game ids
     **/
    public void observeHubGames(ReturnableChange<String> callback) {
        fireDatabaseHelper.observeChild(
                String.class,
                new String[]{"hub"},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Error retrieving hub data", data.toException());
                    }
                });
    }

    /**
     * Retrieves game data from "games" by game id
     *
     * @param gameId
     * @param callback
     */
    public void observeGame(String gameId, Returnable<GameData> callback) {
        fireDatabaseHelper.observeRecordFireData(
                GameData.class,
                new String[]{"games", gameId},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, "Error retrieving game data", error.toException());

                        } else {
                            Log.e(TAG, "Error retrieving game data, unknown error");
                        }
                    }
                },
                false
        );
    }

    public void addInstructionToLibrary(String instructionLibraryName, InstructionData instructionData) {
        fireDatabaseHelper.pushFireDataRecord(
                "library_id_",
                new String[]{
                        "instruction_libraries",
                        instructionLibraryName
                },
                instructionData
        );
    }

    public void observeInstructionLibrary(String instructionLibraryKey, Returnable<List<InstructionData>> callback) {
        fireDatabaseHelper.observeRecordFireDataArray(
                InstructionData.class,
                new String[]{"instruction_libraries", instructionLibraryKey},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, "Error retrieving game data", error.toException());

                        } else {
                            Log.e(TAG, "Error retrieving game data, unknown error");
                        }
                    }
                },
                true
        );
    }

    public void registerInstructionToSchedule(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.pushFireDataRecord(
                "scheduled_instruction_id_",
                new String[]{
                        "game_schedules",
                        gameId
                },
                scheduledInstructionData
        );
    }

    public void registerPlayerToGame(String gameId, PlayerData playerData) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "games",
                        gameId,
                        "players"
                },
                playerData.getId(),
                playerData
        );
    }

    public void observeSchedule(String[] locationArray,
                                ReturnableChange<ScheduledInstructionData> onReturnChange) {
        fireDatabaseHelper.observeChildFireData(
                ScheduledInstructionData.class,
                locationArray,
                onReturnChange,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't observe schedule", data.toException());
                    }
                }
        );
    }

    public void observePlayersInGame(String gameId, Returnable<List<PlayerData>> onReturnChange) {
        fireDatabaseHelper.observeRecordFireDataArray(
                PlayerData.class,
                new String[]{
                        "games",
                        gameId,
                        "players"
                },
                onReturnChange,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't observe schedule", data.toException());
                    }
                },
                false
        );
    }

    public void getInstructionFromLibrary(String instructionLibraryKey, String instructionKey, Returnable<InstructionData> onReturnSuccess) {
        fireDatabaseHelper.observeRecord(
                InstructionData.class,
                new String[]{
                        "instruction_libraries",
                        instructionLibraryKey,
                        instructionKey
                },
                onReturnSuccess,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve instruction from library", data.toException());
                    }
                },
                false
        );
    }

    public void updateScheduledInstruction(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.setFireDataRecord(
                new String[]{
                        "game_schedules",
                        gameId
                },
                scheduledInstructionData
        );
    }
}
