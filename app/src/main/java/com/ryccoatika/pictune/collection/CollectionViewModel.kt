package com.ryccoatika.pictune.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CollectionViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<CollectionViewState>(CollectionViewState.Loading)
    private var discoverCollectionJob: Job? = null
    private var loadMoreCollectionJob: Job? = null

    val viewState: LiveData<CollectionViewState>
        get() = _viewState

    fun discoverCollections(page: Int? = null, perPage: Int? = null) {
        discoverCollectionJob?.cancel()
        discoverCollectionJob = viewModelScope.launch {
            unsplashInteractor.discoverCollections(page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        CollectionViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        CollectionViewState.Empty
                    )
                    is Resource.Error -> _viewState.postValue(
                        CollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        CollectionViewState.Success(result.data)
                    )
                }
            }
        }
    }

    fun loadMoreCollections(page: Int? = null, perPage: Int? = null) {
        loadMoreCollectionJob?.cancel()
        loadMoreCollectionJob = viewModelScope.launch {
            unsplashInteractor.discoverCollections(page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        CollectionViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        CollectionViewState.LoadMoreEmpty
                    )
                    is Resource.Error -> _viewState.postValue(
                        CollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        CollectionViewState.LoadMoreSuccess(result.data)
                    )
                }
            }
        }
    }
}