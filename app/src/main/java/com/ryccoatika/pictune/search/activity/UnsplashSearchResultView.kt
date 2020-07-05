package com.ryccoatika.pictune.search.activity

import androidx.lifecycle.LiveData

interface UnsplashSearchResultView {
    val state: LiveData<UnsplashSearchResultViewState>
    fun searchPhotos(
        query: String,
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null)
    fun loadMoreSearchPhotos(
        query: String,
        page: Int = 1,
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null)
    fun searchCollections(query: String)
    fun loadMoreSearchCollections(query: String, page: Int = 1)
    fun searchUsers(query: String)
    fun loadMoreSearchUsers(query: String, page: Int = 1)

}