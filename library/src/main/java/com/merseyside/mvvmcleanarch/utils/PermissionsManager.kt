package com.merseyside.mvvmcleanarch.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

/**
 * Created by ivan_ on 10.12.2017.
 */
object PermissionsManager {
    fun verifyStoragePermissions(
        activity: Activity?,
        PERMISSIONS: Array<String>,
        code: Int
    ) {
        if (!isPermissionsGranted(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                activity!!,
                PERMISSIONS,
                code
            )
        }
    }

    fun verifyStoragePermissions(
        fragment: Fragment,
        PERMISSIONS: Array<String>,
        code: Int
    ) {
        if (!isPermissionsGranted(fragment.context, PERMISSIONS)) {
            fragment.requestPermissions(
                PERMISSIONS,
                code
            )
        }
    }

    fun isPermissionsGranted(
        context: Context?,
        PERMISSIONS_STORAGE: Array<String>
    ): Boolean {
        for (permission in PERMISSIONS_STORAGE) {
            val granted = ActivityCompat.checkSelfPermission(context!!, permission)
            if (granted != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}