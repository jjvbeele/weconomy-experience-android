package com.teachwithapps.weconomyexperience.firebase;

/**
 * Created by mint on 8-8-17.
 */

public class FireWrapInterface<T> extends FireData {

    private T wrappedData;

    public T getWrappedData() {
        return wrappedData;
    }

    public void setWrappedData(T wrappedData) {
        this.wrappedData = wrappedData;
    }
}
