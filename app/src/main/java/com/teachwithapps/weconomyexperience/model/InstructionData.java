package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 1-8-17.
 */

@IgnoreExtraProperties
@Parcel
public class InstructionData extends FireData {

    @PropertyName("text")
    protected String text;

    @PropertyName("size")
    protected int size;

    public InstructionData() {}

    public InstructionData(String text) {
        this(text, (int)(Math.random() * 6) + 1);
    }

    public InstructionData(String text, int size) {
        this.text = text;
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public int getSize() {
        return size;
    }
}
