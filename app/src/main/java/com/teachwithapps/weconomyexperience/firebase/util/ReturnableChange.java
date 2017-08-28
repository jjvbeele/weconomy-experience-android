package com.teachwithapps.weconomyexperience.firebase.util;

/**
 * Created by mint on 29-12-16.
 */

public interface ReturnableChange<T> extends Returnable<T> {
    void onChildAdded(T data);

    void onChildChanged(T data);

    void onChildRemoved(T data);

    void onChildMoved(T data);
}

