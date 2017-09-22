package org.guts4roses.weconomyexperience.firebase.util;

/**
 * Created by mint on 29-12-16.
 */

public interface Returnable<T> {
    void onResult(T data);
}

