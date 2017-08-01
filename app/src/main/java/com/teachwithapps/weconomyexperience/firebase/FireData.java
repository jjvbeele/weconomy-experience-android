package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by mint on 1-8-17.
 */
@IgnoreExtraProperties
public class FireData {

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
