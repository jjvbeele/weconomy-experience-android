package com.teachwithapps.weconomyexperience;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.FireDatabaseTransactions;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.util.Returnable;
import com.teachwithapps.weconomyexperience.view.GameRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.teachwithapps.weconomyexperience.firebase.FireAuthHelper.RC_SIGN_IN;

/**
 * Created by mint on 26-7-17.
 */

public class HubActivity extends AppCompatActivity {

    private static final String TAG = HubActivity.class.getName();

    //views
    @BindView(R.id.recyclerview_game)
    protected RecyclerView gameRecyclerView;

    //hub game attributes
    private List<GameData> gameDataList;

    private GameRecyclerAdapter.OnClickListener clickGameListener = new GameRecyclerAdapter.OnClickListener() {
        @Override
        public void onClick(GameData gameData) {
            clickHubGame(gameData);
        }
    };

    private GameRecyclerAdapter.OnClickListener clickRemoveGameListener = new GameRecyclerAdapter.OnClickListener() {
        @Override
        public void onClick(GameData gameData) {
            removeHubGame(gameData);
        }
    };

    //firebase attributes
    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            getHubGames();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hub);

        //set up firebase helper classes
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
        fireDatabaseTransactions = new FireDatabaseTransactions();

        ButterKnife.bind(this);

        //set up the list of games present in the hub
        gameDataList = new ArrayList<>();
        gameRecyclerView.setAdapter(
                new GameRecyclerAdapter(
                        gameDataList,
                        clickGameListener,
                        clickRemoveGameListener
                )
        );
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Needed for firebase to handle login by google account
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Receiving broadcast request " + requestCode + " Result " + resultCode);

        // Result returned from FireAuthHelper, pass it along
        if (requestCode == RC_SIGN_IN) {
            fireAuthHelper.passActivityResult(data, resultCode);
        }
    }

    /**
     * request list of games in the hub
     */
    private void getHubGames() {
        fireDatabaseTransactions.queryHubGames(
                new Returnable<List<GameData>>() {
                    @Override
                    public void onResult(List<GameData> dataList) {
                        fillListWithGames(dataList);
                    }
                }
        );
    }

    /**
     * remove game from the hub
     * TODO: don't actually remove, but put data in inactive state
     * @param gameData
     */
    private void removeHubGame(GameData gameData) {
        fireDatabaseTransactions.removeHubGame(gameData);
    }

    /**
     * handle when user clicks on a game to join
     * @param gameData
     */
    private void clickHubGame(GameData gameData) {

    }

    /**
     * fill hub with the provided list of games
     * @param dataList
     */
    private void fillListWithGames(List<GameData> dataList) {
        gameDataList.clear();
        gameDataList.addAll(dataList);
        gameRecyclerView.getAdapter().notifyDataSetChanged();
        Log.d(TAG, "Found " + dataList.size() + " Games");
    }

    /**
     * user wants to make a new game
     */
    @OnClick(R.id.button_start_new_game)
    protected void startNewGame() {
        GameData gameData = new GameData("Test " + gameDataList.size());
        gameDataList.add(gameData);
        Log.d(TAG, "Click new game. Games available: " + gameDataList.size());
        gameRecyclerView.getAdapter().notifyItemInserted(gameDataList.size() - 1);

        fireDatabaseTransactions.registerNewGame(gameData);
    }
}
