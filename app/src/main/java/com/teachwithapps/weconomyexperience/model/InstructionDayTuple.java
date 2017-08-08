package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 8-8-17.
 */

@IgnoreExtraProperties
@Parcel
public class InstructionDayTuple extends FireData {

    @PropertyName("day")
    protected int day;

    @PropertyName("instruction_name")
    protected String instructionName;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

}
