package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.firebase.FireData;

import org.parceler.Parcel;

/**
 * Created by mint on 26-7-17.
 */
@IgnoreExtraProperties
@Parcel
public class GameData extends FireData {

    @PropertyName("name")
    private String name;

    @PropertyName("instruction_library_key")
    private String instructionLibraryKey;

    public GameData() {
    }

    public GameData(String name, String libraryKey) {
        this.name = name;
        this.instructionLibraryKey = libraryKey;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("instruction_library_key")
    public String getInstructionLibraryKey() {
        return instructionLibraryKey;
    }

    @PropertyName("instruction_library_key")
    public void setInstructionLibraryKey(String instructionLibraryKey) {
        this.instructionLibraryKey = instructionLibraryKey;
    }
}
