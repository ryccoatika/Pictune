package com.ryccoatika.pictune.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.FavoriteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val favoriteInteractor: FavoriteUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<FavoriteViewState>(FavoriteViewState.Loading)
    private var getPhotosJob: Job? = null

    val viewState: LiveData<FavoriteViewState>
        get() = _viewState

    fun getAllFavoritePhotos() {
        getPhotosJob?.cancel()
        getPhotosJob = viewModelScope.launch(Dispatchers.IO) {
            favoriteInteractor.getAllFavorite().collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(FavoriteViewState.Loading)
                    is Resource.Empty -> _viewState.postValue(FavoriteViewState.Empty)
                    is Resource.Error -> _viewState.postValue(FavoriteViewState.Error(result.message))
                    is Resource.Success -> _viewState.postValue(FavoriteViewState.Success(result.data))
                }
            }
        }
    }
}