package org.guts4roses.weconomyexperience.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mint on 26-8-17.
 */

public class SecurityUtil {

    private static final String TAG = SecurityUtil.class.getName();

    public static String encode(String encoding, String message) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(encoding);
            byte[] byteResult = messageDigest.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(byte b : byteResult) {
                sb.append(String.format("%02X", b));
            }
            Log.d(TAG, sb.toString());
            return sb.toString();

        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, "Can't encode message", e);

        } catch(NoSuchAlgorithmException e) {
            Log.e(TAG, "Can't encode message", e);
        }

        return null;
    }
}
