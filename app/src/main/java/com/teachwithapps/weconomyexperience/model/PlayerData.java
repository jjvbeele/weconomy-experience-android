package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 9-8-17.
 */
@IgnoreExtraProperties
@Parcel
public class PlayerData extends FireData {

    private String name;
    private String photoUrl;

    public PlayerData() {
    }

    public PlayerData(String uuid, String name, String photoUrl) {
        this.setId("player_id_" + uuid);
        this.name = name;
        this.photoUrl = photoUrl;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("photo_url")
    public String getPhotoUrl() {
        return photoUrl;
    }

    @PropertyName("photo_url")
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
