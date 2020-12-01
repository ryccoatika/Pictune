package com.ryccoatika.pictune.search.activity.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchPhotoViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<SearchPhotoViewState>(SearchPhotoViewState.Loading)
    private var searchPhotoJob: Job? = null
    private var loadMorePhotoJob: Job? = null

    val viewState: LiveData<SearchPhotoViewState>
        get() = _viewState

    fun searchPhoto(
        query: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        color: String? = null,
        orientation: String? = null
    ) {
        searchPhotoJob?.cancel()
        searchPhotoJob = viewModelScope.launch {
            unsplashInteractor.searchPhoto(query, page, perPage, orderBy, color, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            SearchPhotoViewState.Loading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            SearchPhotoViewState.Empty
                        )
                        is Resource.Success -> _viewState.postValue(
                            SearchPhotoViewState.Success(result.data)
                        )
                        is Resource.Error -> _viewState.postValue(
                            SearchPhotoViewState.Error(result.message)
                        )
                    }
                }
        }
    }

    fun loadMorePhoto(
        query: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        color: String? = null,
        orientation: String? = null
    ) {
        loadMorePhotoJob?.cancel()
        loadMorePhotoJob = viewModelScope.launch {
            unsplashInteractor.searchPhoto(query, page, perPage, orderBy, color, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            SearchPhotoViewState.LoadMoreLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            SearchPhotoViewState.LoadMoreEmpty
                        )
                        is Resource.Success -> _viewState.postValue(
                            SearchPhotoViewState.LoadMoreSuccess(result.data)
                        )
                        is Resource.Error -> _viewState.postValue(
                            SearchPhotoViewState.Error(result.message)
                        )
                    }
                }
        }
    }

}