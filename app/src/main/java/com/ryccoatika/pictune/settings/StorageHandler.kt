package com.ryccoatika.pictune.settings

import android.content.Context
import java.io.File

class StorageHandler {
    companion object {
        fun getCacheSize(context: Context): Double {
            var size = 0L
            size += getDirSize(context.cacheDir)
            size += getDirSize(context.externalCacheDir)
            return (size.toDouble()/1_000_000)
        }

        fun getDataSize(context: Context): Double {
            var size = 0L
            size += getDirSize(context.filesDir)
            size += getDirSize(context.getExternalFilesDir(null))
            return (size.toDouble()/1_000_000)
        }

        fun clearCache(context: Context) {
            deleteDir(context.cacheDir)
            deleteDir(context.externalCacheDir)
        }

        fun clearData(context: Context) {
            deleteDir(context.filesDir)
            deleteDir(context.getExternalFilesDir(null))
        }

        private fun getDirSize(file: File?): Long {
            var size = 0L
            file?.listFiles()?.forEach {
                if (it == null) return 0L
                size += if (it.isDirectory) {
                    getDirSize(it)
                } else {
                    it.length()
                }
            }
            return size
        }

        private fun deleteDir(file: File?) {
            file?.listFiles()?.forEach {
                if (it == null) return
                if (it.isDirectory)
                    deleteDir(it)
                else
                    it.delete()
            }
        }
    }
}