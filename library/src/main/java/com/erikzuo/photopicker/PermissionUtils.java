package com.erikzuo.photopicker;

import android.content.pm.PackageManager;

/**
 * Created by YifanZuo on 5/07/2016.
 */
class PermissionUtils {

    static boolean isPermissionGranted(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
