package com.teachwithapps.weconomyexperience;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.GoalData;
import com.teachwithapps.weconomyexperience.model.InstructionData;
import com.teachwithapps.weconomyexperience.util.IntentUtil;
import com.teachwithapps.weconomyexperience.view.FoldedInstructionRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.GoalRecyclerAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 7-8-17.
 */

public class ViewGoalsActivity extends AppCompatActivity {

    private static final String TAG = ViewGoalsActivity.class.getName();

    @BindView(R.id.goal_recycler)
    protected RecyclerView goalRecycler;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    private List<GoalData> goalDataList;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private GameData gameData;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            setupInstructionRecycler();
            getGoals();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_goals);

        ButterKnife.bind(this);

        gameData = IntentUtil.getParcelsIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        toolbarTitle.setText(getString(R.string.view_goals));

        goalRecycler.setAdapter(new GoalRecyclerAdapter(goalDataList));

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
        super.onSaveInstanceState(outState);
    }

    private void setupInstructionRecycler() {
        goalDataList = new ArrayList<>();
        goalRecycler.setLayoutManager(new LinearLayoutManager(this));
        goalRecycler.setAdapter(new GoalRecyclerAdapter(goalDataList));
    }

    private void getGoals() {
        fireDatabaseTransactions.getGoalsInGame(
                gameData.getId(),
                new Returnable<List<GoalData>>() {
                    @Override
                    public void onResult(final List<GoalData> goalDataList) {
                        updateGoalDataList(goalDataList);
                    }
                }
        );
    }

    private void updateGoalDataList(List<GoalData> goalDataList) {
        this.goalDataList = goalDataList;
        goalRecycler.getAdapter().notifyDataSetChanged();
    }

    /**
     * Shows the create goal screen where players can create their goals
     */
    private void showCreateGoalScreen() {
        final View createGoalView = LayoutInflater.from(this).inflate(R.layout.view_create_goal, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewGoalsActivity.this);
        dialogBuilder.setTitle(getString(R.string.create_goal_dialog_title));
        dialogBuilder.setView(createGoalView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String goalText = ((EditText) createGoalView.findViewById(R.id.goal_text)).getText().toString();
                fireDatabaseTransactions.addGoalToGame(gameData.getId(), goalText);
//                updateGoalCount();
                dialog.dismiss();
            }
        });
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * Shows the edit goal screen where players can edit their goals
     *
     * @param goalData
     */
    private void showEditGoalScreen(GoalData goalData) {
        final View createGoalView = LayoutInflater.from(this).inflate(R.layout.view_edit_goal, null);

        ((TextView) createGoalView.findViewById(R.id.goal_text)).setText(goalData.getText());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ViewGoalsActivity.this);
        dialogBuilder.setTitle(getString(R.string.edit_goal_dialog_title));
        dialogBuilder.setView(createGoalView);
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }


    @OnClick(R.id.add_goal)
    protected void onClickAddGoal() {

    }

    @OnClick(R.id.toolbar_close)
    protected void onClickClose() {
        finish();
    }
}
