package com.teachwithapps.weconomyexperience;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
import com.teachwithapps.weconomyexperience.util.IntentUtil;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.AppNavigationDrawer;
import com.teachwithapps.weconomyexperience.view.ScheduleRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.util.MultiRecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 1-8-17.
 */

public class GameActivity extends AppCompatActivity implements FireDatabaseTransactions.OnLoadingListener {

    private static final String TAG = GameActivity.class.getName();

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.schedule_recycler_view)
    protected MultiRecyclerView<ScheduledInstructionData> scheduleRecyclerView;

    @BindView(R.id.days_row)
    protected LinearLayout daysRowLayout;

    @BindView(R.id.goal_view)
    protected TextView goalView;

    @BindView(R.id.loading_view)
    protected View loadingView;

    private List<PlayerData> playerDataList;

    private GameData gameData;

    private int maxVisibleColumn = 3;
    private int minVisibleColumn = 0;
    private int daysCount = maxVisibleColumn - minVisibleColumn + 1;

    private List<List<ScheduledInstructionData>> scheduledInstructionDataMap;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            registerPlayer();
            observePlayers();
            observeSchedule();
            loadingView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        new AppNavigationDrawer(this, drawerLayout);

        playerDataList = new ArrayList<>();
        gameData = IntentUtil.getParcelsIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        toolbarTitle.setText(gameData.getName());

        //initialize instructiondatamap, this will hold the instructions for the visible schedule
        scheduledInstructionDataMap = new ArrayList<>();

        for (int i = 0; i < daysCount; i++) {
            scheduledInstructionDataMap.add(i, new ArrayList<ScheduledInstructionData>());
        }

        //fill the schedule for the number of visible days
        for (int i = 1; i <= daysCount; i++) {
            addDayToSchedule(i); //we start at day 1
        }

        //adapterfactory that will create scheduleadapters for the scheduledrecyclerview
        MultiRecyclerView.AdapterFactory<ScheduleRecyclerAdapter.ViewHolder, ScheduledInstructionData> adapterFactory =
                new MultiRecyclerView.AdapterFactory<ScheduleRecyclerAdapter.ViewHolder, ScheduledInstructionData>() {
                    @Override
                    public RecyclerView.Adapter<ScheduleRecyclerAdapter.ViewHolder> createAdapter(List<ScheduledInstructionData> ts) {
                        return new ScheduleRecyclerAdapter(ts);
                    }
                };

        //add instructiondatamap to the schedulerecyclerview
        scheduleRecyclerView.setDataMap(
                scheduledInstructionDataMap,
                adapterFactory
        );

        //show loading view, will be hidden when user is ready
        //firedatabasetransactions can show and hide it again
        loadingView.setVisibility(View.VISIBLE);

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);

        fireDatabaseTransactions.setOnLoadingListener(this);

        updateGoalCount();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_SELECT_INSTRUCTION) {
            if (resultCode == Constants.RESULT_CODE_OK) {
                int instructionIndexInView = data.getIntExtra(Constants.KEY_INSTRUCTION_DAY, -1);
                InstructionData instructionData = Parcels.unwrap(data.getParcelableExtra(Constants.KEY_INSTRUCTION_PARCEL));

                if (instructionIndexInView > 0 && instructionData != null) {
                    registerInstructionToSchedule(instructionIndexInView, instructionData);

                } else {
                    Log.d(TAG, "NO VALID ARGUMENTS");
                }
            } else {
                Log.d(TAG, "RESULT NOT OK");
            }
        } else {
            Log.d(TAG, "REQUEST NOT SELECTED INSTRUCTION");
        }
    }

    /**
     * Handle long click on instructions on the schedule
     * Routing call for the schedule widget
     *
     * @param data
     */
    public void longClickScheduledInstruction(final ScheduledInstructionData data) {
        showEditScheduledInstructionScreen(data);
    }

    /**
     * Retrieves player data from the playerlist
     * Routing call for schedule widget
     *
     * @param playerId
     */
    public PlayerData getPlayerById(String playerId) {
        for (PlayerData playerData : playerDataList) {
            if (playerData.getId().equals(playerId)) {
                return playerData;
            }
        }

        return null;
    }

    /**
     * Add a day to the schedule visible on the screen
     *
     * @param day index of the visible screen
     */
    private void addDayToSchedule(final int day) {
        View dayCell = LayoutInflater.from(this).inflate(R.layout.view_schedule_day, daysRowLayout, false);

        ((TextView) dayCell.findViewById(R.id.day_text)).setText(getString(R.string.day_text, day));
        dayCell.findViewById(R.id.add_instruction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectInstructionScreen(day);
            }
        });

        daysRowLayout.addView(dayCell);
    }

    /**
     * Add an instruction to the instruction library attached to this game
     *
     * @param instructionData
     */
    private void addInstructionToLibrary(InstructionData instructionData) {
        fireDatabaseTransactions.addInstructionToLibrary(gameData.getId(), instructionData);
    }

    /**
     * Register this player (created through the FireAuthHelper class) to the current game, if not already registered
     */
    private void registerPlayer() {
        FirebaseUser user = fireAuthHelper.getUser();
        Uri photoUrl = user.getPhotoUrl();
        PlayerData playerData = new PlayerData(
                user.getUid(),
                user.getDisplayName(),
                (photoUrl != null) ? photoUrl.toString() : null
        );
        fireDatabaseTransactions.registerPlayerToGame(gameData.getId(), playerData);
    }

    /**
     * Registers an instruction to the schedule, this call will push an instruction to the firebase
     * When that procedure is successful, addInstructionToSchedule is called to add it to the view
     *
     * @param instructionIndexView
     * @param instructionData
     */
    private void registerInstructionToSchedule(int instructionIndexView, InstructionData instructionData) {
        ScheduledInstructionData scheduledInstructionData = new ScheduledInstructionData();
        scheduledInstructionData.setInstructionKey(instructionData.getId());
        scheduledInstructionData.setDay(instructionIndexView);

        //push it to the firebase
        fireDatabaseTransactions.registerInstructionToSchedule(gameData.getId(), scheduledInstructionData);
    }

    /**
     * Observes the schedule and any change on the firebase database will be processed into
     * the schedule view widget
     */
    private void observeSchedule() {
        fireDatabaseTransactions.observeSchedule(
                gameData.getInstructionLibraryKey(),
                new String[]{
                        "game_schedules",
                        gameData.getId()
                },
                new ReturnableChange<ScheduledInstructionData>() {
                    @Override
                    public void onResult(ScheduledInstructionData data) {
                    }

                    @Override
                    public void onChildAdded(final ScheduledInstructionData data) {
                        addInstructionToSchedule(data);
                    }

                    @Override
                    public void onChildChanged(ScheduledInstructionData data) {
                        updateScheduledInstruction(data);
                    }

                    @Override
                    public void onChildRemoved(ScheduledInstructionData data) {
                        removeScheduledInstruction(data);
                    }

                    @Override
                    public void onChildMoved(ScheduledInstructionData data) {
                    }
                }
        );
    }

    /**
     * Add an instruction to the schedule
     *
     * @param data instruction to be added
     */
    private void addInstructionToSchedule(ScheduledInstructionData data) {
        int column = data.getDay() - 1;
        if (column < minVisibleColumn || column > maxVisibleColumn) {
            return;
        }
        List<ScheduledInstructionData> instructionDataList = scheduledInstructionDataMap.get(column);
        instructionDataList.add(0, data);

        checkLabour(column, data);

        scheduleRecyclerView.dataMapContentInserted(column, 0);
    }

    /**
     * Updates a instruction on the schedule
     *
     * @param data
     */
    private void updateScheduledInstruction(final ScheduledInstructionData data) {
        //get instructiondata by key and add to the scheduledInstruction datamap
        int column = data.getDay() - 1;
        if (column < minVisibleColumn || column > maxVisibleColumn) {
            return;
        }
        final List<ScheduledInstructionData> scheduledInstructionDataList = scheduledInstructionDataMap.get(column);

        for (int i = 0; i < scheduledInstructionDataList.size(); i++) {
            ScheduledInstructionData oldData = scheduledInstructionDataList.get(i);
            if (oldData.getId().equals(data.getId())) {
                oldData.setData(data);
                scheduleRecyclerView.dataMapContentUpdated(column, i);
                return;
            }
        }
    }

    /**
     * Removes a instruction from the schedule
     *
     * @param data
     */
    private void removeScheduledInstruction(final ScheduledInstructionData data) {
        //- 1 because days start at 1, arrays start at 0
        int column = data.getDay() - 1;
        if (column < minVisibleColumn || column > maxVisibleColumn) {
            return;
        }
        List<ScheduledInstructionData> scheduledInstructionList =
                scheduledInstructionDataMap.get(column);

        for (int i = 0; i < scheduledInstructionList.size(); i++) {
            ScheduledInstructionData scheduledInstruction = scheduledInstructionList.get(i);
            Log.d(TAG, "Comparing " + data.getId() + " == " + scheduledInstruction.getId());

            if (scheduledInstruction.getId().equals(data.getId())) {
                Log.d(TAG, "Remove data " + data.getId());
                scheduledInstructionList.remove(i);
                scheduleRecyclerView.dataMapContentRemoved(column, i);
                return;
            }
        }
    }

    /**
     * Marks a labour with a player
     *
     * @param scheduledInstructionData
     * @param index
     */
    public void setLabour(final ScheduledInstructionData scheduledInstructionData, final int index) {
        showPlayerSelectionScreen(
                getString(R.string.labour_select_player),
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        if (playerData == null) {
                            scheduledInstructionData.removeLabour(index);
                        } else {
                            scheduledInstructionData.setLabour(index, playerData.getId());
                        }

                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                },
                scheduledInstructionData.getDay(),
                true);
    }

    /**
     * Marks a claim with a player
     *
     * @param scheduledInstructionData
     * @param index
     */
    public void setClaim(final ScheduledInstructionData scheduledInstructionData, final int index) {
        showPlayerSelectionScreen(
                getString(R.string.claim_select_player),
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        if (playerData == null) {
                            scheduledInstructionData.removeClaim(index);
                        } else {
                            scheduledInstructionData.setClaim(index, playerData.getId());
                        }
                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                },
                scheduledInstructionData.getDay(),
                false);
    }

    /**
     * Shows the screen to select an instruction
     *
     * @param indexInView
     */
    private void showSelectInstructionScreen(int indexInView) {
        Intent intent = new Intent(GameActivity.this, SelectInstructionActivity.class);
        intent.putExtra(Constants.KEY_INSTRUCTION_DAY, indexInView);
        intent.putExtra(Constants.KEY_INSTRUCTION_LIBRARY_KEY, gameData.getInstructionLibraryKey());
        startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_INSTRUCTION);
    }

    /**
     * Shows screen for editting a scheduled instruction
     * The instruction can be removed or moved to another day here
     *
     * @param data
     */
    private void showEditScheduledInstructionScreen(final ScheduledInstructionData data) {
        final View scheduledInstructionEditDialog = LayoutInflater.from(this).inflate(R.layout.edit_scheduled_instruction_dialog, null);
        Spinner daySpinner = ((Spinner) scheduledInstructionEditDialog.findViewById(R.id.day_spinner));

        final String[] daysArray = new String[daysCount];
        for (int i = 0; i < daysCount; i++) {
            daysArray[i] = String.valueOf(i + 1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, daysArray);
        daySpinner.setAdapter(adapter);

        daySpinner.setSelection(data.getDay() - 1);
        daySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int selectedDay = Integer.valueOf(daysArray[position]);
                        data.setDay(selectedDay);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edit_scheduled_instruction_dialog_title);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(scheduledInstructionEditDialog);
        final Dialog dialog = builder.show();

        scheduledInstructionEditDialog
                .findViewById(R.id.remove_schedule_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireDatabaseTransactions.removeScheduledInstruction(gameData.getId(), data);
                        dialog.dismiss();
                    }
                });

        scheduledInstructionEditDialog
                .findViewById(R.id.move_scheduled_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireDatabaseTransactions.rescheduleScheduledInstruction(gameData.getId(), data);
                        dialog.dismiss();
                    }
                });
    }

    /**
     * Shows a screen with a list of players, optionally sorted depending on context (for labour)
     * A player can be selected and is returned to the callback
     *
     * @param title
     * @param callback
     * @param day
     * @param sort
     */
    private void showPlayerSelectionScreen(final String title, final Returnable<PlayerData> callback, final int day, final boolean sort) {

        final List<PlayerData> sortedPlayerDataList = (sort) ?
                sortPlayersForLabourSelection(playerDataList, day) :
                new ArrayList<>(playerDataList);

        //fill the string array for the selection dialog
        //we add remove player on top
        final CharSequence[] playerNames = new CharSequence[sortedPlayerDataList.size() + 1];
        playerNames[0] = "Remove player";
        for (int i = 0; i < sortedPlayerDataList.size(); i++) {
            playerNames[i + 1] = sortedPlayerDataList.get(i).getName();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setItems(
                playerNames,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //0 is remove player
                        if (which == 0) {
                            callback.onResult(null);
                        } else {
                            callback.onResult(sortedPlayerDataList.get(which - 1));
                        }
                    }
                });
        dialogBuilder.show();

    }

    /**
     * Removes players in the given list when they are already scheduled on the given day
     *
     * @param playerDataList
     */
    private List<PlayerData> sortPlayersForLabourSelection(List<PlayerData> playerDataList, int day) {
        List<PlayerData> sortedPlayerDataList = new ArrayList<>(playerDataList);
        List<ScheduledInstructionData> scheduledInstructionDataList = scheduledInstructionDataMap.get(day - 1);
        for (ScheduledInstructionData scheduledInstructionData : scheduledInstructionDataList) {
            List<String> labourList = scheduledInstructionData.getLabourList();

            List<PlayerData> snapshotPlayerList = new ArrayList<>(sortedPlayerDataList);
            for (PlayerData playerData : snapshotPlayerList) {
                String playerId = playerData.getId();
                if (labourList.contains(playerId)) {
                    sortedPlayerDataList.remove(playerData);
                }
            }
        }
        return sortedPlayerDataList;
    }

    /**
     * Observes changes in the player list for this game
     */
    private void observePlayers() {
        fireDatabaseTransactions.getPlayersInGame(
                gameData.getId(),
                new ReturnableChange<PlayerData>() {
                    @Override
                    public void onChildAdded(PlayerData data) {
                        playerDataList.add(data);
                    }

                    @Override
                    public void onChildChanged(PlayerData playerData) {
                        for (PlayerData needle : playerDataList) {
                            if (needle.getId().equals(playerData.getId())) {
                                playerDataList.set(playerDataList.indexOf(needle), playerData);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(PlayerData playerData) {
                        for (PlayerData needle : playerDataList) {
                            if (needle.getId().equals(playerData.getId())) {
                                playerDataList.remove(playerDataList.indexOf(needle));
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(PlayerData playerData) {
                    }

                    @Override
                    public void onResult(final PlayerData playerData) {
                    }
                }
        );
    }

    /**
     * Removes players from the scheduledinstructiondata if they already are assigned to a labour elsewhere
     *
     * @param column
     * @param scheduledInstructionToCheck
     */
    private void checkLabour(int column, ScheduledInstructionData scheduledInstructionToCheck) {
        if (column < minVisibleColumn || column > maxVisibleColumn) {
            return;
        }
        List<ScheduledInstructionData> scheduledInstructionDataList = scheduledInstructionDataMap.get(column);
        for (ScheduledInstructionData scheduledInstructionData : scheduledInstructionDataList) {
            if (scheduledInstructionData == scheduledInstructionToCheck) {
                continue;
            }

            List<String> labourList = scheduledInstructionData.getLabourList();

            Map<String, String> labourMapToCheck = scheduledInstructionToCheck.getLabourMap();
            Map<String, String> labourMapSnapshotToCheck = new HashMap<>(labourMapToCheck); //prevents concurrent modifications
            for (String key : labourMapSnapshotToCheck.keySet()) {
                String playerId = labourMapToCheck.get(key);
                if (labourList.contains(playerId)) {
                    Log.d(TAG, "playerId " + playerId + " exists in " + scheduledInstructionData.getId());
                    labourMapToCheck.remove(key);
                }
            }
        }

        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionToCheck);
    }

    /**
     * Requests the number of goals registered to this game and updates the textview with the result
     */
    private void updateGoalCount() {
        fireDatabaseTransactions.getGoalCount(
                gameData.getId(),
                new Returnable<Long>() {
                    @Override
                    public void onResult(Long count) {
                        goalView.setText(getString(R.string.goals_text, count));
                    }
                }
        );
    }

    /**
     * Displays the goal screen where players can view, mark and create their goals
     */
    private void showGoalScreen() {
        Intent intent = new Intent(GameActivity.this, ViewGoalsActivity.class);
        intent.putExtra(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
        startActivity(intent);
    }

    @OnClick(R.id.goal_view)
    protected void onClickGoalView() {
        showGoalScreen();
    }

    @OnClick(R.id.toolbar_close)
    protected void onClickToolbarClose() {
        finish();
    }

    @Override
    public void onLoadingChanged(Returnable<?> callback, FireDatabaseTransactions.LoadState loadState) {
        if (loadState == FireDatabaseTransactions.LoadState.LOADING_STARTED) {
            loadingView.setVisibility(View.VISIBLE);

        } else {
            loadingView.setVisibility(View.GONE);
        }
    }
}
