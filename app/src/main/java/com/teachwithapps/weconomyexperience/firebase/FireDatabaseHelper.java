package com.teachwithapps.weconomyexperience.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teachwithapps.weconomyexperience.util.Returnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mint on 23-12-16.
 */

public class FireDatabaseHelper {

    private static final String TAG = FireDatabaseHelper.class.getName();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef;

    public FireDatabaseHelper() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseRef = firebaseDatabase.getReference();
    }

    public <T extends FireData> void getRecordArray(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<T> dataList = new ArrayList<T>();
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        T data = childData.getValue(dataResultClass);
                        data.setId(childData.getKey());
                        dataList.add(data);
                    }
                    returnOnSuccess.onResult(dataList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            });
        }
    }

    public <T> void getRecord(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    returnOnSuccess.onResult(dataSnapshot.getValue(dataResultClass));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            });
        }
    }

    public <T> String pushRecord(String[] locationArray, T data) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            DatabaseReference newRecordRef = locationRef.push();
            newRecordRef.setValue(data);

            return newRecordRef.getKey();

        } else {
            return null;
        }
    }

    public void removeRecord(String[] locationArray, String id) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            locationRef.child(id).removeValue();
        }
    }

    private DatabaseReference getLocationRef(String[] locationArray) {
        DatabaseReference locationRef = databaseRef;
        for (String location : locationArray) {
            if (locationRef != null && location == null) {
                return null;
            }

            locationRef = locationRef.child(location);
        }
        return locationRef;
    }



    public static String escapeChar(char escapeChar) {
        return "|" + Integer.toHexString(escapeChar) + "|";
    }

    public static String escape(String unescaped) {
        return unescaped
                .replace(".", escapeChar('.'))
                .replace(":", escapeChar(':'))
                .replace(";", escapeChar(';'))
                .replace("#", escapeChar('#'))
                .replace("$", escapeChar('$'))
                .replace("[", escapeChar('['))
                .replace("@", escapeChar('@'))
                .replace("\'", escapeChar('\''))
                .replace("\"", escapeChar('\"'))
                .replace("]", escapeChar(']'));
    }
}
