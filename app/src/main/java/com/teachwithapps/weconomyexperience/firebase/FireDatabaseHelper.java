package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teachwithapps.weconomyexperience.util.Returnable;

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

    public <T> void getRecord(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {

        DatabaseReference locationRef = null;
        for (String location : locationArray) {
            if (location == null) {
                returnOnFail.onResult(null);
                return;
            }

            locationRef = databaseRef.child(location);
        }

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

    private String escapeChar(char escapeChar) {
        return "|" + Integer.toHexString(escapeChar) + "|";
    }

    private String escape(String unescaped) {
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
