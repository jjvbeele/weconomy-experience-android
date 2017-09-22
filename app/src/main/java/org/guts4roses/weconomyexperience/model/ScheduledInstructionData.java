package org.guts4roses.weconomyexperience.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import org.guts4roses.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    protected Map<String, String> labourMap;
    @PropertyName("claim_list")
    protected Map<String, String> claimMap;

    //ignored for json parsing
    @Exclude
    protected InstructionData bindedInstructionData;

    public ScheduledInstructionData() {
    }

    @PropertyName("instruction_key")
    public String getInstructionKey() {
        return instructionKey;
    }

    @PropertyName("instruction_key")
    public void setInstructionKey(String instructionKey) {
        this.instructionKey = instructionKey;
    }

    @PropertyName("labour_list")
    public Map<String, String> getLabourMap() {
        if (labourMap == null) {
            labourMap = new HashMap<>();
        }
        return labourMap;
    }

    @Exclude
    public List<String> getLabourList() {
        List<String> labourList = new ArrayList<>();
        labourList.addAll(getLabourMap().values());
        return labourList;
    }

    @PropertyName("labour_list")
    public void setLabourMap(Map<String, String> labourMap) {
        this.labourMap = labourMap;
    }

    @Exclude
    public void setLabour(int index, String playerId) {
        //we add "0" as a workaround to force deserialization to String instead of integer
        //Otherwise, it is treated as a sparse array instead of a map
        //The 0 will later be lost when parsing back to an integer
        getLabourMap().put("0" + String.valueOf(index), playerId);
    }

    @Exclude
    public void removeLabour(int index) {
        //we add "0" as a workaround to force deserialization to String instead of integer
        //Otherwise, it is treated as a sparse array instead of a map
        //The 0 will later be lost when parsing back to an integer
        getLabourMap().remove("0" + String.valueOf(index));
    }

    @PropertyName("claim_list")
    public Map<String, String> getClaimMap() {
        if (claimMap == null) {
            claimMap = new HashMap<>();
        }
        return claimMap;
    }

    @Exclude
    public List<String> getClaimList() {
        List<String> claimlist = new ArrayList<>();
        claimlist.addAll(getClaimMap().values());
        return claimlist;
    }

    @PropertyName("claim_list")
    public void setClaimMap(Map<String, String> claimMap) {
        this.claimMap = claimMap;
    }

    @Exclude
    public void setClaim(int index, String playerId) {
        //we add "0" as a workaround to force deserialization to String instead of integer
        //Otherwise, it is treated as a sparse array instead of a map
        //The 0 will later be lost when parsing back to an integer
        getClaimMap().put("0" + String.valueOf(index), playerId);
    }

    @Exclude
    public void removeClaim(int index) {
        //we add "0" as a workaround to force deserialization to String instead of integer
        //Otherwise, it is treated as a sparse array instead of a map
        //The 0 will later be lost when parsing back to an integer
        getClaimMap().remove("0" + String.valueOf(index));
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

    @Exclude
    public void setData(ScheduledInstructionData data) {
        this.instructionKey = data.instructionKey;
        this.day = data.day;
        this.labourMap = data.labourMap;
        this.claimMap = data.claimMap;
    }
}
