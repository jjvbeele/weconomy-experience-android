package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by mint on 8-8-17.
 */

@IgnoreExtraProperties
@Parcel
public class PlanningData extends FireData {

    @PropertyName("instruction_list")
    protected List<InstructionDayTuple> instructionDayTupleList;

    public List<InstructionDayTuple> getInstructionDayTupleList() {
        return instructionDayTupleList;
    }

    public void setInstructionDayTupleList(List<InstructionDayTuple> instructionDayTupleList) {
        this.instructionDayTupleList = instructionDayTupleList;
    }
}
