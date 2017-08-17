package com.teachwithapps.weconomyexperience;

import com.google.firebase.database.DatabaseError;
import com.teachwithapps.weconomyexperience.firebase.FireDatabaseHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
import com.teachwithapps.weconomyexperience.util.Log;

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
                new String[]{"games"},
                gameData
        );

        gameData.setId(id);

        fireDatabaseHelper.addRecord(
                new String[]{"hub"},
                id,
                id
        );

        //No custom instructionlibrary? Create a copy of the default library
        if (gameData.getInstructionLibraryKey() == null) {
            //push the new library and obtain the firebase key (location)
            final String instructionLibraryKey = fireDatabaseHelper.pushRecord(
                    "library_id_",
                    new String[]{"instruction_libraries"},
                    gameData.getId()
            );

            //get default library and push it in the newly pushed library using the key
            fireDatabaseHelper.getRecordsAsync(
                    InstructionData.class,
                    new String[]{
                            "insruction_libraries",
                            "default_library"
                    },
                    new Returnable<InstructionData>() {
                        @Override
                        public void onResult(InstructionData data) {
                            fireDatabaseHelper.addRecord(
                                    new String[]{
                                            "instruction_libraries",
                                            instructionLibraryKey
                                    },
                                    data.getId(),
                                    data
                            );
                        }
                    },
                    new Returnable<DatabaseError>() {
                        @Override
                        public void onResult(DatabaseError data) {
                            Log.e(TAG, "Can't copy default library", data.toException());
                        }
                    }
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
    public void getGameData(String gameId, Returnable<GameData> callback) {
        fireDatabaseHelper.getRecord(
                GameData.class,
                new String[]{"games", gameId},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, "Error retrieving game data", error.toException());

                        } else {
                            Log.d(TAG, "Error retrieving game data, unknown error");
                        }
                    }
                }
        );
    }

    public void addInstructionToLibrary(String instructionLibraryName, InstructionData instructionData) {
        fireDatabaseHelper.pushRecord(
                "library_id_",
                new String[]{
                        "instruction_libraries",
                        instructionLibraryName
                },
                instructionData
        );
    }

    public void getInstructionsFromLibrary(
            String instructionLibraryKey,
            Returnable<List<InstructionData>> callback) {
        fireDatabaseHelper.getRecordsList(
                InstructionData.class,
                new String[]{"instruction_libraries", instructionLibraryKey},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        if (error != null) {
                            Log.e(TAG, "Error retrieving game data", error.toException());

                        } else {
                            Log.d(TAG, "Error retrieving game data, unknown error");
                        }
                    }
                }
        );
    }

    public void registerInstructionToSchedule(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.pushRecord(
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
        fireDatabaseHelper.observeChild(
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

    public void getPlayersInGame(String gameId, Returnable<List<PlayerData>> onReturn) {
        fireDatabaseHelper.getRecordsList(
                PlayerData.class,
                new String[]{
                        "games",
                        gameId,
                        "players"
                },
                onReturn,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve player list", data.toException());
                    }
                }
        );
    }

    public void getInstructionFromLibrary(String instructionLibraryKey, String instructionKey, Returnable<InstructionData> onReturnSuccess) {
        fireDatabaseHelper.getRecord(
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
                }
        );
    }

    public void updateScheduledInstruction(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "game_schedules",
                        gameId
                },
                scheduledInstructionData.getId(),
                scheduledInstructionData
        );
    }

    public void removeScheduledInstruction(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "game_schedules",
                        gameId,
                        scheduledInstructionData.getId()
                });
    }

    public void getPlayerById(String gameId, String playerId, Returnable<PlayerData> callback) {
            fireDatabaseHelper.getRecord(
                    PlayerData.class,
                    new String[]{"games", gameId, "players", playerId},
                    callback,
                    new Returnable<DatabaseError>() {
                        @Override
                        public void onResult(DatabaseError data) {
                            Log.e(TAG, "Failed to retrieve player from firebase", data.toException());
                        }
                    }
            );
    }
}
