package com.ryccoatika.pictune.search.topic

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class TopicViewState {
    data class Error(val message: String?) : TopicViewState()

    // get topic photos
    object Loading : TopicViewState()
    object Empty : TopicViewState()
    data class Success(val data: List<PhotoMinimal>?) : TopicViewState()

    // load more photos
    object LoadMoreLoading : TopicViewState()
    object LoadMoreEmpty : TopicViewState()
    data class LoadMoreSuccess(val data: List<PhotoMinimal>?) : TopicViewState()
}