package com.ryccoatika.pictune.search

import com.ryccoatika.core.domain.model.TopicMinimal

sealed class SearchViewState {
    data class Error(val message: String?) : SearchViewState()

    // discover topic
    object Loading : SearchViewState()
    object Empty : SearchViewState()
    data class Success(val data: List<TopicMinimal>?) : SearchViewState()

    // load more topic
    object LoadMoreLoading : SearchViewState()
    object LoadMoreEmpty : SearchViewState()
    data class LoadMoreSuccess(val data: List<TopicMinimal>?) : SearchViewState()
}