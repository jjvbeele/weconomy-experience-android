package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

/**
 * Created by mint on 16-8-17.
 * Implementation of FireDataInterface for convenience
 */
public class FireData implements FireDataInterface {

    @Exclude
    private String id;

    @Exclude
    public String getId() {
        return this.id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

}
