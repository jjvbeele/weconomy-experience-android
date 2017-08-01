package com.teachwithapps.weconomyexperience.util;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.List;

/**
 * Created by mint on 1-8-17.
 */


public interface ReturnableList<T> {
    void onResult(GenericTypeIndicator<List<T>> data);
}