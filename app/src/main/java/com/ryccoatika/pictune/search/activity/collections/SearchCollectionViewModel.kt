package com.ryccoatika.pictune.search.activity.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchCollectionViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState =
        MutableLiveData<SearchCollectionViewState>(SearchCollectionViewState.Loading)
    private var discoverCollectionJob: Job? = null
    private var loadMoreCollectionJob: Job? = null

    val viewStateSearch: LiveData<SearchCollectionViewState>
        get() = _viewState

    fun searchCollections(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ) {
        discoverCollectionJob?.cancel()
        discoverCollectionJob = viewModelScope.launch {
            unsplashInteractor.searchCollection(query, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchCollectionViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchCollectionViewState.Empty
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchCollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchCollectionViewState.Success(result.data)
                    )
                }
            }
        }
    }

    fun loadMoreCollections(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ) {
        loadMoreCollectionJob?.cancel()
        loadMoreCollectionJob = viewModelScope.launch {
            unsplashInteractor.searchCollection(query, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchCollectionViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchCollectionViewState.LoadMoreEmpty
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchCollectionViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchCollectionViewState.LoadMoreSuccess(result.data)
                    )
                }
            }
        }
    }
}