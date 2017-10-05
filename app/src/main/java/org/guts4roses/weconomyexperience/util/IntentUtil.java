package org.guts4roses.weconomyexperience.util;

import android.content.Intent;
import android.os.Bundle;

import org.parceler.Parcels;

/**
 * Created by mint on 30-8-17.
 */

public class IntentUtil {

    private static final String TAG = IntentUtil.class.getName();


    /**
     * Helper method to load intent and savedinstancestate data if available
     *
     * @param intent             intent of the activity with parameters passed from the calling parent activity
     * @param savedInstanceState savedinstancestate bundle to retrieve parameters when activity is recreated
     * @param key                key of the data
     * @param <T>                data to return
     * @return returns data of type T
     */
    public static <T> T getParcelsIntentData(Intent intent, Bundle savedInstanceState, String key) {
        T data = null;

        //get parcel
        if (intent.hasExtra(key)) {
            //get from intent given by calling activity
            data = Parcels.unwrap(intent.getParcelableExtra(key));

        } else if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
            //get from savedinstancestate saved when activity is recreated by the app
            data = Parcels.unwrap(savedInstanceState.getParcelable(key));

        } else {
            Log.d(TAG, "Can't find key " + key + " in intent or savedinstancestate bundle");
        }

        return data;
    }
}
