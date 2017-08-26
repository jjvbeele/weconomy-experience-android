package com.teachwithapps.weconomyexperience;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.google.firebase.auth.FirebaseUser;
import com.teachwithapps.weconomyexperience.firebase.FireAuthHelper;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.model.GameData;
import com.teachwithapps.weconomyexperience.util.SecurityUtil;
import com.teachwithapps.weconomyexperience.view.AppNavigationDrawer;
import com.teachwithapps.weconomyexperience.view.GameRecyclerAdapter;
import com.teachwithapps.weconomyexperience.view.util.NavigationDrawer;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

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

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @BindView(R.id.button_start_new_game)
    protected View startNewGameButton;

    private MaterialMenuDrawable materialMenu;

    //hub game attributes
    private Map<String, GameData> gameDataMap;

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
            setupLayout();
            observeHubGames();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hub);

        ButterKnife.bind(this);

        new AppNavigationDrawer(this, drawerLayout);

        //set up firebase helper classes
        fireDatabaseTransactions = new FireDatabaseTransactions();
        fireAuthHelper = new FireAuthHelper(this);
        fireAuthHelper.withUser(this, fireAuthCallback);
    }

    private void setupLayout() {
        //set up the list of games present in the hub
        gameDataMap = new HashMap<>();
        gameRecyclerView.setAdapter(
                new GameRecyclerAdapter(
                        gameDataMap,
                        clickGameListener,
                        clickRemoveGameListener
                )
        );
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        boolean admin = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE).getBoolean(Constants.PREF_ADMIN, false);
        if(!admin) {
            checkAdminRole();
        } else {
            enableAdminLayout(true);
        }
    }

    private void checkAdminRole() {
        fireDatabaseTransactions.verifyRole("admin", fireAuthHelper.getUser().getUid(), new Returnable<Boolean>() {
            @Override
            public void onResult(Boolean data) {
                if(data == null) { data = false; }
                Log.d(TAG, "admin? " + data);
                SharedPreferences preferences = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
                preferences
                        .edit()
                        .putBoolean(Constants.PREF_ADMIN, data)
                        .apply();

                enableAdminLayout(data);
            }
        });
    }

    private void enableAdminLayout(boolean enabled) {
        if (enabled) {
            startNewGameButton.setVisibility(View.VISIBLE);
        } else {
            startNewGameButton.setVisibility(View.GONE);
        }
        ((GameRecyclerAdapter) gameRecyclerView.getAdapter()).updateAdmin(enabled);
    }

    /**
     * Needed for firebase to handle login by google account
     *
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
    private void observeHubGames() {
        fireDatabaseTransactions.observeHubGames(
                new ReturnableChange<String>() {
                    @Override
                    public void onChildAdded(String data) {
                        getGameData(data);
                        Log.d(TAG, "add " + data);
                    }

                    @Override
                    public void onChildChanged(String data) {

                    }

                    @Override
                    public void onChildRemoved(String data) {
                        gameDataMap.remove(data);
                        gameRecyclerView.getAdapter().notifyDataSetChanged();
                        Log.d(TAG, "remove " + data);
                    }

                    @Override
                    public void onChildMoved(String data) {

                    }
                }
        );
    }

    private void getGameData(final String gameKey) {
        fireDatabaseTransactions.getGameData(gameKey, new Returnable<GameData>() {
            @Override
            public void onResult(GameData data) {
                gameDataMap.put(gameKey, data);
                gameRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * remove game from the hub
     *
     * @param gameData
     */
    private void removeHubGame(GameData gameData) {
        fireDatabaseTransactions.removeHubGame(gameData);
    }

    /**
     * handle when user clicks on a game to join
     *
     * @param gameData
     */
    private void clickHubGame(GameData gameData) {
        Intent intent = new Intent(HubActivity.this, GameActivity.class);
        intent.putExtra(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
        startActivity(intent);
    }

    private void showCreateGameScreen() {
        final View createGameView = LayoutInflater.from(this).inflate(R.layout.view_create_game, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HubActivity.this);
        dialogBuilder.setTitle(getString(R.string.create_game_dialog_title));
        dialogBuilder.setView(createGameView);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String gameTitle = ((EditText) createGameView.findViewById(R.id.game_title)).getText().toString();
                GameData gameData = new GameData(gameTitle, "default_library");
                fireDatabaseTransactions.registerNewGame(gameData);
                dialog.dismiss();
            }
        });
        Dialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * user wants to make a new game
     */
    @OnClick(R.id.button_start_new_game)
    protected void startNewGame() {
        showCreateGameScreen();
    }
}
