package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 26-7-17.
 */
@IgnoreExtraProperties
@Parcel
public class GameData extends FireData {

    public String id;

    @PropertyName("name")
    public String name;

    @PropertyName("password")
    public boolean password;

    @PropertyName("players")
    public int players;

    @PropertyName("state")
    public String state;

    public GameData() {
    }

    public GameData(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean getPassword() {
        return password;
    }

    public int getPlayers() {
        return players;
    }

    public String getState() {
        return state;
    }

}
