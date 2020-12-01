package com.ryccoatika.pictune.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PhotoViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<PhotoViewState>(PhotoViewState.Loading)
    private var discoverPhotoJob: Job? = null
    private var loadMorePhotoJob: Job? = null

    val viewState: LiveData<PhotoViewState>
        get() = _viewState

    fun discoverPhoto(orderBy: String? = null) {
        discoverPhotoJob?.cancel()
        discoverPhotoJob = viewModelScope.launch {
            unsplashInteractor.discoverPhotos(orderBy).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        PhotoViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        PhotoViewState.Empty
                    )
                    is Resource.Success -> _viewState.postValue(
                        PhotoViewState.Success(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        PhotoViewState.Error(result.message)
                    )
                }
            }
        }
    }

    fun loadMorePhoto(orderBy: String? = null, page: Int? = null) {
        loadMorePhotoJob?.cancel()
        loadMorePhotoJob = viewModelScope.launch {
            unsplashInteractor.discoverPhotos(orderBy, page).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        PhotoViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        PhotoViewState.LoadMoreEmpty
                    )
                    is Resource.Success -> _viewState.postValue(
                        PhotoViewState.LoadMoreSuccess(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        PhotoViewState.Error(result.message)
                    )
                }
            }
        }
    }

}