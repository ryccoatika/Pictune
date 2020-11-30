package com.ryccoatika.pictune.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<SearchViewState>()
    private var discoverTopicJob: Job? = null
    private var loadMoreTopicJob: Job? = null

    val viewState: LiveData<SearchViewState>
        get() = _viewState

    fun discoverTopic(
        ids: String? = null,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null
    ) {
        discoverTopicJob?.cancel()
        discoverTopicJob = viewModelScope.launch {
            unsplashInteractor.discoverTopic(ids, page, perPage, orderBy).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchViewState.Empty
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchViewState.Success(result.data)
                    )
                }
            }
        }
    }

    fun loadMoreTopic(
        ids: String? = null,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null
    ) {
        loadMoreTopicJob?.cancel()
        loadMoreTopicJob = viewModelScope.launch {
            unsplashInteractor.discoverTopic(ids, page, perPage, orderBy).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchViewState.LoadMoreEmpty
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchViewState.LoadMoreSuccess(result.data)
                    )
                }
            }
        }
    }
}