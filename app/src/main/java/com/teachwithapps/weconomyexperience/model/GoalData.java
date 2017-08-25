package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 23-8-17.
 */


@IgnoreExtraProperties
@Parcel
public class GoalData extends FireData {

    private String text;

    private boolean completed;

    public GoalData() {}

    public GoalData(String text) {
        this.text = text;
    }


    @PropertyName("text")
    public String getText() {
        return text;
    }

    @PropertyName("text")
    public void setText(String text) {
        this.text = text;
    }

    @PropertyName("completed")
    public boolean isCompleted() {
        return completed;
    }

    @PropertyName("completed")
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}