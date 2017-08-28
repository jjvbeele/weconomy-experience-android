package com.teachwithapps.weconomyexperience;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;

/**
 * Created by mint on 1-8-17.
 */

public class Constants {

    public static final int RESULT_CODE_OK = 1001;
    public static final int RESULT_CODE_CANCELLED = 1002;

    public static final int REQUEST_CODE_SELECT_INSTRUCTION = 2001;

    public static final String KEY_INSTRUCTION_LIBRARY_KEY = "instruction_library_key";
    public static final String KEY_INSTRUCTION_DAY = "instruction_index_in_schedule";
    public static final String KEY_GAME_DATA_PARCEL = "game_data_parcel";
    public static final String KEY_INSTRUCTION_PARCEL = "instruction_parcel";
    public static final String DEFAULT_SHARED_PREFERENCES = "default_shared_preferences";
    public static final String PREF_ADMIN = "pref_admin";

    public static @DrawableRes int getProductIcon(String inputType) {
        if(inputType != null) {
            switch (inputType) {
                case "flour":
                    return R.drawable.ic_flour;
                case "bread":
                    return R.drawable.ic_bread;
                default:
                    return R.drawable.ic_package;
            }
        } else {
            return R.drawable.ic_empty;
        }
    }

    public static @DrawableRes int getLabourIcon() {
        return R.drawable.ic_shovel;
    }
}
