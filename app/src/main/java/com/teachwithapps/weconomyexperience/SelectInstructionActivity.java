package com.teachwithapps.weconomyexperience;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.view.FoldedInstructionRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.ScheduleRecyclerAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 7-8-17.
 */

public class SelectInstructionActivity extends AppCompatActivity {

    @BindView(R.id.select_instruction_button)
    protected View selectInstructionButton;

    @BindView(R.id.instruction_recycler)
    protected RecyclerView instructionRecycler;

    private int instructionIndexInSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_instruction);

        ButterKnife.bind(this);

        instructionIndexInSchedule = getIntent().getIntExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, -1);

        if (instructionIndexInSchedule < 0) {
            finish();
        }

        List<InstructionData> instructionDataList = new ArrayList<>();
        instructionDataList.add(new InstructionData("Grind Grain to Flour", 1));
        instructionDataList.add(new InstructionData("Bake Flour to Bread", 1));
        instructionDataList.add(new InstructionData("Mill Grain to Flour", 4));
        instructionDataList.add(new InstructionData("Bread Baking Oven", 3));
        instructionDataList.add(new InstructionData("Manage Recreation Park", 2));
        instructionDataList.add(new InstructionData("Manage Ressearch Lab", 2));

        instructionRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionRecycler.setAdapter(new FoldedInstructionRecyclerAdapter(
                instructionDataList,
                new FoldedInstructionRecyclerAdapter.OnInstructionClickListener() {
                    @Override
                    public void onClick(InstructionData instructionData) {
                        finishWithInstruction(instructionData);
                    }
                }
        ));
    }

    private void finishWithInstruction(InstructionData instructionData) {
        Parcelable instructionParcelable = Parcels.wrap(instructionData);

        Intent instructionIntent = new Intent();
        instructionIntent.putExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, instructionIndexInSchedule);
        instructionIntent.putExtra(Constants.KEY_INSTRUCTION_PARCEL, instructionParcelable);
        setResult(Constants.RESULT_CODE_OK, instructionIntent);
        finish();
    }
}
