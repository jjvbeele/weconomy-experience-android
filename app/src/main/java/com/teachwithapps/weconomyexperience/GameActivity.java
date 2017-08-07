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

        //get gamedata parcel
        if (getIntent().hasExtra(Constants.KEY_GAME_DATA_PARCEL)) {
            //get from intent given by calling activity
            gameData = Parcels.unwrap(getIntent().getParcelableExtra(Constants.KEY_GAME_DATA_PARCEL));

        } else if (savedInstanceState != null && savedInstanceState.containsKey(Constants.KEY_GAME_DATA_PARCEL)) {
            //get from savedinstancestate saved when activity is recreated by the app
            gameData = Parcels.unwrap(savedInstanceState.getBundle(Constants.KEY_GAME_DATA_PARCEL));

        } else {
            //no game data avaiable, finish this activity
            finish();
        }

        //initialize instructiondatamap, this will hold the instructions for the visible schedule
        instructionDataMap = new ArrayList<>();

        //fill the schedule for the number of visible days
        for(int i = 0; i < numberOfVisibleDays; i++) {
            addDayToSchedule(i);
        }

        //add instructiondatamap to the schedulerecyclerview
        scheduleRecyclerView.setDataMap(instructionDataMap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
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

    private void addDayToSchedule(final int indexInView) {
        View dayCell = LayoutInflater.from(this).inflate(R.layout.view_schedule_day, daysRowLayout, false);

        ((TextView)dayCell.findViewById(R.id.day_text)).setText(getString(R.string.day_text, indexInView));
        dayCell.findViewById(R.id.add_instruction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, SelectInstructionActivity.class);
                intent.putExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, indexInView);
                startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_INSTRUCTION);
            }
        });

        instructionDataMap.add(indexInView, new ArrayList<InstructionData>());

        daysRowLayout.addView(dayCell);
    }

    private void addInstructionToSchedule(int indexInView, InstructionData instructionData) {
        List<InstructionData> instructionDataList = instructionDataMap.get(indexInView);
        instructionDataList.add(0, instructionData);

        scheduleRecyclerView.dataMapContentChanged(indexInView, 0);
    }
}
