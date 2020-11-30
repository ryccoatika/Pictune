package com.ryccoatika.pictune.user.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserCollectionsViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState =
        MutableLiveData<UserCollectionsViewState>(UserCollectionsViewState.Loading)
    private var getUserCollectionJob: Job? = null
    private var loadMoreCollectionJob: Job? = null

    val viewState: LiveData<UserCollectionsViewState>
        get() = _viewState

    fun getUserCollections(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ) {
        getUserCollectionJob?.cancel()
        getUserCollectionJob = viewModelScope.launch {
            unsplashInteractor.getUserCollections(username, page, perPage, orderBy, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            UserCollectionsViewState.Loading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            UserCollectionsViewState.Empty
                        )
                        is Resource.Error -> _viewState.postValue(
                            UserCollectionsViewState.Error(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            UserCollectionsViewState.Success(result.data)
                        )
                    }
                }
        }
    }

    fun loadMoreCollections(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ) {
        loadMoreCollectionJob?.cancel()
        loadMoreCollectionJob = viewModelScope.launch {
            unsplashInteractor.getUserCollections(username, page, perPage, orderBy, orientation)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> _viewState.postValue(
                            UserCollectionsViewState.LoadMoreLoading
                        )
                        is Resource.Empty -> _viewState.postValue(
                            UserCollectionsViewState.LoadMoreEmpty
                        )
                        is Resource.Error -> _viewState.postValue(
                            UserCollectionsViewState.Error(result.message)
                        )
                        is Resource.Success -> _viewState.postValue(
                            UserCollectionsViewState.LoadMoreSuccess(result.data)
                        )
                    }
                }
        }
    }
}