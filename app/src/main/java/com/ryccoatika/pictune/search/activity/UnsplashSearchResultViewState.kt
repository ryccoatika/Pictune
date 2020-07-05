package com.ryccoatika.pictune.search.activity

import com.ryccoatika.pictune.db.UnsplashSearchCollectionsResponse
import com.ryccoatika.pictune.db.UnsplashSearchPhotosResponse
import com.ryccoatika.pictune.db.UnsplashSearchUsersResponse

sealed class UnsplashSearchResultViewState {
    object Loading : UnsplashSearchResultViewState()
    object LoadMoreLoading : UnsplashSearchResultViewState()
    data class PhotosSuccess(val response: UnsplashSearchPhotosResponse) : UnsplashSearchResultViewState()
    data class CollectionsSuccess(val response: UnsplashSearchCollectionsResponse) : UnsplashSearchResultViewState()
    data class UsersSuccess(val response: UnsplashSearchUsersResponse) : UnsplashSearchResultViewState()
    data class LoadMorePhotosSuccess(val response: UnsplashSearchPhotosResponse) : UnsplashSearchResultViewState()
    data class LoadMoreCollectionsSuccess(val response: UnsplashSearchCollectionsResponse) : UnsplashSearchResultViewState()
    data class LoadMoreUsersSuccess(val response: UnsplashSearchUsersResponse) : UnsplashSearchResultViewState()
    data class Error(val error: Throwable) : UnsplashSearchResultViewState()
    data class LoadMoreError(val error: Throwable) : UnsplashSearchResultViewState()
}