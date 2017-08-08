package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

@IgnoreExtraProperties
@Parcel
public class ScheduledInstructionData extends FireData {

    @PropertyName("instruction_key")
    protected String instructionKey;

    @PropertyName("day")
    protected int day;

    @PropertyName("labour_list")
    protected List<String> labourList;

    @PropertyName("claim_list")
    protected List<String> claimList;

    public ScheduledInstructionData() {}

    public String getInstructionKey() {
        return instructionKey;
    }

    public void setInstructionKey(String instructionKey) {
        this.instructionKey = instructionKey;
    }

    public List<String> getLabourList() {
        return labourList;
    }

    public void setLabourList(List<String> labourList) {
        this.labourList = labourList;
    }

    public List<String> getClaimList() {
        return claimList;
    }

    public void setClaimList(List<String> claimList) {
        this.claimList = claimList;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
