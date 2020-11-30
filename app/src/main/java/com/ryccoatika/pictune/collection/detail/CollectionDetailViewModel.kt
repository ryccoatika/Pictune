package com.ryccoatika.pictune.collection.detail

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

class CollectionDetailViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState =
        MutableLiveData<CollectionDetailViewState>(CollectionDetailViewState.Loading)
    private var getCollectionJob: Job? = null
    private var getPhotosJob: Job? = null
    private var loadMorePhotosJob: Job? = null

    val viewState: LiveData<CollectionDetailViewState>
        get() = _viewState

    fun getCollection(id: String) {
        getCollectionJob?.cancel()
        getCollectionJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.getCollection(id).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        CollectionDetailViewState.Loading
                    )
                    is Resource.Empty,
                    is Resource.Error -> _viewState.postValue(
                        CollectionDetailViewState.Error(null)
                    )
                    is Resource.Success -> _viewState.postValue(
                        CollectionDetailViewState.Success(result.data)
                    )
                }
            }
        }
    }

    fun getCollectionPhotos(
        id: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null
    ) {
        getPhotosJob?.cancel()
        getPhotosJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.getCollectionPhotos(id, page, perPage, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            CollectionDetailViewState.GetPhotosLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            CollectionDetailViewState.GetPhotosEmpty
                        )
                        is Resource.Error -> _viewState.postValue(
                            CollectionDetailViewState.ListPhotosError(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            CollectionDetailViewState.GetPhotosSuccess(result.data)
                        )
                    }
                }
        }
    }

    fun loadMoreCollectionPhotos(
        id: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null
    ) {
        loadMorePhotosJob?.cancel()
        loadMorePhotosJob = viewModelScope.launch(Dispatchers.IO) {
            unsplashInteractor.getCollectionPhotos(id, page, perPage, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            CollectionDetailViewState.LoadMorePhotoLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            CollectionDetailViewState.LoadMorePhotosEmpty
                        )
                        is Resource.Error -> _viewState.postValue(
                            CollectionDetailViewState.ListPhotosError(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            CollectionDetailViewState.LoadMorePhotoSuccess(result.data)
                        )
                    }
                }
        }
    }
}