package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.Exclude;
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

    private String instructionKey;
    private int day;
    private List<String> labourList;
    private List<String> claimList;

    public ScheduledInstructionData() {}

    @PropertyName("instruction_key")
    public String getInstructionKey() {
        return instructionKey;
    }

    @PropertyName("instruction_key")
    public void setInstructionKey(String instructionKey) {
        this.instructionKey = instructionKey;
    }

    @PropertyName("labour_list")
    public List<String> getLabourList() {
        return labourList;
    }

    @PropertyName("labour_list")
    public void setLabourList(List<String> labourList) {
        this.labourList = labourList;
    }

    @PropertyName("claim_list")
    public List<String> getClaimList() {
        return claimList;
    }

    @PropertyName("claim_list")
    public void setClaimList(List<String> claimList) {
        this.claimList = claimList;
    }

    @PropertyName("day")
    public int getDay() {
        return day;
    }

    @PropertyName("day")
    public void setDay(int day) {
        this.day = day;
    }
}
