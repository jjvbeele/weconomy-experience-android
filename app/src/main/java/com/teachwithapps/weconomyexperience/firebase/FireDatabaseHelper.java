package com.teachwithapps.weconomyexperience.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teachwithapps.weconomyexperience.firebase.util.Returnable;
import com.teachwithapps.weconomyexperience.firebase.util.ReturnableChange;
import com.teachwithapps.weconomyexperience.util.Log;

import java.lang.ref.WeakReference;
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

    /**
     * Retrieve one record from the firebase
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void getRecord(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {
        genericObserveRecord(
                dataResultClass,
                locationArray,
                new Returnable<T>() {
                    @Override
                    public void onResult(T data) {
                        returnOnSuccess.onResult(data);
                    }
                },
                returnOnFail,
                false,
                false
        );
    }

    /**
     * Retrieve records from the firebase asynchronously
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void getRecordsAsync(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {
        genericObserveRecord(
                dataResultClass,
                locationArray,
                new Returnable<T>() {
                    @Override
                    public void onResult(T data) {
                        returnOnSuccess.onResult(data);
                    }
                },
                returnOnFail,
                true,
                false);
    }

    /**
     * Retrieves a list of data from the database
     * The list is compiled by counting the amount of nodes and keep waiting
     * until it is filled. At that point, the callback 'returnOnSuccess' is called.
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void getRecordsList(
            final Class<T> dataResultClass,
            final String[] locationArray,
            final Returnable<List<T>> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {
        getChildCount(
                locationArray,
                new Returnable<Long>() {
                    @Override
                    public void onResult(final Long childCount) {
                        final List<T> list = new ArrayList<>();
                        genericObserveRecord(
                                dataResultClass,
                                locationArray,
                                new Returnable<T>() {
                                    @Override
                                    public void onResult(T data) {
                                        list.add(data);
                                        if (list.size() >= childCount) {
                                            returnOnSuccess.onResult(list);
                                        }
                                    }
                                },
                                returnOnFail,
                                true,
                                false);
                    }
                },
                returnOnFail
        );
    }

    /**
     * Observe record from the firebase
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
            final Returnable<DatabaseError> returnOnFail) {
        genericObserveRecord(
                dataResultClass,
                locationArray,
                new Returnable<T>() {
                    @Override
                    public void onResult(T data) {
                        returnOnSuccess.onResult(data);
                    }
                },
                returnOnFail,
                false,
                true);
    }

    /**
     * Retrieve number of children nodes on a specific location in the firebase
     *
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     */
    public void getChildCount(
            String[] locationArray,
            final Returnable<Long> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {

        DatabaseReference locationRef = getLocationRef(locationArray);
        if (locationRef != null) {
            locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    returnOnSuccess.onResult(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            });
        }
    }

    /**
     * observe record from the firebase, either once or continuously
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    private <T> void genericObserveRecord(
            final Class<T> dataResultClass,
            String[] locationArray,
            final Returnable<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail,
            final boolean loopChildren,
            boolean continuousListener) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordValueListener(locationRef, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!loopChildren) {
                        T data = dataSnapshot.getValue(dataResultClass);
                        handleFireData(data, dataSnapshot.getKey());
                        returnOnSuccess.onResult(data);

                    } else {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            T data = childDataSnapshot.getValue(dataResultClass);
                            handleFireData(data, childDataSnapshot.getKey());
                            returnOnSuccess.onResult(data);
                        }
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
     * If the data requested is inheriting from FireDataInterface
     * write the key value to the id
     *
     * @param data
     * @param id
     * @param <T>
     */
    private <T> void handleFireData(T data, String id) {
        if (data instanceof FireDataInterface) {
            ((FireDataInterface) data).setId(id);
        }
    }

    /**
     * Observes changes on children within a node
     *
     * @param dataResultClass
     * @param locationArray
     * @param returnOnSuccess
     * @param returnOnFail
     * @param <T>
     */
    public <T> void observeChild(
            final Class<T> dataResultClass,
            String[] locationArray,
            final ReturnableChange<T> returnOnSuccess,
            final Returnable<DatabaseError> returnOnFail) {

        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            addRecordChildListener(locationRef, new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        T data = dataSnapshot.getValue(dataResultClass);
                        handleFireData(data, dataSnapshot.getKey());
                        returnOnSuccess.onChildAdded(data);

                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error: corrupted database", e);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    try {
                        T data = dataSnapshot.getValue(dataResultClass);
                        handleFireData(data, dataSnapshot.getKey());
                        returnOnSuccess.onChildChanged(data);

                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error: corrupted database", e);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        T data = dataSnapshot.getValue(dataResultClass);
                        handleFireData(data, dataSnapshot.getKey());
                        returnOnSuccess.onChildRemoved(data);

                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error: corrupted database", e);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    try {
                        T data = dataSnapshot.getValue(dataResultClass);
                        handleFireData(data, dataSnapshot.getKey());
                        returnOnSuccess.onChildMoved(data);

                    } catch (DatabaseException e) {
                        Log.e(TAG, "Error: corrupted database", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    returnOnFail.onResult(databaseError);
                }
            });
        }
    }

    /**
     * Push a new record into the database, giving it a unique key identifier
     *
     * @param preKeyName
     * @param locationArray
     * @param data
     * @param <T>
     * @return
     */
    public <T> String pushRecord(String preKeyName, String[] locationArray, T data) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            DatabaseReference newRecordRef = locationRef.push();
            String key;
            if (preKeyName != null) {
                key = preKeyName + newRecordRef.getKey();
                newRecordRef.removeValue();
                newRecordRef = locationRef.child(key);

            } else {
                key = newRecordRef.getKey();
            }
            newRecordRef.setValue(data);

            handleFireData(data, newRecordRef.getKey());

            return key;

        } else {
            return null;
        }
    }

    /**
     * Add a record to firebase database.
     * An existing record on the given key node will be replaced
     *
     * @param locationArray
     * @param key
     * @param data
     * @param <T>
     */
    public <T> void addRecord(String[] locationArray, String key, T data) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            locationRef.child(key).setValue(data);
        }
    }

    /**
     * Remove a record from the firebase database
     *
     * @param locationArray
     */
    public void removeRecord(String[] locationArray) {
        DatabaseReference locationRef = getLocationRef(locationArray);

        if (locationRef != null) {
            locationRef.removeValue();
            Log.d(TAG, "Removing record " + Arrays.toString(locationArray));
        }
    }

    private void addRecordValueListener(DatabaseReference ref, ValueEventListener valueEventListener, boolean continuous) {
        if (continuous) {
            ref.addValueEventListener(valueEventListener);

        } else {
            ref.addListenerForSingleValueEvent(valueEventListener);
        }
    }

    private void addRecordChildListener(DatabaseReference ref, ChildEventListener childEventListener) {
        ref.addChildEventListener(childEventListener);
    }

    private DatabaseReference getLocationRef(String[] locationArray) {
        DatabaseReference locationRef = databaseRef;
        for (String location : locationArray) {
            if (location == null) {
                return null;
            }

            locationRef = locationRef.child(location);

            if (locationRef == null) {
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
