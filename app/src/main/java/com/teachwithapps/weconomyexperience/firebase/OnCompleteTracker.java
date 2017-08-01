package com.teachwithapps.weconomyexperience.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.teachwithapps.weconomyexperience.util.Returnable;

/**
 * Created by mint on 7-3-17.
 */

public class OnCompleteTracker<T> implements OnCompleteListener<T> {

    private Returnable<Task<T>> successReturnable;
    private int tasksToComplete;

    public OnCompleteTracker(int tasksToComplete, Returnable<Task<T>> successReturnable) {
        this.successReturnable = successReturnable;
        this.tasksToComplete = tasksToComplete;
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        tasksToComplete--;
        if (tasksToComplete <= 0) {
            successReturnable.onResult(task);
        }
    }
}
