package com.ryccoatika.pictune.photo.detail

import androidx.lifecycle.*
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.domain.usecase.FavoriteUseCase
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PhotoDetailViewModel(
    private val unsplashInteractor: UnsplashUseCase,
    private val favoriteInteractor: FavoriteUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<PhotoDetailViewState>(PhotoDetailViewState.Loading)
    private var getPhotoJob: Job? = null

    val viewState: LiveData<PhotoDetailViewState>
        get() = _viewState

    fun getPhoto(id: String) {
        getPhotoJob?.cancel()
        getPhotoJob = viewModelScope.launch {
            unsplashInteractor.getPhoto(id).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        PhotoDetailViewState.Loading
                    )
                    is Resource.Empty,
                    is Resource.Error -> _viewState.postValue(
                        PhotoDetailViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        PhotoDetailViewState.Success(result.data)
                    )
                }
            }
        }
    }

    fun isFavorite(id: String) = favoriteInteractor.checkPhotoFavorited(id).asLiveData()

    fun setPhotoFavorite(photo: PhotoMinimal, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite)
                favoriteInteractor.addPhotoToFavorite(photo)
            else
                favoriteInteractor.deletePhotoFromFavorite(photo)
        }
    }

    fun triggerDownload(id: String) {
        viewModelScope.launch {
            unsplashInteractor.triggerDownloadPhoto(id)
        }
    }
}