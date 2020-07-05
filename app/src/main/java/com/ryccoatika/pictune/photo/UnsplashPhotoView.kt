package com.ryccoatika.pictune.photo

import androidx.lifecycle.LiveData

interface UnsplashPhotoView {
    val state: LiveData<UnsplashPhotoViewState>
    fun getPhotoStatistics(id: String)
}