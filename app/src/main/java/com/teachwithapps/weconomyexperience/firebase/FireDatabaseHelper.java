package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teachwithapps.weconomyexperience.util.Log;
import com.teachwithapps.weconomyexperience.util.Returnable;

import java.util.ArrayList;
import java.util.Arrays;
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

    public <T> void observeRecordArray(
            Class<T> dataClass,
            String location,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {
        observeRecordArray(dataClass, new String[]{location}, returnOnSuccess, returnOnFail, continuousListener);
    }

    public <T> void observeRecordArray(
            final Class<T> dataClass,
            String[] locationArray,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordValueListener(locationRef, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<T> dataList = new ArrayList<>();
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        T dataValue = childData.getValue(dataClass);
                        dataList.add(dataValue);
                    }
                    returnOnSuccess.onResult(dataList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            }, continuousListener);
        }
    }

    /**
     * Returns array of dataclass inheriting FireData, which ID will be set as its firebase key
     *
     * @param dataResultClass
     * @param location
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T extends FireData> void observeRecordFireDataArray(
            final Class<T> dataResultClass,
            String location,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {
        observeRecordFireDataArray(dataResultClass, new String[]{location}, returnOnSuccess, returnOnFail, continuousListener);
    }

    /**
     * Returns array of dataclass inheriting FireData, which ID will be set as its firebase key
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T extends FireData> void observeRecordFireDataArray(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordValueListener(locationRef, new ValueEventListener() {
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
            }, continuousListener);
        }
    }

    public <T extends FireData> void observeRecordFireData(
            final Class<T> dataResultClass,
            String location,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {
        observeRecordFireData(dataResultClass, new String[]{location}, returnOnSuccess, returnOnFail, continuousListener);
    }

    public <T extends FireData> void observeRecordFireData(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordValueListener(locationRef, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    T data = dataSnapshot.getValue(dataResultClass);
                    if(data != null) {
                        data.setId(dataSnapshot.getKey());
                        returnOnSuccess.onResult(data);
                    } else {
                        returnOnFail.onResult(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            }, continuousListener);
        }
    }

    /**
     * Retrieve one record from the firebase
     *
     * @param dataResultClass
     * @param location
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void observeRecord(
            final Class<T> dataResultClass,
            String location,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {
        observeRecord(dataResultClass, new String[]{location}, returnOnSuccess, returnOnFail, continuousListener);
    }

    /**
     * Retrieve one record from the firebase
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void observeRecord(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            boolean continuousListener) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordValueListener(locationRef, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    returnOnSuccess.onResult(dataSnapshot.getValue(dataResultClass));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            }, continuousListener);
        }
    }

    public <T> String pushRecord(String location, T data) {
        return pushRecord(new String[]{location}, data);
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

    public <T> void addRecord(String location, String key, T data) {
        addRecord(new String[] {location}, key, data);
    }

    public <T> void addRecord(String[] locationArray, String key, T data) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if(locationRef != null) {
            locationRef.child(key).setValue(data);
        }
    }

    public void removeRecord(String location) {
        removeRecord(new String[]{location});
    }

    public void removeRecord(String[] locationArray) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            Log.d(TAG, "Removing " + Arrays.deepToString(locationArray));
            locationRef.removeValue();
        }
    }

    private void addRecordValueListener(DatabaseReference ref, ValueEventListener valueEventListener, boolean continuous) {
        if(continuous) {
            ref.addValueEventListener(valueEventListener);

        } else {
            ref.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    private DatabaseReference getLocationRef(String[] locationArray) {
        DatabaseReference locationRef = databaseRef;
        for (String location : locationArray) {
            if (location == null) {
                Log.d(TAG, "Location is null");
                return null;
            }

            locationRef = locationRef.child(location);

            if (locationRef == null) {
                Log.d(TAG, "Location does not exist");
                return null;
            }
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
