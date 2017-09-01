package com.teachwithapps.weconomyexperience;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.util.IntentUtil;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.view.FoldedInstructionRecyclerAdapter;

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

    private static final String TAG = SelectInstructionActivity.class.getName();

    @BindView(R.id.instruction_recycler)
    protected RecyclerView instructionRecycler;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    private int instructionIndexInSchedule;
    private String libraryKey;

    private GameData gameData;

    private List<InstructionData> availableInstructionList;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            setupInstructionRecycler();
            observeAvailableInstructionsInGame();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_instruction);

        ButterKnife.bind(this);

        gameData = IntentUtil.getParcelsIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        toolbarTitle.setText(getString(R.string.select_instruction));

        libraryKey = getIntent().getStringExtra(Constants.KEY_LIBRARY_KEY);
        instructionIndexInSchedule = getIntent().getIntExtra(Constants.KEY_INSTRUCTION_DAY, -1);

        if (instructionIndexInSchedule < 0) {
            finish();
        }

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
    }

    private void observeAvailableInstructionsInGame() {
        fireDatabaseTransactions.observeAvailableInstructionsInGame(
                gameData.getId(),
                gameData.getLibraryKey(),
                new ReturnableChange<InstructionData>() {
                    @Override
                    public void onChildAdded(InstructionData data) {
                        availableInstructionList.add(data);
                        instructionRecycler.getAdapter().notifyItemInserted(availableInstructionList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(InstructionData data) {
                        for (InstructionData needle : availableInstructionList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = availableInstructionList.indexOf(needle);
                                availableInstructionList.remove(index);
                                availableInstructionList.add(index, data);
                                instructionRecycler.getAdapter().notifyItemChanged(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(InstructionData data) {
                        for (InstructionData needle : availableInstructionList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = availableInstructionList.indexOf(needle);
                                availableInstructionList.remove(index);
                                instructionRecycler.getAdapter().notifyItemRemoved(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(InstructionData data) {
                    }

                    @Override
                    public void onResult(InstructionData data) {
                    }
                }
        );
    }

    private void setupInstructionRecycler() {
        availableInstructionList = new ArrayList<>();
        instructionRecycler.setLayoutManager(new LinearLayoutManager(this));
        instructionRecycler.setAdapter(new FoldedInstructionRecyclerAdapter(
                availableInstructionList,
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
        instructionIntent.putExtra(Constants.KEY_INSTRUCTION_DAY, instructionIndexInSchedule);
        instructionIntent.putExtra(Constants.KEY_INSTRUCTION_PARCEL, instructionParcelable);
        setResult(Constants.RESULT_CODE_OK, instructionIntent);
        finish();
    }

    @OnClick(R.id.toolbar_close)
    protected void onClickClose() {
        finish();
    }
}
