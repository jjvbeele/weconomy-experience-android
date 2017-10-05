package org.guts4roses.weconomyexperience.util;


import org.guts4roses.weconomyexperience.BuildConfig;

/**
 * Created by mint on 21-9-16.
 */

public class Log {

    public static void d(String tag, String message) {
        if (message == null) {
            message = "Message was null";
        }
        if (BuildConfig.DEBUG) {
            android.util.Log.d(tag, message);

        } else {
            //Fabric.getLogger().d(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable error) {
        if (message == null) {
            message = "Message was null";
        }
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, message, error);

        } else {
//            Fabric.getLogger().e(tag, message, error);
        }
    }

}
