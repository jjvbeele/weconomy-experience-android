package org.guts4roses.weconomyexperience;

import com.google.firebase.database.DatabaseError;

import org.guts4roses.weconomyexperience.firebase.FireDatabaseHelper;
import org.guts4roses.weconomyexperience.firebase.util.Returnable;
import org.guts4roses.weconomyexperience.firebase.util.ReturnableChange;
import org.guts4roses.weconomyexperience.model.GameData;
import org.guts4roses.weconomyexperience.model.GoalData;
import org.guts4roses.weconomyexperience.model.InstructionData;
import org.guts4roses.weconomyexperience.model.PlayerData;
import org.guts4roses.weconomyexperience.model.ScheduledInstructionData;
import org.guts4roses.weconomyexperience.model.SelectedGoalData;
import org.guts4roses.weconomyexperience.util.Log;

import java.lang.ref.WeakReference;
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

    public void unregister() {
        onLoadingListenerRef.clear();
        fireDatabaseHelper.unregisterAllListeners();
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
        if (gameData.getLibraryKey() == null) {
            //push the new library and obtain the firebase key (location)
            final String instructionLibrary = fireDatabaseHelper.pushRecord(
                    "library_id_",
                    new String[]{
                            "libraries"
                    },
                    gameData.getId()
            );

            //get default library and push it in the newly pushed library using the key
            fireDatabaseHelper.getRecordsAsync(
                    InstructionData.class,
                    new String[]{
                            "libraries",
                            "default_library",
                            "instructions"
                    },
                    new Returnable<InstructionData>() {
                        @Override
                        public void onResult(InstructionData data) {
                            fireDatabaseHelper.addRecord(
                                    new String[]{
                                            "libraries",
                                            instructionLibrary,
                                            "instructions"
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

    /**
     * Adds a new instruction to the given library
     * @param instructionLibraryName library to add the instruction to
     * @param instructionData instruction to add to the library
     */
    public void addInstructionToLibrary(String instructionLibraryName, InstructionData instructionData) {
        fireDatabaseHelper.pushRecord(
                "library_id_",
                new String[]{
                        "libraries",
                        instructionLibraryName,
                        "instructions"
                },
                instructionData
        );
    }

    /**
     * Get all instructions from the given library
     * @param libraryKey library from which to retrieve instructions
     * @param callback callback in which the list of instructions is returned
     */
    public void getInstructionsFromLibrary(
            String libraryKey,
            Returnable<List<InstructionData>> callback) {
        fireDatabaseHelper.getRecordsList(
                InstructionData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "instructions"
                },
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

    /**
     * Registers a new instruction to the schedule
     * @param gameId game to which to add the new instruction
     * @param scheduledInstructionData scheduledinstruction to add to the game's schedule
     */
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

    /**
     * Register a new player to the game
     * @param gameId game to which to register the new player
     * @param playerData player to register to the game
     */
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
     *
     * @param libraryKey
     * @param locationArray
     * @param callback
     */
    public void observeSchedule(
            final String libraryKey,
            final String[] locationArray,
            final ReturnableChange<ScheduledInstructionData> callback) {

        updateLoadingState(callback, LoadState.LOADING_STARTED);

        fireDatabaseHelper.observeChild(
                ScheduledInstructionData.class,
                locationArray,
                new ReturnableChange<ScheduledInstructionData>() {
                    @Override
                    public void onChildAdded(ScheduledInstructionData data) {
                        bindInstructionToScheduledInstruction(libraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildAdded(data);
                                updateLoadingState(callback, LoadState.LOADING_DONE);
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(ScheduledInstructionData data) {
                        bindInstructionToScheduledInstruction(libraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildChanged(data);
                                updateLoadingState(callback, LoadState.LOADING_DONE);
                            }
                        });
                    }

                    @Override
                    public void onChildRemoved(ScheduledInstructionData data) {
                        callback.onChildRemoved(data);
                        updateLoadingState(callback, LoadState.LOADING_DONE);
                    }

                    @Override
                    public void onChildMoved(ScheduledInstructionData data) {
                        bindInstructionToScheduledInstruction(libraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onChildMoved(data);
                                updateLoadingState(callback, LoadState.LOADING_DONE);
                            }
                        });
                    }

                    @Override
                    public void onResult(ScheduledInstructionData data) {
                        bindInstructionToScheduledInstruction(libraryKey, data, new Returnable<ScheduledInstructionData>() {
                            @Override
                            public void onResult(ScheduledInstructionData data) {
                                callback.onResult(data);
                                updateLoadingState(callback, LoadState.LOADING_DONE);
                            }
                        });
                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't observe schedule", data.toException());
                        updateLoadingState(callback, LoadState.LOADING_DONE);
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
            final String libraryKey,
            final ScheduledInstructionData data,
            final Returnable<ScheduledInstructionData> callback) {
        //get instructiondata by key and add to the scheduledInstruction datamap
        getInstructionFromLibrary(
                libraryKey,
                data.getInstructionKey(),
                new Returnable<InstructionData>() {
                    @Override
                    public void onResult(InstructionData instructionData) {
                        data.bindInstructionData(instructionData);
                        callback.onResult(data);
                    }
                });
    }

    /**
     * Observe any changes in player data for a game
     * @param gameId game from which to observe the players
     * @param callback callback to handle any changes in player data
     */
    public void observePlayersInGame(String gameId, final ReturnableChange<PlayerData> callback) {
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

    /**
     * Retrieve a specific instructions from a given library
     * @param libraryKey library from which to retrieve instructions
     * @param instructionKey instruction to retrieve
     * @param onReturnSuccess callback to receive the instruction data
     */
    public void getInstructionFromLibrary(String libraryKey, String instructionKey, Returnable<InstructionData> onReturnSuccess) {
        fireDatabaseHelper.getRecord(
                InstructionData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "instructions",
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

    /**
     * Updates a change in a scheduledinstruction
     * @param gameId game in which to update the scheduledinstruction
     * @param scheduledInstructionData scheduledinstruction that has changed and needs to push its update
     */
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

    /**
     * remove a scheduledinstruction from a game
     * @param gameId game from which to remove a scheduled instruction
     * @param scheduledInstructionData scheduledinstruction to remove
     */
    public void removeScheduledInstruction(String gameId, ScheduledInstructionData scheduledInstructionData) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "game_schedules",
                        gameId,
                        scheduledInstructionData.getId()
                });
    }

    /**
     * Add instruction to the list of available instructions, which can be picked by the players
     * @param gameId game in which to make an instruction available
     * @param instructionData instruction to make available to pick
     */
    public void addInstructionToAvailableInstructions(
            String gameId,
            InstructionData instructionData) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "game_instructions",
                        gameId,
                        "available_instructions"
                },
                instructionData.getId(),
                instructionData.getId()
        );
    }

    /**
     * Removes availability of an instruction
     * @param gameId game in which to remove availability of an instruction
     * @param instructionData instruction to make unavailable for players
     */
    public void removeInstructionFromAvailableInstructions(
            String gameId,
            InstructionData instructionData) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "game_instructions",
                        gameId,
                        "available_instructions",
                        instructionData.getId()
                }
        );
    }

    /**
     * Observe any changes in availability of instructions
     * @param gameId game in which to observe availability of instructions
     * @param libraryKey library from which to retrieve instruction data if it changed
     * @param callback callback in which to pass the changed instruction
     */
    public void observeAvailableInstructionsInGame(
            final String gameId,
            final String libraryKey,
            final ReturnableChange<InstructionData> callback) {
        fireDatabaseHelper.observeChild(
                String.class,
                new String[]{
                        "game_instructions",
                        gameId,
                        "available_instructions"
                },
                new ReturnableChange<String>() {
                    @Override
                    public void onChildAdded(final String instructionId) {
                        getInstructionFromLibraryById(
                                libraryKey,
                                instructionId,
                                new Returnable<InstructionData>() {
                                    @Override
                                    public void onResult(InstructionData data) {
                                        callback.onChildAdded(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildChanged(final String instructionId) {
                        getInstructionFromLibraryById(
                                libraryKey,
                                instructionId,
                                new Returnable<InstructionData>() {
                                    @Override
                                    public void onResult(InstructionData data) {
                                        callback.onChildChanged(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildRemoved(final String instructionId) {
                        getInstructionFromLibraryById(
                                libraryKey,
                                instructionId,
                                new Returnable<InstructionData>() {
                                    @Override
                                    public void onResult(InstructionData data) {
                                        callback.onChildRemoved(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildMoved(String goalId) {

                    }

                    @Override
                    public void onResult(String goalId) {

                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve goal list", data.toException());
                    }
                }
        );
    }

    /**
     * Retrieve an instruction from a library by its id
     * @param libraryKey library from which to retrieve an instruction
     * @param instructionId instruction id to search for
     * @param callback callback to pass in the result
     */
    public void getInstructionFromLibraryById(final String libraryKey, final String instructionId, final Returnable<InstructionData> callback) {
        fireDatabaseHelper.getRecord(
                InstructionData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "instructions",
                        instructionId
                },
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Unable to retrieve goal data", data.toException());
                    }
                }
        );
    }

    /**
     * Observe any changes in instructions in a library
     * @param libraryKey library to observe
     * @param callback callback in which to receive changes
     */
    public void observeInstructionsFromLibrary(String libraryKey, ReturnableChange<InstructionData> callback) {
        fireDatabaseHelper.observeChild(
                InstructionData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "instructions"
                },
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve library goals", data.toException());
                    }
                }
        );
    }

    /**
     * Retrieve a player by id
     * @param gameId game in which to retrieve a player
     * @param playerId id to search for
     * @param onReturn callback to receive the result in
     */
    public void getPlayerById(String gameId, String playerId, Returnable<PlayerData> onReturn) {
        if (!playerId.startsWith("player_id_")) {
            playerId = "player_id_" + playerId;
        }
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

    /**
     * Returns the amount of goals currently set by players in a game
     * @param gameId game in which to count set goals
     * @param returnable calllback in which to receive the result
     */
    public void getSelectedGoalCount(String gameId, Returnable<Long> returnable) {
        fireDatabaseHelper.getChildCount(
                new String[]{
                        "game_goals",
                        gameId,
                        "selected_goals"
                },
                returnable,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve selected goals", data.toException());
                    }
                }
        );
    }

    /**
     * Removes a goal from the set goals by players
     * @param gameId game in which to remove goal
     * @param selectedGoalId id of the goal to remove from selected goals
     */
    public void removeSelectedGoal(String gameId, String selectedGoalId) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "game_goals",
                        gameId,
                        "selected_goals",
                        selectedGoalId
                }
        );
    }

    /**
     * Makes a goal available to select for all players
     * @param gameId game in which to make a goal available
     * @param goalData goal to make available
     */
    public void addGoalToAvailableGoals(
            String gameId,
            GoalData goalData) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "game_goals",
                        gameId,
                        "available_goals"
                },
                goalData.getId(),
                goalData.getId()
        );
    }

    /**
     * Removes a goal from available goals for the players
     * @param gameId game in which to make a goal unavailable
     * @param goalData goal to make unavailable for players
     */
    public void removeGoalFromAvailableGoals(
            String gameId,
            GoalData goalData) {
        fireDatabaseHelper.removeRecord(
                new String[]{
                        "game_goals",
                        gameId,
                        "available_goals",
                        goalData.getId()
                }
        );
    }

    /**
     * Observes any changes in a goal
     * @param gameId game in which to observe goals
     * @param libraryKey library from which to retrieve a goal that changed
     * @param callback callback to receive the changed goal
     */
    public void observeAvailableGoalsInGame(
            final String gameId,
            final String libraryKey,
            final ReturnableChange<GoalData> callback) {
        fireDatabaseHelper.observeChild(
                String.class,
                new String[]{
                        "game_goals",
                        gameId,
                        "available_goals"
                },
                new ReturnableChange<String>() {
                    @Override
                    public void onChildAdded(final String goalId) {
                        getGoalFromLibraryById(
                                libraryKey,
                                goalId,
                                new Returnable<GoalData>() {
                                    @Override
                                    public void onResult(GoalData data) {
                                        callback.onChildAdded(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildChanged(final String goalId) {
                        getGoalFromLibraryById(
                                libraryKey,
                                goalId,
                                new Returnable<GoalData>() {
                                    @Override
                                    public void onResult(GoalData data) {
                                        callback.onChildChanged(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildRemoved(final String goalId) {
                        getGoalFromLibraryById(
                                libraryKey,
                                goalId,
                                new Returnable<GoalData>() {
                                    @Override
                                    public void onResult(GoalData data) {
                                        callback.onChildRemoved(data);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onChildMoved(String goalId) {

                    }

                    @Override
                    public void onResult(String goalId) {

                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve goal list", data.toException());
                    }
                }
        );
    }

    /**
     * Observes changes in goal data in a library
     * @param libraryKey library to observe
     * @param callback callback in which to receive changed goal data
     */
    public void observeGoalsFromLibrary(String libraryKey, ReturnableChange<GoalData> callback) {
        fireDatabaseHelper.observeChild(
                GoalData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "goals"
                },
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve library goals", data.toException());
                    }
                }
        );
    }

    /**
     * Updates a goal that was changed
     * @param gameId game in which this change should be updated
     * @param selectedGoalData goal that was changed
     */
    public void updateSelectedGoal(String gameId, SelectedGoalData selectedGoalData) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "game_goals",
                        gameId,
                        "selected_goals"
                },
                selectedGoalData.getId(),
                selectedGoalData
        );
    }

    /**
     * Returns a list of players in game
     * @param gameId game from which to retrieve player list
     * @param onReturn callback in which to receive player list
     */
    public void getPlayersInGame(final String gameId, final Returnable<List<PlayerData>> onReturn) {
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
                        Log.e(TAG, "Unable to read players", data.toException());
                    }
                }
        );
    }

    /**
     * Count the players in a game
     * @param gameId game in which to count players
     * @param onReturn callback to receive the result
     */
    public void getPlayerCount(final String gameId, final Returnable<Long> onReturn) {
        fireDatabaseHelper.getChildCount(
                new String[]{
                        "games",
                        gameId,
                        "players"
                },
                onReturn,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve player count", data.toException());
                    }
                }
        );
    }

    /**
     * Observe changes in selected goals by players
     * @param gameId game in which to observe changes in selected goals
     * @param libraryKey library from which to retrieve goal data that changed
     * @param callback callback to receive changed goal data
     */
    public void observeSelectedGoalsInGame(
            final String gameId,
            final String libraryKey,
            final ReturnableChange<SelectedGoalData> callback) {

        fireDatabaseHelper.observeChild(
                SelectedGoalData.class,
                new String[]{
                        "game_goals",
                        gameId,
                        "selected_goals"
                },
                new ReturnableChange<SelectedGoalData>() {
                    @Override
                    public void onChildAdded(SelectedGoalData data) {
                        bindSelectedGoalData(gameId, libraryKey, data, new Returnable<SelectedGoalData>() {
                            @Override
                            public void onResult(SelectedGoalData data) {
                                callback.onChildAdded(data);
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(SelectedGoalData data) {
                        bindSelectedGoalData(gameId, libraryKey, data, new Returnable<SelectedGoalData>() {
                            @Override
                            public void onResult(SelectedGoalData data) {
                                callback.onChildChanged(data);
                            }
                        });
                    }

                    @Override
                    public void onChildRemoved(SelectedGoalData data) {
                        callback.onChildRemoved(data);
                    }

                    @Override
                    public void onChildMoved(SelectedGoalData data) {
                    }

                    @Override
                    public void onResult(SelectedGoalData data) {
                    }
                },
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Can't retrieve goal list", data.toException());
                    }
                }
        );
    }

    /**
     * Returns goal data from library by id
     * @param libraryKey library from which to retrieve a goal
     * @param goalId id of the goal to retrieve
     * @param callback callback to receive goal data in
     */
    public void getGoalFromLibraryById(final String libraryKey, final String goalId, final Returnable<GoalData> callback) {
        fireDatabaseHelper.getRecord(
                GoalData.class,
                new String[]{
                        "libraries",
                        libraryKey,
                        "goals",
                        goalId
                },
                callback,
                new Returnable<DatabaseError>() {
                    @Override
                    public void onResult(DatabaseError data) {
                        Log.e(TAG, "Unable to retrieve goal data", data.toException());
                    }
                }
        );
    }

    /**
     * Binds goal data to selectedgoal data
     * This is a post-firebase transaction method to reconnect references from selectedgoal data to actual goal data
     * @param gameId game in which the goal resides
     * @param libraryKey library in which the goal resides
     * @param selectedGoalData selected goal data in which the goal data should be bound
     * @param callback callback that receives the selectedgoal data on success
     */
    public void bindSelectedGoalData(final String gameId, final String libraryKey, final SelectedGoalData selectedGoalData, final Returnable<SelectedGoalData> callback) {
        getPlayerById(gameId, selectedGoalData.getPlayerId(), new Returnable<PlayerData>() {
            @Override
            public void onResult(PlayerData data) {
                selectedGoalData.bindPlayerData(data);

                getGoalFromLibraryById(libraryKey, selectedGoalData.getGoalId(), new Returnable<GoalData>() {
                    @Override
                    public void onResult(GoalData goalData) {
                        selectedGoalData.bindGoalData(goalData);
                        callback.onResult(selectedGoalData);
                    }
                });
            }
        });
    }

    public void addGoalToSelectedGoals(String gameId, SelectedGoalData selectedGoalData) {
        fireDatabaseHelper.pushRecord(
                "selected_goal_id_",
                new String[]{
                        "game_goals",
                        gameId,
                        "selected_goals"
                },
                selectedGoalData
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
    public void observeRole(final String role, final String userId, final Returnable<String> callback) {
        fireDatabaseHelper.observeRecord(
                String.class,
                new String[]{
                        "secured",
                        userId,
                        role
                },
                new Returnable<String>() {
                    @Override
                    public void onResult(String data) {
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

    public void setRole(final String userId, final String role, final boolean toggle) {
        fireDatabaseHelper.addRecord(
                new String[]{
                        "secured",
                        userId
                },
                role,
                String.valueOf(toggle)
        );
    }
}
