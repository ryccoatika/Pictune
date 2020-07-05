package com.ryccoatika.pictune.favorite

import androidx.lifecycle.LiveData

interface FavoriteView {
    val state: LiveData<FavoriteViewState>
    fun getAllFavoritePhotos()
    fun getUnsplashPhotoFromInternet(id: String)
}