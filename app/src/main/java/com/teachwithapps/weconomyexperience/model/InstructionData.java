package com.teachwithapps.weconomyexperience.model;

import com.teachwithapps.weconomyexperience.firebase.FireData;

/**
 * Created by mint on 1-8-17.
 */

public class InstructionData extends FireData {

    protected String text;
    protected int size;

    public InstructionData() {}

    public InstructionData(String text) {
        this.text = text;
        this.size = (int)(Math.random() * 6) + 1;
    }

    public String getText() {
        return text;
    }

    public int getSize() {
        return size;
    }
}
