package com.ryccoatika.pictune.discoverPhotos

import androidx.lifecycle.LiveData
import com.ryccoatika.pictune.db.UnsplashPhotoResponse

interface UnsplashDiscoverPhotosView {
    val state: LiveData<UnsplashDiscoverPhotosViewState>
    fun unsplashDiscover(orderBy: String = UnsplashPhotoResponse.ORDER_BY_LATEST)
    fun unsplashLoadMore(page: Int = 1, orderBy: String = UnsplashPhotoResponse.ORDER_BY_LATEST)
}