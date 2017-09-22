package org.guts4roses.weconomyexperience.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import org.guts4roses.weconomyexperience.BuildConfig;
import org.guts4roses.weconomyexperience.firebase.FireData;
import org.parceler.Parcel;

/**
 * Created by mint on 26-7-17.
 */
@IgnoreExtraProperties
@Parcel
public class GameData extends FireData {

    @PropertyName("name")
    protected String name;

    @PropertyName("library_key")
    protected String libraryKey;

    @PropertyName("version")
    protected String version;

    public GameData() {
    }

    public GameData(String name, String libraryKey) {
        this.name = name;
        this.libraryKey = libraryKey;
        this.version = String.valueOf(BuildConfig.FIREBASE_MAX_VERSION_CODE);
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
