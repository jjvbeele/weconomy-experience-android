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
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.model.GoalData;
import com.teachwithapps.weconomyexperience.model.SelectedGoalData;
import com.teachwithapps.weconomyexperience.util.IntentUtil;
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

    private List<SelectedGoalData> goalDataList;

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private GameData gameData;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            setupInstructionRecycler();
            getSelectedGoals();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_goals);

        ButterKnife.bind(this);

        gameData = IntentUtil.getParcelsIntentData(getIntent(), savedInstanceState, Constants.KEY_GAME_DATA_PARCEL);

        toolbarTitle.setText(getString(R.string.view_goals));

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

    private void getSelectedGoals() {
        fireDatabaseTransactions.observeSelectedGoalsInGame(
                gameData.getId(),
                new ReturnableChange<SelectedGoalData>() {
                    @Override
                    public void onChildAdded(SelectedGoalData data) {
                        goalDataList.add(data);
                        goalRecycler.getAdapter().notifyItemInserted(goalDataList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(SelectedGoalData data) {
                        for(SelectedGoalData needle : goalDataList) {
                            if(needle.getId().equals(data.getId())) {
                                int index = goalDataList.indexOf(needle);
                                goalDataList.remove(index);
                                goalDataList.add(index, data);
                                goalRecycler.getAdapter().notifyItemChanged(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(SelectedGoalData data) {
                        for(SelectedGoalData needle : goalDataList) {
                            if(needle.getId().equals(data.getId())) {
                                int index = goalDataList.indexOf(needle);
                                goalDataList.remove(index);
                                goalRecycler.getAdapter().notifyItemRemoved(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(SelectedGoalData data) {

                    }

                    @Override
                    public void onResult(SelectedGoalData data) {

                    }
                }
        );
    }

    @OnClick(R.id.add_goal)
    protected void onClickAddGoal() {
        fireDatabaseTransactions.getAvailableGoalsInGame(
                gameData.getId(),
                new Returnable<List<GoalData>>() {
                    @Override
                    public void onResult(final List<GoalData> goalDataList) {
                        String[] availableGoalArray = new String[goalDataList.size()];
                        for (int i = 0; i < goalDataList.size(); i++) {
                            availableGoalArray[i] = goalDataList.get(i).getText();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGoalsActivity.this);
                        builder.setTitle(R.string.select_goal);
                        builder.setItems(availableGoalArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GoalData goalData = goalDataList.get(which);
                                SelectedGoalData selectedGoalData = new SelectedGoalData();
                                selectedGoalData.setGoalId(goalData.getId());
                                selectedGoalData.setPlayerId("player_id_" + fireAuthHelper.getUser().getUid());
                                selectedGoalData.setRealized(false);
                                fireDatabaseTransactions.addGoalToSelectedGoals(gameData.getId(), selectedGoalData);
                            }
                        });
                        builder.show();
                    }
                }
        );
    }

    @OnClick(R.id.toolbar_close)
    protected void onClickClose() {
        finish();
    }

    public void onLongClickSelectedGoal(SelectedGoalData selectedGoalData) {
        fireDatabaseTransactions.removeSelectedGoal(gameData.getId(), selectedGoalData.getId());
    }
}
