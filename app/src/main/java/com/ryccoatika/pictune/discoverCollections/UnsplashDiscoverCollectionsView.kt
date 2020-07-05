package com.ryccoatika.pictune.discoverCollections

import androidx.lifecycle.LiveData

interface UnsplashDiscoverCollectionsView {
    val state: LiveData<UnsplashDiscoverCollectionsViewState>
    fun discoverCollections(filter: String)
    fun loadMoreCollections(page: Int, filter: String)
}