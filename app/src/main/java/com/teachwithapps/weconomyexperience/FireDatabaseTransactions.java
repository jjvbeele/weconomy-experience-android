package com.teachwithapps.weconomyexperience;

import com.google.firebase.database.DatabaseError;
import com.teachwithapps.weconomyexperience.firebase.FireDatabaseHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.GoalData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
import com.teachwithapps.weconomyexperience.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

public class FireDatabaseTransactions {

    private static final String TAG = FireDatabaseTransactions.class.getName();

    private FireDatabaseHelper fireDatabaseHelper;

    public enum LoadState {
        LOADING_STARTED,
        LOADING_DONE
    }

    public interface OnLoadingListener {
        void onLoadingChanged(Returnable<?> callback, LoadState loadState);
    }

    private WeakReference<OnLoadingListener> onLoadingListenerRef;


    public FireDatabaseTransactions() {
        this.fireDatabaseHelper = new FireDatabaseHelper();
    }

    /**
     * Listen to loading states
     *
     * @param onLoadingListener
     */
    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListenerRef = new WeakReference<>(onLoadingListener);
    }

    /**
     * Update loading states
     *
     * @param loadState
     */
    private void updateLoadingState(Returnable<?> callback, LoadState loadState) {
        if (onLoadingListenerRef != null) {
            OnLoadingListener onLoadingListener = onLoadingListenerRef.get();
            if (onLoadingListener != null) {
                onLoadingListener.onLoadingChanged(callback, loadState);
            }
        }
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
    public void observeHubGames(final ReturnableChange<String> callback) {
        updateLoadingState(callback, LoadState.LOADING_STARTED);
        fireDatabaseHelper.observeChild(
                String.class,
                new String[]{"hub"},
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
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
    public void getGameData(String gameId, final Returnable<GameData> callback) {
        updateLoadingState(callback, LoadState.LOADING_STARTED);
        fireDatabaseHelper.getRecord(
                GameData.class,
                new String[]{"games", gameId},
                new Returnable<GameData>() {
                    @Override
                    public void onResult(GameData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onResult(data);
                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError error) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
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

    /**
     * Observes changes in the schedule
     * When scheduled instructions are added or changed, the retrieved scheduled instruction is
     * bound to the instruction data from the instruction library
     * @param instructionLibraryKey
     * @param locationArray
     * @param callback
     */
    public void observeSchedule(
            final String instructionLibraryKey,
            final String[] locationArray,
            final ReturnableChange<ScheduledInstructionData> callback) {

        updateLoadingState(callback, LoadState.LOADING_STARTED);

        fireDatabaseHelper.observeChild(
                ScheduledInstructionData.class,
                locationArray,
                new ReturnableChange<ScheduledInstructionData>() {
                    @Override
                    public void onChildAdded(ScheduledInstructionData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        bindInstructionToScheduledInstruction(instructionLibraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildAdded(data);
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(ScheduledInstructionData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        bindInstructionToScheduledInstruction(instructionLibraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildChanged(data);
                            }
                        });
                    }

                    @Override
                    public void onChildRemoved(ScheduledInstructionData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onChildRemoved(data);
                    }

                    @Override
                    public void onChildMoved(ScheduledInstructionData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        bindInstructionToScheduledInstruction(instructionLibraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildMoved(data);
                            }
                        });
                    }

                    @Override
                    public void onResult(ScheduledInstructionData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        bindInstructionToScheduledInstruction(instructionLibraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onResult(data);
                            }
                        });
                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        Log.e(TAG, "Can't observe schedule", data.toException());
                    }
                }
        );
    }

    /**
     * Bind the instruction data to the scheduled instruction
     *
     * @param data
     * @param callback
     */
    private void bindInstructionToScheduledInstruction(
            final String instructionLibraryKey,
            final ScheduledInstructionData data,
            final Returnable<ScheduledInstructionData> callback) {
        //get instructiondata by key and add to the scheduledInstruction datamap
        getInstructionFromLibrary(
                instructionLibraryKey,
                data.getInstructionKey(),
                new Returnable<InstructionData>() {
                    @Override
                    public void onResult(InstructionData instructionData) {
                        data.bindInstructionData(instructionData);
                        callback.onResult(data);
                    }
                });
    }

    public void getPlayersInGame(String gameId, final ReturnableChange<PlayerData> callback) {
        updateLoadingState(callback, LoadState.LOADING_STARTED);
        fireDatabaseHelper.observeChild(
                PlayerData.class,
                new String[]{
                        "games",
                        gameId,
                        "players"
                },
                new ReturnableChange<PlayerData>() {
                    @Override
                    public void onChildAdded(PlayerData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onChildAdded(data);
                    }

                    @Override
                    public void onChildChanged(PlayerData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onChildChanged(data);

                    }

                    @Override
                    public void onChildRemoved(PlayerData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onChildRemoved(data);

                    }

                    @Override
                    public void onChildMoved(PlayerData data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onChildMoved(data);

                    }

                    @Override
                    public void onResult(PlayerData data) {

                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
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

    public void getPlayerById(String gameId, String playerId, Returnable<PlayerData> onReturn) {
        fireDatabaseHelper.getRecord(
                PlayerData.class,
                new String[]{"games", gameId, "players", playerId},
                onReturn,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Failed to retrieve player from firebase", data.toException());
                    }
                }
        );
    }

    public void getGoalsInGame(final String gameId, final Returnable<List<GoalData>> onReturn) {
        getGoalCount(gameId, new Returnable<Long>() {
            @Override
            public void onResult(Long data) {
                if (data > 0) {
                    fireDatabaseHelper.getRecordsList(
                            GoalData.class,
                            new String[]{
                                    "games",
                                    gameId,
                                    "goals"
                            },
                            onReturn,
                            new Returnable<DatabaseError>() {
                                @Override
                                public void onResult(DatabaseError data) {
                                    Log.e(TAG, "Can't retrieve goal list", data.toException());
                                }
                            }
                    );
                } else {
                    onReturn.onResult(new ArrayList<GoalData>());
                }
            }
        });
    }

    public void addGoalToGame(String gameId, String goalText) {
        fireDatabaseHelper.pushRecord(
                "goal_id_",
                new String[]{
                        "games",
                        gameId,
                        "goals"
                },
                new GoalData(goalText)
        );
    }

    public void getGoalCount(String gameId, Returnable<Long> returnable) {
        fireDatabaseHelper.getChildCount(
                new String[]{
                        "games",
                        gameId,
                        "goals"
                },
                returnable,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Unable to fetch goal count", data.toException());
                    }
                }
        );
    }

    /**
     * Reschedules an instruction on the schedule from one day to another.
     * The instruction is always inserted at the top, but the order is not maintained in the firebase
     * Consecutive calls may put the instruction in a different order
     *
     * @param gameId
     * @param scheduledInstructionData
     */
    public void rescheduleScheduledInstruction(String gameId, ScheduledInstructionData scheduledInstructionData) {
        removeScheduledInstruction(gameId, scheduledInstructionData);
        updateScheduledInstruction(gameId, scheduledInstructionData);
    }

    /**
     * Returns true or false wether or not the player is registered as an administrator
     *
     * @param role
     * @param userId
     * @param callback
     */
    public void verifyRole(final String role, final String userId, final Returnable<Boolean> callback) {
        updateLoadingState(callback, LoadState.LOADING_STARTED);
        fireDatabaseHelper.getRecord(
                Boolean.class,
                new String[]{
                        "secured",
                        userId,
                        role
                },
                new Returnable<Boolean>() {
                    @Override
                    public void onResult(Boolean data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        callback.onResult(data);
                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                        Log.e(TAG, "Can't fetch security role", data.toException());
                    }
                });
    }
}
