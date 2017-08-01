package com.teachwithapps.weconomyexperience;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.teachwithapps.weconomyexperience.model.GameData;

import org.parceler.Parcels;

import butterknife.ButterKnife;

/**
 * Created by mint on 1-8-17.
 */

public class GameActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Constants.GAME_DATA_PARCEL, Parcels.wrap(gameData));
        super.onSaveInstanceState(outState);
    }
}
