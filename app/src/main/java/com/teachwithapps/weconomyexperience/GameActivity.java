package com.teachwithapps.weconomyexperience;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.model.PlayerData;
import com.teachwithapps.weconomyexperience.model.ScheduledInstructionData;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.ScheduleRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.util.MultiLinearRecyclerView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getName();

    @BindView(R.id.schedule_recycler_view)
    protected MultiLinearRecyclerView<ScheduledInstructionData> scheduleRecyclerView;

    @BindView(R.id.days_row)
    protected LinearLayout daysRowLayout;

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
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        gameData = getIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        //initialize instructiondatamap, this will hold the instructions for the visible schedule
        scheduledInstructionDataMap = new ArrayList<>();

        for (int i = 0; i < numberOfVisibleDays; i++) {
            scheduledInstructionDataMap.add(i, new ArrayList<ScheduledInstructionData>());
        }

        //fill the schedule for the number of visible days
        for (int i = 0; i < numberOfVisibleDays; i++) {
            addDayToSchedule(i + 1); //we start at day 1
        }

        MultiLinearRecyclerView.AdapterFactory
                <ScheduleRecyclerAdapter.ViewHolder, ScheduledInstructionData>
                adapterFactory = new MultiLinearRecyclerView.AdapterFactory
                <ScheduleRecyclerAdapter.ViewHolder, ScheduledInstructionData>() {
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

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
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

                if (instructionIndexInView >= 0 && instructionData != null) {
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

        ((TextView) dayCell.findViewById(R.id.day_text)).setText(getString(R.string.day_text, indexInView + 1));
        dayCell.findViewById(R.id.add_instruction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectInstructionScreen(indexInView);
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

    private void openSelectInstructionScreen(int indexInView) {
        Intent intent = new Intent(GameActivity.this, SelectInstructionActivity.class);
        intent.putExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, indexInView);
        intent.putExtra(Constants.KEY_INSTRUCTION_LIBRARY_KEY, gameData.getInstructionLibraryKey());
        startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_INSTRUCTION);
    }

    /**
     * Add an instruction to the schedule
     *
     * @param indexInView              day visible on the screen to add the instruction to
     * @param scheduledInstructionData instruction to be added
     */
    private void addInstructionToSchedule(int indexInView, ScheduledInstructionData scheduledInstructionData) {
        List<ScheduledInstructionData> instructionDataList = scheduledInstructionDataMap.get(indexInView);
        instructionDataList.add(0, scheduledInstructionData);

        scheduleRecyclerView.dataMapContentChanged(indexInView, 0, true);
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

    private void observeSchedule() {
        fireDatabaseTransactions.observeSchedule(
                new String[]{
                        "game_schedules",
                        gameData.getId()
                },
                new ReturnableChange<ScheduledInstructionData>() {
                    @Override
                    public void onChildAdded(final ScheduledInstructionData scheduledInstructionData) {
                        //get instructiondata by key and add to the scheduledInstruction datamap
                        fireDatabaseTransactions.getInstructionFromLibrary(
                                gameData.getInstructionLibraryKey(),
                                scheduledInstructionData.getInstructionKey(),
                                new Returnable<InstructionData>() {
                                    @Override
                                    public void onResult(InstructionData instructionData) {
                                        final List<ScheduledInstructionData> scheduledInstructionDataList = scheduledInstructionDataMap.get(scheduledInstructionData.getDay() - 1);
                                        scheduledInstructionData.bindInstructionData(instructionData);
                                        scheduledInstructionDataList.add(scheduledInstructionData);
                                        scheduleRecyclerView.dataMapChanged();
                                    }
                                });
                    }

                    @Override
                    public void onChildChanged(ScheduledInstructionData data) {

                    }

                    @Override
                    public void onChildRemoved(ScheduledInstructionData data) {
                        int column = data.getDay() - 1;
                        List<ScheduledInstructionData> scheduledInstructionList =
                                scheduledInstructionDataMap.get(column);
                        for (int i = 0; i < scheduledInstructionList.size(); i++) {
                            ScheduledInstructionData scheduledInstruction = scheduledInstructionList.get(i);
                            Log.d(TAG, "Comparing " + data.getId() + " == " + scheduledInstruction.getId());
                            if (scheduledInstruction.getId().equals(data.getId())) {
                                Log.d(TAG, "Remove data " + data.getId());
                                scheduledInstructionList.remove(i);
                                scheduleRecyclerView.dataMapContentChanged(column, i, false);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(ScheduledInstructionData data) {

                    }
                }
        );
    }

    public void removeData(ScheduledInstructionData data) {
        fireDatabaseTransactions.removeScheduledInstruction(gameData.getId(), data);
    }

    public void setLabour(final ScheduledInstructionData scheduledInstructionData) {
        showPlayerSelectionScreen(
                "Select a player to assign for labour",
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        scheduledInstructionData.getLabourList().add(playerData.getId());
                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                });
    }

    public void setClaim(final ScheduledInstructionData scheduledInstructionData) {
        showPlayerSelectionScreen(
                "Select a player to claim output for",
                new Returnable<PlayerData>() {
                    @Override
                    public void onResult(PlayerData playerData) {
                        scheduledInstructionData.getClaimList().add(playerData.getId());
                        fireDatabaseTransactions.updateScheduledInstruction(gameData.getId(), scheduledInstructionData);
                    }
                });
    }

    private void showPlayerSelectionScreen(final String title, final Returnable returnable) {
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
}
