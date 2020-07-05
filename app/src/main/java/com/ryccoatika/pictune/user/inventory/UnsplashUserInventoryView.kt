package com.ryccoatika.pictune.user.inventory

import androidx.lifecycle.LiveData

interface UnsplashUserInventoryView {
    val state: LiveData<UnsplashUserInventoryViewState>
    fun getUserPhotos(username: String)
    fun loadMoreUserPhotos(username: String, page: Int)
    fun getUserLikes(username: String)
    fun loadMoreUserLikes(username: String, page: Int)
    fun getUserCollections(username: String)
    fun loadMoreUserCollections(username: String, page: Int)
}