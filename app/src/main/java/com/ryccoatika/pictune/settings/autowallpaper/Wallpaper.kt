package com.ryccoatika.pictune.settings.autowallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log

class Wallpaper(val context: Context) {

    companion object {
        fun setWallpaper(context: Context, path: String) {
            val setWallpaper = Intent()
            setWallpaper.action = Intent.ACTION_ATTACH_DATA
            setWallpaper.addCategory(Intent.CATEGORY_DEFAULT)
            setWallpaper.setDataAndType(Uri.parse(path), "image/jpeg")
            setWallpaper.putExtra("mimeType", "image/jpeg")
            setWallpaper.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.startActivity(
                Intent.createChooser(setWallpaper, "Set as")
            )
        }
    }

    private val wallpaperManager = WallpaperManager.getInstance(context)

    fun setHomeScreenWallpaper(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= 24) {
            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM)
        }
    }

    fun setLockScreenWallpaper(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= 24)
            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK)
    }
}