package com.teachwithapps.weconomyexperience;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.view.MultiLinearRecyclerView;
import com.teachwithapps.weconomyexperience.view.ScheduleRecyclerAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class GameActivity extends AppCompatActivity {

    @BindView(R.id.schedule_recycler_view)
    protected MultiLinearRecyclerView scheduleRecyclerView;

    private GameData gameData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        //get gamedata parcel
        if (getIntent().hasExtra(Constants.GAME_DATA_PARCEL)) {
            //get from intent given by calling activity
            gameData = Parcels.unwrap(getIntent().getParcelableExtra(Constants.GAME_DATA_PARCEL));

        } else if (savedInstanceState != null && savedInstanceState.containsKey(Constants.GAME_DATA_PARCEL)) {
            //get from savedinstancestate saved when activity is recreated by the app
            gameData = Parcels.unwrap(savedInstanceState.getBundle(Constants.GAME_DATA_PARCEL));

        } else {
            //no game data avaiable, finish this activity
            finish();
        }

        List<List<InstructionData>> instructionDataMap = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<InstructionData> instructionList = new ArrayList<>();
            if(i == 0) {
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Mill Flour"));

            }
            if(i == 1) {
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));

            }
            if(i == 2) {
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Mill Flour"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));

            }
            if(i == 3) {
                instructionList.add(new InstructionData("Grind Flour"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));

            }
            if(i == 4) {
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));
                instructionList.add(new InstructionData("Bake Bread"));

            }
            instructionDataMap.add(instructionList);
        }

        scheduleRecyclerView.setDataMap(instructionDataMap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.GAME_DATA_PARCEL, Parcels.wrap(gameData));
        super.onSaveInstanceState(outState);
    }
}
