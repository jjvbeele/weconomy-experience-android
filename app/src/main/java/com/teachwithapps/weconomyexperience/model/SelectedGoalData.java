package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 23-8-17.
 */


@IgnoreExtraProperties
@Parcel
public class SelectedGoalData extends FireData {

    @PropertyName("realised")
    private boolean realised;

    @PropertyName("goal_id")
    private String goalId;

    @PropertyName("player_id")
    private String playerId;

    @Exclude
    private GoalData goalData;

    @Exclude
    private PlayerData playerData;

    public SelectedGoalData() {
    }

    @PropertyName("goal_id")
    public String getGoalId() {
        return goalId;
    }

    @PropertyName("goal_id")
    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    @PropertyName("player_id")
    public String getPlayerId() {
        return playerId;
    }

    @PropertyName("player_id")
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @PropertyName("realised")
    public boolean getRealised() {
        return realised;
    }

    @PropertyName("realised")
    public void setRealised(boolean realised) {
        this.realised = realised;
    }

    @Exclude
    public GoalData getGoalData() {
        return goalData;
    }

    @Exclude
    public void bindGoalData(GoalData goalData) {
        this.goalData = goalData;
    }

    @Exclude
    public PlayerData getPlayerData() {
        return playerData;
    }

    @Exclude
    public void bindPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }
}
