package org.guts4roses.weconomyexperience.firebase;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

/**
 * Created by mint on 16-8-17.
 * Implementation of FireDataInterface for convenience
 */
@Parcel
public class FireData implements FireDataInterface {

    @Exclude
    protected String id;

    @Exclude
    public String getId() {
        return this.id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

}
