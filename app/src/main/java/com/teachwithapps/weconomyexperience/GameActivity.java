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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.GoalData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.AppNavigationDrawer;
import com.teachwithapps.weconomyexperience.view.ScheduleRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.util.MultiRecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
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

    private GameData gameData;

    private int numberOfVisibleDays = 4;

    private List<List<ScheduledInstructionData>> scheduledInstructionDataMap;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            registerPlayer();
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

        gameData = getIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        toolbarTitle.setText(gameData.getName());

        //initialize instructiondatamap, this will hold the instructions for the visible schedule
        scheduledInstructionDataMap = new ArrayList<>();

        for (int i = 0; i < numberOfVisibleDays; i++) {
            scheduledInstructionDataMap.add(i, new ArrayList<ScheduledInstructionData>());
        }

        //fill the schedule for the number of visible days
        for (int i = 1; i <= numberOfVisibleDays; i++) {
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

    /**
     * Helper method to load intent and savedinstancestate data if available
     *
     * @param intent             intent of the activity with parameters passed from the calling parent activity
     * @param savedInstanceState savedinstancestate bundle to retrieve parameters when activity is recreated
     * @param key                key of the data
     * @param <T>                data to return
     * @return returns data of type T
     */
    private <T> T getIntentData(Intent intent, Bundle savedInstanceState, String key) {
        T data = null;

        //get parcel
        if (intent.hasExtra(key)) {
            //get from intent given by calling activity
            data = Parcels.unwrap(getIntent().getParcelableExtra(key));

        } else if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
            //get from savedinstancestate saved when activity is recreated by the app
            data = Parcels.unwrap(savedInstanceState.getParcelable(key));

        } else {
            Log.d(TAG, "Can't find key " + key + " in intent or savedinstancestate bundle");
        }

        return data;
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
                int instructionIndexInView = data.getIntExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, -1);
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
     * Add a day to the schedule visible on the screen
     *
     * @param indexInView index of the visible screen
     */
    private void addDayToSchedule(final int indexInView) {
        View dayCell = LayoutInflater.from(this).inflate(R.layout.view_schedule_day, daysRowLayout, false);

        ((TextView) dayCell.findViewById(R.id.day_text)).setText(getString(R.string.day_text, indexInView));
        dayCell.findViewById(R.id.add_instruction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectInstructionScreen(indexInView);
            }
        });

        daysRowLayout.addView(dayCell);
    }

    private void registerInstructionToSchedule(int instructionIndexView, InstructionData instructionData) {
        ScheduledInstructionData scheduledInstructionData = new ScheduledInstructionData();
        scheduledInstructionData.setInstructionKey(instructionData.getId());
        scheduledInstructionData.setDay(instructionIndexView);

        //push it to the firebase
        fireDatabaseTransactions.registerInstructionToSchedule(gameData.getId(), scheduledInstructionData);
    }

    private void showSelectInstructionScreen(int indexInView) {
        Intent intent = new Intent(GameActivity.this, SelectInstructionActivity.class);
        intent.putExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, indexInView);
        intent.putExtra(Constants.KEY_INSTRUCTION_LIBRARY_KEY, gameData.getInstructionLibraryKey());
        startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_INSTRUCTION);
    }

    /**
     * Add an instruction to the schedule
     *
     * @param day                      day visible on the screen to add the instruction to
     * @param scheduledInstructionData instruction to be added
     */
    private void addInstructionToSchedule(int day, ScheduledInstructionData scheduledInstructionData) {
        List<ScheduledInstructionData> instructionDataList = scheduledInstructionDataMap.get(day);
        instructionDataList.add(0, scheduledInstructionData);
        scheduleRecyclerView.dataMapContentInserted(day, 0);
    }

    private void addInstructionToLibrary(InstructionData instructionData) {
        fireDatabaseTransactions.addInstructionToLibrary(gameData.getId(), instructionData);
    }

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
     * Observes the schedule and any change on the firebase database will be processed into
     * the schedule view widget
     */
    private void observeSchedule() {
        fireDatabaseTransactions.observeSchedule(
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
                        addScheduledInstruction(data);
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

    private void addScheduledInstruction(final ScheduledInstructionData data) {
        //get instructiondata by key and add to the scheduledInstruction datamap
        fireDatabaseTransactions.getInstructionFromLibrary(
                gameData.getInstructionLibraryKey(),
                data.getInstructionKey(),
                new Returnable<InstructionData>() {
                    @Override
                    public void onResult(InstructionData instructionData) {
                        data.bindInstructionData(instructionData);
                        int day = data.getDay() - 1;
                        addInstructionToSchedule(day, data);
                    }
                });
    }

    private void updateScheduledInstruction(final ScheduledInstructionData data) {
        //get instructiondata by key and add to the scheduledInstruction datamap
        int column = data.getDay() - 1;
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

    private void removeScheduledInstruction(final ScheduledInstructionData data) {
        int column = data.getDay() - 1;
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

    public void longClickScheduledInstruction(final ScheduledInstructionData data) {
        final View scheduledInstructionEditDialog = LayoutInflater.from(this).inflate(R.layout.edit_scheduled_instruction_dialog, null);
        Spinner daySpinner = ((Spinner) scheduledInstructionEditDialog.findViewById(R.id.day_spinner));
        daySpinner.setSelection(data.getDay() - 1);
        daySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String[] dayArray = getResources().getStringArray(R.array.available_days_array);
                        int selectedDay = Integer.valueOf(dayArray[position]);
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

    public void getPlayerById(String playerId, Returnable<PlayerData> callback) {
        fireDatabaseTransactions.getPlayerById(
                gameData.getId(),
                playerId,
                callback
        );
    }

    public void setLabour(final ScheduledInstructionData scheduledInstructionData, final int index) {
        showPlayerSelectionScreen(
                "Select a player to assign for labour",
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        Map<String, String> labourArray = scheduledInstructionData.getLabourList();
                        //we add "0" as a workaround to force deserialization to String instead of integer
                        //Otherwise, it is treated as a sparse array instead of a map
                        //The 0 will later be lost when parsing back to an integer
                        labourArray.put("0" + String.valueOf(index), playerData.getId());
                        scheduledInstructionData.setLabourList(labourArray);
                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                });
    }

    public void setClaim(final ScheduledInstructionData scheduledInstructionData, final int index) {
        showPlayerSelectionScreen(
                "Select a player to claim output for",
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        Map<String, String> claimArray = scheduledInstructionData.getClaimList();
                        //we add "0" as a workaround to force deserialization to String instead of integer
                        //Otherwise, it is treated as a sparse array instead of a map
                        //The 0 will later be lost when parsing back to an integer
                        claimArray.put("0" + String.valueOf(index), playerData.getId());
                        scheduledInstructionData.setClaimList(claimArray);
                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                });
    }

    private void showPlayerSelectionScreen(final String title, final Returnable<PlayerData> returnable) {
        fireDatabaseTransactions.getPlayersInGame(
                gameData.getId(),
                new Returnable<List<PlayerData>>() {
                    @Override
                    public void onResult(final List<PlayerData> playerDataList) {
                        final CharSequence[] playerNames = new CharSequence[playerDataList.size()];
                        for (int i = 0; i < playerDataList.size(); i++) {
                            playerNames[i] = playerDataList.get(i).getName();
                        }

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
                        dialogBuilder.setTitle(title);
                        dialogBuilder.setItems(
                                playerNames,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        returnable.onResult(playerDataList.get(which));
                                    }
                                });
                        dialogBuilder.show();
                    }
                }
        );
    }

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

    private void showGoalScreen() {
        fireDatabaseTransactions.getGoalsInGame(
                gameData.getId(),
                new Returnable<List<GoalData>>() {
                    @Override
                    public void onResult(final List<GoalData> goalDataList) {
                        Log.d(TAG, "Show goals screen");
                        goalDataList.add(0, new GoalData("Create new goal"));
                        final CharSequence[] goalTextArray = new CharSequence[goalDataList.size()];
                        for (int i = 0; i < goalDataList.size(); i++) {
                            goalTextArray[i] = goalDataList.get(i).getText();
                        }

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
                        dialogBuilder.setTitle(getString(R.string.goal_dialog_title));
                        dialogBuilder.setItems(
                                goalTextArray,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            showCreateGoalScreen();
                                            dialog.dismiss();
                                        } else {
                                            showEditGoalScreen(goalDataList.get(which));
                                        }
                                    }
                                });
                        dialogBuilder.show();
                    }
                }
        );
    }

    private void showCreateGoalScreen() {
        final View createGoalView = LayoutInflater.from(this).inflate(R.layout.view_create_goal, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
        dialogBuilder.setTitle(getString(R.string.create_goal_dialog_title));
        dialogBuilder.setView(createGoalView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String goalText = ((EditText) createGoalView.findViewById(R.id.goal_text)).getText().toString();
                fireDatabaseTransactions.addGoalToGame(gameData.getId(), goalText);
                updateGoalCount();
                dialog.dismiss();
            }
        });
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showEditGoalScreen(GoalData goalData) {
        final View createGoalView = LayoutInflater.from(this).inflate(R.layout.view_edit_goal, null);

        ((TextView) createGoalView.findViewById(R.id.goal_text)).setText(goalData.getText());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(GameActivity.this);
        dialogBuilder.setTitle(getString(R.string.edit_goal_dialog_title));
        dialogBuilder.setView(createGoalView);
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @OnClick(R.id.goal_view)
    protected void onClickGoalView() {
        //Add after first release
        //showGoalScreen();
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
