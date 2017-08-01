package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

/**
 * Created by mint on 1-8-17.
 */

public class InstructionData extends FireData {

    public enum ListItemType {
        TYPE_ADD_BUTTON
    }

    private ListItemType listItemType;

    @PropertyName("text")
    protected String text;

    @PropertyName("size")
    protected int size;

    public InstructionData() {}

    public InstructionData(ListItemType type) {
        setType(type);
        size = 1;
    }

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

    public ListItemType getType() {
        return listItemType;
    }

    public void setType(ListItemType type) {
        this.listItemType = type;
    }
}
