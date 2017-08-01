package com.teachwithapps.weconomyexperience.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.teachwithapps.weconomyexperience.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by mint on 23-12-16.
 */

public class FireAuthHelper {

    private static final String TAG = FireAuthHelper.class.getName();
    private static final String FIRE_AUTH_ID = "fire_auth_id";

    public static final int RC_SIGN_IN = 9001;

    public interface FireAuthCallback {
        void userReady(FirebaseUser firebaseUser);
    }

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;

    private List<WeakReference<FireAuthCallback>> callbackQueue;

    private Toast authFailedToast;

    public FireAuthHelper(Context context) {
        callbackQueue = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        authFailedToast = Toast.makeText(context, "Authentication failed.",
                Toast.LENGTH_SHORT);
    }

    public boolean withUser(FragmentActivity activity, FireAuthCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.d(TAG, "firebaseUser is null, signing in");

            if (googleApiClient == null) {
                Log.d(TAG, "googleApiClient is null");
                // Not signed in, launch the Sign In activity
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getResources().getString(R.string.default_web_client_id))
                        .requestId()
                        .requestEmail()
                        .build();

                googleApiClient = new GoogleApiClient.Builder(activity)
                        .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(TAG, connectionResult.getErrorMessage());
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
            }

            Integer id = callbackQueue.size();
            callbackQueue.add(id, new WeakReference<FireAuthCallback>(callback));

            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            signInIntent.putExtra(FIRE_AUTH_ID, id);
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
            return false;

        } else {
            callback.userReady(firebaseUser);
            return false;
        }
    }

    public void passActivityResult(Intent data, int resultCode) {
        ArrayList<WeakReference<FireAuthCallback>> callbackQueueCopy = new ArrayList<>(callbackQueue);
        callbackQueue.clear();

        for (WeakReference<FireAuthCallback> callbackRef : callbackQueueCopy) {
            if (callbackRef != null) {
                FireAuthCallback callback = callbackRef.get();
                if (callback != null) {
                    if (resultCode == RESULT_OK) {
                        firebaseAuthWithGoogle(data, callback);

                    } else {
                        new AlertDialog.Builder(authFailedToast.getView().getContext()).setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .setMessage("The server is not available at the moment")
                                .create()
                                .show();
                    }

                } else {
                    Log.d(TAG, "Can't retrieve callback to user signin response: null");
                }

            } else {
                Log.d(TAG, "Can't retrieve callback to user signin response: not in list");
            }

        }
    }

    public void stopGoogleApiClient(FragmentActivity activity) {
        Log.d(TAG, "googleApiClient stopped");
        if (googleApiClient != null) {
            googleApiClient.stopAutoManage(activity);
            googleApiClient.disconnect();
        }
    }

    public void firebaseAuthWithGoogle(Intent data, final FireAuthCallback callback) {
        Log.d(TAG, "Firebase Auth");
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Log.d(TAG, "Success!");
            GoogleSignInAccount account = result.getSignInAccount();

            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithCredential", task.getException());
                                authFailedToast.show();

                            } else if (task.getResult() != null && task.getResult().getUser() != null) {
                                callback.userReady(task.getResult().getUser());

                            } else {
                                Log.d(TAG, "Unknown error: no user found after signing");
                            }
                        }
                    });
        } else {
            Log.d(TAG, "No success");
        }
    }

    public FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }
}
