package com.upstream.basemvvmimpl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

/**
 * Created by ivan_ on 10.12.2017.
 */

public class PermissionsManager {

    public static void verifyStoragePermissions(Activity activity, String[] PERMISSIONS, int code) {

        if (!isPermissionsGranted(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    code);
        }
    }

    public static void verifyStoragePermissions(Fragment fragment, String[] PERMISSIONS, int code) {

        if (!isPermissionsGranted(fragment.getContext(), PERMISSIONS)) {
            fragment.requestPermissions(
                    PERMISSIONS,
                    code);
        }
    }

    public static boolean isPermissionsGranted(Context context, String[] PERMISSIONS_STORAGE) {
        for (String permission : PERMISSIONS_STORAGE) {
            int granted = ActivityCompat.checkSelfPermission(context, permission);
            if (granted != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}
