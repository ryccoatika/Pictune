package com.ryccoatika.pictune

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

class PermissionManager {
    companion object {
        const val WRITE_EXTERNAL_REQUEST_CODE = 100

        fun isWriteExternalPermissionGranted(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val result = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                result == PackageManager.PERMISSION_GRANTED
            } else true
        }

        fun requestWriteExternalPermission(context: Context) {
            if (!isWriteExternalPermissionGranted(context))
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_REQUEST_CODE
                )
        }
    }
}