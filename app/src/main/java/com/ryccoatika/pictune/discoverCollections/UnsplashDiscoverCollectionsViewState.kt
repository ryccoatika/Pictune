package com.ryccoatika.pictune.discoverCollections

import com.ryccoatika.pictune.db.UnsplashCollectionResponse

sealed class UnsplashDiscoverCollectionsViewState {
    object DiscoverLoading : UnsplashDiscoverCollectionsViewState()
    object LoadMoreLoading : UnsplashDiscoverCollectionsViewState()
    data class DiscoverSuccess(val responses: List<UnsplashCollectionResponse?>) : UnsplashDiscoverCollectionsViewState()
    data class LoadMoreSuccess(val responses: List<UnsplashCollectionResponse?>) : UnsplashDiscoverCollectionsViewState()
    data class DiscoverError(val error: Throwable) : UnsplashDiscoverCollectionsViewState()
    data class LoadMoreError(val error: Throwable) : UnsplashDiscoverCollectionsViewState()
}