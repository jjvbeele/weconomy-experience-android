package com.teachwithapps.weconomyexperience;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.view.GameRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 26-7-17.
 */

public class LobbyActivity extends AppCompatActivity {

    private static final String TAG = LobbyActivity.class.getName();

    @BindView(R.id.recyclerview_game)
    protected RecyclerView gameRecyclerView;

    private List<GameData> gameDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        ButterKnife.bind(this);

        gameDataList = new ArrayList<>();
        gameRecyclerView.setAdapter(new GameRecyclerAdapter(gameDataList));
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.button_start_new_game)
    protected void startNewGame() {
        gameDataList.add(new GameData("Test " + gameDataList.size()));
        Log.d(TAG, "Click new game. Games available: " + gameDataList.size());
        gameRecyclerView.getAdapter().notifyItemInserted(gameDataList.size() - 1);
    }
}
