package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;
import com.teachwithapps.weconomyexperience.firebase.FireDataInterface;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mint on 1-8-17.
 */

@IgnoreExtraProperties
@Parcel
public class ScheduledInstructionData extends FireData {

    @PropertyName("instruction_key")
    private String instructionKey;
    @PropertyName("day")
    private int day;
    @PropertyName("labour_list")
    private List<String> labourList;
    @PropertyName("claim_list")
    private List<String> claimList;

    //ignored for json parsing
    @Exclude
    private InstructionData bindedInstructionData;

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
        if(labourList == null) {
            labourList = new ArrayList<>();
        }
        return labourList;
    }

    @PropertyName("labour_list")
    public void setLabourList(List<String> labourList) {
        this.labourList = labourList;
    }

    @PropertyName("claim_list")
    public List<String> getClaimList() {
        if(claimList == null) {
            claimList = new ArrayList<>();
        }
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

    @Exclude
    public void bindInstructionData(InstructionData instructionData) {
        this.bindedInstructionData = instructionData;
    }

    @Exclude
    public InstructionData getBindedInstructionData() {
        return bindedInstructionData;
    }
}
