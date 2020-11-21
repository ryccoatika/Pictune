package com.ryccoatika.pictune.discoverPhoto

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

class DiscoverPhotoViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<DiscoverPhotoViewState>()
    private var discoverPhotoJob: Job? = null
    private var loadMorePhotoJob: Job? = null

    val viewState: LiveData<DiscoverPhotoViewState>
        get() = _viewState

    fun discoverPhoto(orderBy: String? = null) {
        discoverPhotoJob?.cancel()
        discoverPhotoJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.discoverPhotos(orderBy).collect { result ->
                when(result) {
                    is Resource.Loading -> _viewState.postValue(
                        DiscoverPhotoViewState.DiscoverLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        DiscoverPhotoViewState.DiscoverEmpty
                    )
                    is Resource.Success -> _viewState.postValue(
                        DiscoverPhotoViewState.DiscoverSuccess(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        DiscoverPhotoViewState.Error(result.message)
                    )
                }
            }
        }
    }

    fun loadMorePhoto(orderBy: String? = null, page: Int? = null) {
        loadMorePhotoJob?.cancel()
        loadMorePhotoJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.discoverPhotos(orderBy, page).collect { result ->
                when(result) {
                    is Resource.Loading -> _viewState.postValue(
                        DiscoverPhotoViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        DiscoverPhotoViewState.LoadMoreEmpty
                    )
                    is Resource.Success -> _viewState.postValue(
                        DiscoverPhotoViewState.LoadMoreSuccess(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        DiscoverPhotoViewState.Error(result.message)
                    )
                }
            }
        }
    }

}