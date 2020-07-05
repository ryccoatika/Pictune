package com.ryccoatika.pictune.collection

import androidx.lifecycle.LiveData

interface UnsplashCollectionView {
    val state: LiveData<UnsplashCollectionViewState>
    fun getRelatedCollections(id: Int)
    fun getPhotosOfCollections(id: Int)
    fun loadMorePhotos(id: Int, page: Int)
}