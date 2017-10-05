package org.guts4roses.weconomyexperience;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import org.guts4roses.weconomyexperience.firebase.FireAuthHelper;
import org.guts4roses.weconomyexperience.firebase.util.Returnable;
import org.guts4roses.weconomyexperience.firebase.util.ReturnableChange;
import org.guts4roses.weconomyexperience.model.GameData;
import org.guts4roses.weconomyexperience.util.Log;
import org.guts4roses.weconomyexperience.view.AppNavigationDrawer;
import org.guts4roses.weconomyexperience.view.GameRecyclerAdapter;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 26-7-17.
 * Activity that shows the hub screen with available games.
 * Admins can create new games and remove existing games here.
 */

public class HubActivity extends AppCompatActivity implements FireDatabaseTransactions.OnLoadingListener, AppNavigationDrawer.NavigationInterface {

    private static final String TAG = HubActivity.class.getName();

    //views
    @BindView(R.id.recyclerview_game)
    protected RecyclerView gameRecyclerView;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;

    @BindView(R.id.button_start_new_game)
    protected View startNewGameButton;

    @BindView(R.id.loading_view)
    protected View loadingView;

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
            observeAdminRole();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hub);

        ButterKnife.bind(this);

        //set up the navigation drawer for this screen
        new AppNavigationDrawer(this, this, drawerLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            //set up firebase helper classes
            fireDatabaseTransactions = new FireDatabaseTransactions();
            fireAuthHelper = new FireAuthHelper(this);
            fireAuthHelper.withUser(this, fireAuthCallback);

            fireDatabaseTransactions.setOnLoadingListener(this);

        } else {
            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.ok, null)
                    .setMessage(R.string.wifi_off)
                    .setTitle(R.string.wifi_off_title)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fireDatabaseTransactions.unregister();
        fireAuthHelper.unregister(this);
    }

    /**
     * Method to be called at the start of the activity creation
     * Sets up the recyclerview and other things
     */
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
    }

    /**
     * Observes any changes in the admin role and reflects those changes on the screen and
     * in the preferences
     */
    private void observeAdminRole() {
        fireDatabaseTransactions.observeRole("admin", fireAuthHelper.getUser().getUid(), new Returnable<String>() {
            @Override
            public void onResult(String data) {
                boolean admin = (data == null) ? false : Boolean.valueOf(data);

                Log.d(TAG, "admin? " + admin);
                SharedPreferences preferences = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
                preferences
                        .edit()
                        .putBoolean(Constants.PREF_ADMIN, admin)
                        .apply();

                enableAdminLayout(admin);
            }
        });
    }

    /**
     * Enables or disables admin layout to give the administrator extra functions
     * @param enabled whether or not the admin layout should be enabled
     */
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
        if (requestCode == FireAuthHelper.RC_SIGN_IN) {
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
                    public void onResult(String data) {
                    }

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

    /**
     * Requests game data of a specific game
     * @param gameKey key of the game to retrieve
     */
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
        if (gameData.getVersion() == null ||
                Double.parseDouble(gameData.getVersion()) < BuildConfig.FIREBASE_MIN_VERSION_CODE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.game_too_old_title));
            builder.setMessage(getString(R.string.game_too_old));
            builder.setPositiveButton(R.string.ok, null);
            builder.show();

        } else if (Double.parseDouble(gameData.getVersion()) > BuildConfig.FIREBASE_MAX_VERSION_CODE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_too_old_title));
            builder.setMessage(getString(R.string.app_too_old));
            builder.setPositiveButton(R.string.ok, null);
            builder.show();

        } else {
            Intent intent = new Intent(HubActivity.this, GameActivity.class);
            intent.putExtra(Constants.KEY_GAME_DATA_PARCEL, Parcels.wrap(gameData));
            startActivity(intent);
        }
    }

    /**
     * Shows a screen to create a game
     */
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

    /**
     * FireDatabaseTransactions indicates a change in the loading status
     * @param callback
     * @param loadState
     */
    @Override
    public void onLoadingChanged(Returnable<?> callback, FireDatabaseTransactions.LoadState loadState) {
        if (loadState == FireDatabaseTransactions.LoadState.LOADING_STARTED) {
            loadingView.setVisibility(View.VISIBLE);

        } else {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void signOut() {
        fireAuthHelper.stopGoogleApiClient(this);
        Intent intent = new Intent(this, HubActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void toggleAdminMode() {
        SharedPreferences prefs = getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
        boolean admin = !prefs.getBoolean(Constants.PREF_ADMIN, true);
        fireDatabaseTransactions.setRole(fireAuthHelper.getUser().getUid(), "admin", admin);
    }
}
