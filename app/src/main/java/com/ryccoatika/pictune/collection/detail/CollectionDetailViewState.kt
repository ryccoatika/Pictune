package com.ryccoatika.pictune.collection.detail

import com.ryccoatika.core.domain.model.CollectionDetail
import com.ryccoatika.core.domain.model.PhotoMinimal


sealed class CollectionDetailViewState {
    data class Error(val message: String?) : CollectionDetailViewState()

    // get collection
    object Loading : CollectionDetailViewState()
    data class Success(val data: CollectionDetail?) : CollectionDetailViewState()

    data class ListPhotosError(val message: String?) : CollectionDetailViewState()

    // get collectionPhoto
    object GetPhotosLoading : CollectionDetailViewState()
    object GetPhotosEmpty : CollectionDetailViewState()
    data class GetPhotosSuccess(val data: List<PhotoMinimal>?) : CollectionDetailViewState()

    // load more
    object LoadMorePhotosEmpty : CollectionDetailViewState()
    object LoadMorePhotoLoading : CollectionDetailViewState()
    data class LoadMorePhotoSuccess(val data: List<PhotoMinimal>?) : CollectionDetailViewState()
}