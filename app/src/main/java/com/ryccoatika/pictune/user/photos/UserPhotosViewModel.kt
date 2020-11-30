package com.ryccoatika.pictune.user.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserPhotosViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<UserPhotosViewState>(UserPhotosViewState.Loading)
    private var getUserPhotosJob: Job? = null
    private var loadMorePhotoJob: Job? = null

    val viewState: LiveData<UserPhotosViewState>
        get() = _viewState

    fun getUserPhotos(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ) {
        getUserPhotosJob?.cancel()
        getUserPhotosJob = viewModelScope.launch {
            unsplashInteractor.getUserPhotos(username, page, perPage, orderBy, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            UserPhotosViewState.Loading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            UserPhotosViewState.Empty
                        )
                        is Resource.Success -> _viewState.postValue(
                            UserPhotosViewState.Success(result.data)
                        )
                        is Resource.Error -> _viewState.postValue(
                            UserPhotosViewState.Error(result.message)
                        )
                    }
                }
        }
    }

    fun loadMorePhoto(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ) {
        loadMorePhotoJob?.cancel()
        loadMorePhotoJob = viewModelScope.launch {
            unsplashInteractor.getUserPhotos(username, page, perPage, orderBy, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            UserPhotosViewState.LoadMoreLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            UserPhotosViewState.LoadMoreEmpty
                        )
                        is Resource.Success -> _viewState.postValue(
                            UserPhotosViewState.LoadMoreSuccess(result.data)
                        )
                        is Resource.Error -> _viewState.postValue(
                            UserPhotosViewState.Error(result.message)
                        )
                    }
                }
        }
    }

}