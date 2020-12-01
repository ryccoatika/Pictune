package com.ryccoatika.pictune.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionHelper {

    const val REQUEST_CODE = 100

    private fun shouldAskPermission(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    }

    fun shouldAskPermission(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) {
            val permissionResult = ActivityCompat.checkSelfPermission(context, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED)
                return true
        }
        return false
    }

    fun askPermission(context: Context, permission: List<String>) {
        permission.forEach {
            if (shouldAskPermission(context, it)) {
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    permission.toTypedArray(),
                    REQUEST_CODE
                )
            }
        }
    }
}