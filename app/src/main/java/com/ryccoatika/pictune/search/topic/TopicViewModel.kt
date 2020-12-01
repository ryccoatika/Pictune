package com.ryccoatika.pictune.search.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TopicViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<TopicViewState>(TopicViewState.Loading)
    private var getTopicPhotosJob: Job? = null
    private var loadMoreTopicPhotoJob: Job? = null

    val viewState: LiveData<TopicViewState>
        get() = _viewState

    fun getTopicPhotos(
        idOrSlug: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null,
        orderBy: String? = null
    ) {
        getTopicPhotosJob?.cancel()
        getTopicPhotosJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.getTopicPhotos(idOrSlug, page, perPage, orientation, orderBy)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            TopicViewState.Loading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            TopicViewState.Empty
                        )
                        is Resource.Error -> _viewState.postValue(
                            TopicViewState.Error(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            TopicViewState.Success(result.data)
                        )
                    }
                }
        }
    }

    fun loadMoreTopicPhotos(
        idOrSlug: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null,
        orderBy: String? = null
    ) {
        loadMoreTopicPhotoJob?.cancel()
        loadMoreTopicPhotoJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.getTopicPhotos(idOrSlug, page, perPage, orientation, orderBy)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            TopicViewState.LoadMoreLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            TopicViewState.LoadMoreEmpty
                        )
                        is Resource.Error -> _viewState.postValue(
                            TopicViewState.Error(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            TopicViewState.LoadMoreSuccess(result.data)
                        )
                    }
                }
        }
    }
}