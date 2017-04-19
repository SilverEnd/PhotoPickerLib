package com.erikzuo.photopicker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by YifanZuo on 5/07/2016.
 */
class IOHelper {

    public static File createFile(String filename, String directory) {
        File mediaStorageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_PICTURES), directory);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Error", "Oops! Failed create " + directory);
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator + filename + "_" + timeStamp + ".jpg");
    }

}
