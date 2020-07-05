package com.ryccoatika.pictune.search.category

import androidx.lifecycle.LiveData

interface UnsplashCategoryView {
    val state: LiveData<UnsplashCategoryViewState>
    fun searchCategory(category: String)
    fun loadMore(category: String, page: Int)
}