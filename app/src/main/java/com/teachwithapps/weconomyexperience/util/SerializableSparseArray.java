package com.teachwithapps.weconomyexperience.util;

import android.util.SparseArray;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <E>
 * @author Asaf Pinhassi www.mobiledev.co.il
 */
@IgnoreExtraProperties
public class SerializableSparseArray<E> extends SparseArray<E> implements Serializable {

    @Exclude
    private static final long serialVersionUID = 824056059663678000L;

    public static <E> SerializableSparseArray<E> fromList(List<E> list) {
        SerializableSparseArray<E> sparseArray = new SerializableSparseArray<>();
        for (int i = 0; i < list.size(); i++) {
            sparseArray.put(i, list.get(i));
        }

        return sparseArray;
    }

    public SerializableSparseArray(int capacity) {
        super(capacity);
    }

    public SerializableSparseArray() {
        super();
    }

    public List<E> toList() {
        List<E> list = new ArrayList<>();
        int i = 0;
        while (list.size() < size()) {
            E e = super.get(i);
            if (e != null) {
                list.add(super.get(i));
            } else {
                list.add(null);
            }

            i++;
        }
        return list;
    }

    /**
     * This method is private but it is called using reflection by java
     * serialization mechanism. It overwrites the default object serialization.
     * <p>
     * <br/><br/><b>IMPORTANT</b>
     * The access modifier for this method MUST be set to <b>private</b> otherwise {@link java.io.StreamCorruptedException}
     * will be thrown.
     *
     * @param oos the stream the data is stored into
     * @throws IOException an exception that might occur during data storing
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        Object[] data = new Object[size()];

        for (int i = data.length - 1; i >= 0; i--) {
            Object[] pair = {keyAt(i), valueAt(i)};
            data[i] = pair;
        }
        oos.writeObject(data);
    }

    /**
     * This method is private but it is called using reflection by java
     * serialization mechanism. It overwrites the default object serialization.
     * <p>
     * <br/><br/><b>IMPORTANT</b>
     * The access modifier for this method MUST be set to <b>private</b> otherwise {@link java.io.StreamCorruptedException}
     * will be thrown.
     *
     * @param oos the stream the data is read from
     * @throws IOException            an exception that might occur during data reading
     * @throws ClassNotFoundException this exception will be raised when a class is read that is
     *                                not known to the current ClassLoader
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Object[] data = (Object[]) ois.readObject();
        for (int i = data.length - 1; i >= 0; i--) {
            Object[] pair = (Object[]) data[i];
            this.append((Integer) pair[0], (E) pair[1]);
        }
        return;
    }


}