package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by mint on 1-8-17.
 */
@IgnoreExtraProperties
public class FireData {

    @Exclude
    private String id;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}
