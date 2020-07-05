package com.ryccoatika.pictune.user

import androidx.lifecycle.LiveData

interface UnsplashUserView {
    val state: LiveData<UnsplashUserViewState>
    fun unsplashGetUser(username: String)
}