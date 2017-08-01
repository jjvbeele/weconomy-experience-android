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

public class LobbyActivity extends AppCompatActivity {

    private static final String TAG = LobbyActivity.class.getName();

    @BindView(R.id.recyclerview_game)
    protected RecyclerView gameRecyclerView;

    private List<GameData> gameDataList;

    private FireDatabaseTransactions fireDatabaseTransactions;
    private FireAuthHelper fireAuthHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        fireDatabaseTransactions = new FireDatabaseTransactions();

        ButterKnife.bind(this);

        gameDataList = new ArrayList<>();
        gameRecyclerView.setAdapter(new GameRecyclerAdapter(
                gameDataList,
                new GameRecyclerAdapter.OnClickListener() {
                    @Override
                    public void onClick(GameData gameData) {
                        fireDatabaseTransactions.removeHubGame(gameData);
                    }
                }));
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
    }

    private FireAuthHelper.FireAuthCallback fireAuthCallback = new FireAuthHelper.FireAuthCallback() {
        @Override
        public void userReady(FirebaseUser firebaseUser) {
            getHubGames();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Receiving broadcast request " + requestCode + " Result " + resultCode);

        // Result returned from FireAuthHelper, pass it along
        if (requestCode == RC_SIGN_IN) {
            fireAuthHelper.passActivityResult(data, resultCode);
        }
    }

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

    private void fillListWithGames(List<GameData> dataList) {
        gameDataList.clear();
        gameDataList.addAll(dataList);
        gameRecyclerView.getAdapter().notifyDataSetChanged();
        Log.d(TAG, "Found " + dataList.size() + " Games");
    }

    @OnClick(R.id.button_start_new_game)
    protected void startNewGame() {
        GameData gameData = new GameData("Test " + gameDataList.size());
        gameDataList.add(gameData);
        Log.d(TAG, "Click new game. Games available: " + gameDataList.size());
        gameRecyclerView.getAdapter().notifyItemInserted(gameDataList.size() - 1);

        fireDatabaseTransactions.registerNewGame(gameData);
    }
}
