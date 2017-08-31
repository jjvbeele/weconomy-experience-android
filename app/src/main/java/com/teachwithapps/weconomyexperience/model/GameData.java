package com.teachwithapps.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;
import com.teachwithapps.weconomyexperience.BuildConfig;
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

    @PropertyName("library_key")
    private String libraryKey;

    @PropertyName("version")
    private String version;

    public GameData() {
    }

    public GameData(String name, String libraryKey) {
        this.name = name;
        this.libraryKey = libraryKey;
        this.version = String.valueOf(BuildConfig.FIREBASE_VERSION_CODE);
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("library_key")
    public String getLibraryKey() {
        return libraryKey;
    }

    @PropertyName("library_key")
    public void setLibraryKey(String libraryKey) {
        this.libraryKey = libraryKey;
    }

    @PropertyName("version")
    public String getVersion() {
        return version;
    }

    @PropertyName("version")
    public void setVersion(String version) {
        this.version = version;
    }
}
