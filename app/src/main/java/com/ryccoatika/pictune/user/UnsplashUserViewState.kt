package com.ryccoatika.pictune.user

import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import com.ryccoatika.pictune.db.UnsplashUserResponse

sealed class UnsplashUserViewState {
    object UserLoading: UnsplashUserViewState()
    data class UserSuccess(val response: UnsplashUserResponse): UnsplashUserViewState()
    data class UserError(val error: Throwable): UnsplashUserViewState()
}