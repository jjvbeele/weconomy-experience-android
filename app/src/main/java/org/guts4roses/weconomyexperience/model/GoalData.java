package org.guts4roses.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import org.guts4roses.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 23-8-17.
 */


@IgnoreExtraProperties
@Parcel
public class GoalData extends FireData {

    @PropertyName("text")
    protected String text;

    @PropertyName("text")
    protected String type;

    public GoalData() {
    }

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

    @PropertyName("type")
    public String getType() {
        return type;
    }

    @PropertyName("completed")
    public void setType(String type) {
        this.type = type;
    }
}
