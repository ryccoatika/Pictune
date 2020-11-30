package com.ryccoatika.pictune.user

import com.ryccoatika.core.domain.model.UserDetail

sealed class UserViewState {
    object Loading : UserViewState()
    data class Success(val data: UserDetail?) : UserViewState()
    data class Error(val message: String?) : UserViewState()
}