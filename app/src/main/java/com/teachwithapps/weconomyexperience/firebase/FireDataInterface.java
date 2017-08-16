package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.Exclude;

/**
 * Created by mint on 1-8-17.
 * Interface used to set key value as id in the model
 * This id is not pushed to the firebase database as a separate field
 * but it is assigned the key value that its database record has
 */
public interface FireDataInterface {

    @Exclude
    String getId();

    @Exclude
    void setId(String id);
}
