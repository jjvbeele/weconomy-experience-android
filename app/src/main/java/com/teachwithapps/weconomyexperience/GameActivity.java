package com.teachwithapps.weconomyexperience;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.MultiLinearRecyclerView;

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
    protected MultiLinearRecyclerView scheduleRecyclerView;

    @BindView(R.id.days_row)
    protected LinearLayout daysRowLayout;

    private GameData gameData;

    private int numberOfVisibleDays = 4;

    private List<List<InstructionData>> instructionDataMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        gameData = getIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        //initialize instructiondatamap, this will hold the instructions for the visible schedule
        instructionDataMap = getIntentData(getIntent(), savedInstanceState, Constants.KEY_INSTRUCTION_MAP_PARCEL);
        if(instructionDataMap == null) {
            instructionDataMap = new ArrayList<>();

            for(int i = 0; i < numberOfVisibleDays; i++) {
                instructionDataMap.add(i, new ArrayList<InstructionData>());
            }

        }

        //fill the schedule for the number of visible days
        for(int i = 0; i < numberOfVisibleDays; i++) {
            addDayToSchedule(i);
        }

        //add instructiondatamap to the schedulerecyclerview
        scheduleRecyclerView.setDataMap(instructionDataMap);
    }

    /**
     * Helper method to load intent and savedinstancestate data if available
     * @param intent intent of the activity with parameters passed from the calling parent activity
     * @param savedInstanceState savedinstancestate bundle to retrieve parameters when activity is recreated
     * @param key key of the data
     * @param <T> data to return
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
        outState.putParcelable(Constants.KEY_INSTRUCTION_MAP_PARCEL, Parcels.wrap(instructionDataMap));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_CODE_SELECT_INSTRUCTION) {
            if(resultCode == Constants.RESULT_CODE_OK) {
                int instructionIndexInView = data.getIntExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, -1);
                InstructionData instructionData = Parcels.unwrap(data.getParcelableExtra(Constants.KEY_INSTRUCTION_PARCEL));

                if(instructionIndexInView >= 0 && instructionData != null) {
                    addInstructionToSchedule(instructionIndexInView, instructionData);

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
     * @param indexInView index of the visible screen
     */
    private void addDayToSchedule(final int indexInView) {
        View dayCell = LayoutInflater.from(this).inflate(R.layout.view_schedule_day, daysRowLayout, false);

        ((TextView)dayCell.findViewById(R.id.day_text)).setText(getString(R.string.day_text, indexInView + 1));
        dayCell.findViewById(R.id.add_instruction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, SelectInstructionActivity.class);
                intent.putExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, indexInView);
                startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_INSTRUCTION);
            }
        });

        daysRowLayout.addView(dayCell);
    }

    /**
     * Add an instruction to the schedule
     * @param indexInView day visible on the screen to add the instruction to
     * @param instructionData instruction to be added
     */
    private void addInstructionToSchedule(int indexInView, InstructionData instructionData) {
        List<InstructionData> instructionDataList = instructionDataMap.get(indexInView);
        instructionDataList.add(0, instructionData);

        scheduleRecyclerView.dataMapContentChanged(indexInView, 0, true);
    }
}
