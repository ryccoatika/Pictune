package com.ryccoatika.pictune.photo.detail

import com.ryccoatika.core.domain.model.PhotoDetail


sealed class PhotoDetailViewState {
    object Loading : PhotoDetailViewState()
    data class Success(val data: PhotoDetail?) : PhotoDetailViewState()
    data class Error(val message: String?) : PhotoDetailViewState()
}