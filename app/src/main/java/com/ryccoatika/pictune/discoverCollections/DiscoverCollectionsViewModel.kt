package com.ryccoatika.pictune.discoverCollections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DiscoverCollectionsViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<DiscoverCollectionViewState>()
    private var discoverCollectionJob: Job? = null
    private var loadMoreCollectionJob: Job? = null

    val viewState: LiveData<DiscoverCollectionViewState>
        get() = _viewState

    fun discoverCollections(page: Int? = null, perPage: Int? = null) {
        discoverCollectionJob?.cancel()
        discoverCollectionJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.discoverCollections(page, perPage).buffer().collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        DiscoverCollectionViewState.DiscoverLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        DiscoverCollectionViewState.DiscoverEmpty
                    )
                    is Resource.Error -> _viewState.postValue(
                        DiscoverCollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        DiscoverCollectionViewState.DiscoverSuccess(result.data)
                    )
                }
            }
        }
    }

    fun loadMoreCollections(page: Int? = null, perPage: Int? = null) {
        loadMoreCollectionJob?.cancel()
        loadMoreCollectionJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.discoverCollections(page, perPage).buffer().collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        DiscoverCollectionViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        DiscoverCollectionViewState.LoadMoreEmpty
                    )
                    is Resource.Error -> _viewState.postValue(
                        DiscoverCollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        DiscoverCollectionViewState.LoadMoreSuccess(result.data)
                    )
                }
            }
        }
    }
}