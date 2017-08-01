package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.PropertyName;

/**
 * Created by mint on 26-7-17.
 */

public class GameData {

    @PropertyName("name")
    public String name;

    @PropertyName("password")
    public boolean password;

    @PropertyName("players")
    public int players;

    @PropertyName("state")
    public String state;

    public GameData() {}

    public GameData(String name) {
        this.name = name;
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
