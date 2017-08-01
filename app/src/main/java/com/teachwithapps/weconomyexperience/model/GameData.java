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

    @PropertyName("name")
    protected String name;

    @PropertyName("password")
    protected boolean password;

    @PropertyName("players")
    protected int players;

    @PropertyName("state")
    protected String state;

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
