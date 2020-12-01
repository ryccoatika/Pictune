package com.ryccoatika.pictune.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.utils.GlideApp
import com.ryccoatika.pictune.utils.dirSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(
    private val context: Context
) : ViewModel() {

    private val _cacheSize = MutableLiveData(getGlideCacheSize())
    val cacheSize: LiveData<Long>
        get() = _cacheSize

    private fun getGlideCacheSize(): Long = GlideApp.getPhotoCacheDir(context)?.dirSize() ?: 0

    fun clearCache() {
        viewModelScope.launch {
            launch(Dispatchers.Default) {
                GlideApp.get(context).clearDiskCache()
                _cacheSize.postValue(getGlideCacheSize())
            }.start()
        }
    }
}