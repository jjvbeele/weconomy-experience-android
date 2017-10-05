package org.guts4roses.weconomyexperience;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import org.guts4roses.weconomyexperience.firebase.FireAuthHelper;
import org.guts4roses.weconomyexperience.firebase.util.ReturnableChange;
import org.guts4roses.weconomyexperience.model.GameData;
import org.guts4roses.weconomyexperience.model.GoalData;
import org.guts4roses.weconomyexperience.model.SelectedGoalData;
import org.guts4roses.weconomyexperience.util.IntentUtil;
import org.guts4roses.weconomyexperience.view.GoalRecyclerAdapter;

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

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private GameData gameData;

    private List<GoalData> availableGoalList;
    private List<GoalData> libraryGoalList;
    private List<SelectedGoalData> selectedGoalList;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            observeGoals();
            setupInstructionRecycler();
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
        selectedGoalList = new ArrayList<>();
        goalRecycler.setLayoutManager(new LinearLayoutManager(this));
        goalRecycler.setAdapter(new GoalRecyclerAdapter(selectedGoalList));
    }

    private void observeGoals() {
        libraryGoalList = new ArrayList<>();
        availableGoalList = new ArrayList<>();
        selectedGoalList = new ArrayList<>();

        observeSelectedGoals();
        observeAvailableGoals();
        observeLibraryGoals();
    }

    private void observeSelectedGoals() {
        fireDatabaseTransactions.observeSelectedGoalsInGame(
                gameData.getId(),
                gameData.getLibraryKey(),
                new ReturnableChange<SelectedGoalData>() {
                    @Override
                    public void onChildAdded(SelectedGoalData data) {
                        selectedGoalList.add(data);
                        goalRecycler.getAdapter().notifyItemInserted(selectedGoalList.size() - 1);
                    }

                    @Override
                    public void onChildChanged(SelectedGoalData data) {
//                        for (SelectedGoalData needle : selectedGoalList) {
//                            if (needle.getId().equals(data.getId())) {
//                                int index = selectedGoalList.indexOf(needle);
//                                selectedGoalList.remove(index);
//                                selectedGoalList.add(index, data);
//                                goalRecycler.getAdapter().notifyItemChanged(index);
//                                return;
//                            }
//                        }
                    }

                    @Override
                    public void onChildRemoved(SelectedGoalData data) {
                        for (SelectedGoalData needle : selectedGoalList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = selectedGoalList.indexOf(needle);
                                selectedGoalList.remove(index);
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

    private void observeAvailableGoals() {
        fireDatabaseTransactions.observeAvailableGoalsInGame(
                gameData.getId(),
                gameData.getLibraryKey(),
                new ReturnableChange<GoalData>() {
                    @Override
                    public void onChildAdded(GoalData data) {
                        availableGoalList.add(data);
                    }

                    @Override
                    public void onChildChanged(GoalData data) {
                        for (GoalData needle : availableGoalList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = availableGoalList.indexOf(needle);
                                availableGoalList.remove(index);
                                availableGoalList.add(index, data);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(GoalData data) {
                        for (GoalData needle : availableGoalList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = availableGoalList.indexOf(needle);
                                availableGoalList.remove(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(GoalData data) {
                    }

                    @Override
                    public void onResult(GoalData data) {
                    }
                }
        );
    }

    private void observeLibraryGoals() {
        fireDatabaseTransactions.observeGoalsFromLibrary(
                gameData.getLibraryKey(),
                new ReturnableChange<GoalData>() {
                    @Override
                    public void onChildAdded(GoalData data) {
                        libraryGoalList.add(data);
                    }

                    @Override
                    public void onChildChanged(GoalData data) {
                        for (GoalData needle : libraryGoalList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = libraryGoalList.indexOf(needle);
                                libraryGoalList.remove(index);
                                libraryGoalList.add(index, data);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(GoalData data) {
                        for (GoalData needle : libraryGoalList) {
                            if (needle.getId().equals(data.getId())) {
                                int index = libraryGoalList.indexOf(needle);
                                libraryGoalList.remove(index);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(GoalData data) {
                    }

                    @Override
                    public void onResult(GoalData data) {
                    }
                }
        );
    }

    private void showAddGoalMenu() {
        String[] availableGoalArray = new String[availableGoalList.size()];
        for (int i = 0; i < availableGoalList.size(); i++) {
            availableGoalArray[i] = availableGoalList.get(i).getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewGoalsActivity.this);
        builder.setTitle(R.string.select_goal);
        builder.setPositiveButton(R.string.done, null);
        builder.setItems(availableGoalArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoalData goalData = availableGoalList.get(which);
                SelectedGoalData selectedGoalData = new SelectedGoalData();
                selectedGoalData.setGoalId(goalData.getId());
                selectedGoalData.setPlayerId("player_id_" + fireAuthHelper.getUser().getUid());
                selectedGoalData.setRealised(false);
                fireDatabaseTransactions.addGoalToSelectedGoals(gameData.getId(), selectedGoalData);
            }
        });
        builder.show();

    }

    @OnClick(R.id.add_goal)
    protected void onClickAddGoal() {
        showAddGoalMenu();
    }

    @OnClick(R.id.toolbar_close)
    protected void onClickClose() {
        finish();
    }

    public void onLongClickSelectedGoal(SelectedGoalData selectedGoalData) {
        fireDatabaseTransactions.removeSelectedGoal(gameData.getId(), selectedGoalData.getId());
    }

    public void onCheckSelectedGoal(SelectedGoalData selectedGoalData) {
        fireDatabaseTransactions.updateSelectedGoal(gameData.getId(), selectedGoalData);
    }
}
