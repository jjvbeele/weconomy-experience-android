package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.PropertyName;

import java.util.List;

/**
 * Created by mint on 1-8-17.
 */
public class HubData {

    @PropertyName("")
    public List<GameData> gameDataList;

    public List<GameData> getGameList() {
        return gameDataList;
    }
}
