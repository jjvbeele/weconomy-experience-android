package com.teachwithapps.weconomyexperience;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.FoldedInstructionRecyclerAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mint on 7-8-17.
 */

public class SelectInstructionActivity extends AppCompatActivity {

    private static final String TAG = SelectInstructionActivity.class.getName();

    @BindView(R.id.select_instruction_button)
    protected View selectInstructionButton;

    @BindView(R.id.instruction_recycler)
    protected RecyclerView instructionRecycler;

    private int instructionIndexInSchedule;
    private String instructionLibraryKey;

    private List<InstructionData> instructionDataList;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            loadInstructionLibrary();
        }
    };

    private void loadInstructionLibrary() {
        setupInstructionRecycler();
        fireDatabaseTransactions.getInstructionsFromLibrary(
                instructionLibraryKey,
                new Returnable<List<InstructionData>>() {
                    @Override
                    public void onResult(List<InstructionData> data) {
                        Log.d(TAG, "Load data " + data.size());
                        instructionDataList.addAll(data);
                        instructionRecycler.getAdapter().notifyDataSetChanged();
                    }
                });
    }

    private void setupInstructionRecycler() {
        instructionDataList = new ArrayList<>();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_instruction);

        ButterKnife.bind(this);

        instructionLibraryKey = getIntent().getStringExtra(Constants.KEY_INSTRUCTION_LIBRARY_KEY);
        instructionIndexInSchedule = getIntent().getIntExtra(Constants.KEY_INSTRUCTION_INDEX_IN_SCHEDULE, -1);

        if (instructionIndexInSchedule < 0) {
            finish();
        }

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
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
