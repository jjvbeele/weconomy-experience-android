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

    @PropertyName("planning")
    protected PlanningData planningData;

    @PropertyName("instruction_library")
    protected String instructionLibraryKey;

    public GameData() {
    }

    public GameData(String name, String libraryKey) {
        this.name = name;
        this.instructionLibraryKey = libraryKey;
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

    public PlanningData getPlanningData() {
        return planningData;
    }

    public void setPlanningData(PlanningData planningData) {
        this.planningData = planningData;
    }

    public String getInstructionLibraryKey() {
        return instructionLibraryKey;
    }

    public void setInstructionLibraryKey(String instructionLibraryKey) {
        this.instructionLibraryKey = instructionLibraryKey;
    }

}
