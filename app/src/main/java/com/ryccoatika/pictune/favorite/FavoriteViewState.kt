package com.ryccoatika.pictune.favorite

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class FavoriteViewState {
    data class Error(val message: String?) : FavoriteViewState()

    data class Success(val data: List<PhotoMinimal>?) : FavoriteViewState()
    object Loading : FavoriteViewState()
    object Empty : FavoriteViewState()
}