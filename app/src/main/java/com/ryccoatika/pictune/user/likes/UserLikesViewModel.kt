package com.ryccoatika.pictune.user.likes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserLikesViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<UserLikesViewState>(UserLikesViewState.Loading)
    private var getUserLikesJob: Job? = null
    private var loadMoreLikesJob: Job? = null

    val viewState: LiveData<UserLikesViewState>
        get() = _viewState

    fun getUserLikes(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
    ) {
        getUserLikesJob?.cancel()
        getUserLikesJob = viewModelScope.launch {
            unsplashInteractor.getUserLikes(username, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        UserLikesViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        UserLikesViewState.Empty
                    )
                    is Resource.Success -> _viewState.postValue(
                        UserLikesViewState.Success(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        UserLikesViewState.Error(result.message)
                    )
                }
            }
        }
    }

    fun loadMoreLikes(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ) {
        loadMoreLikesJob?.cancel()
        loadMoreLikesJob = viewModelScope.launch {
            unsplashInteractor.getUserLikes(username, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        UserLikesViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        UserLikesViewState.LoadMoreEmpty
                    )
                    is Resource.Success -> _viewState.postValue(
                        UserLikesViewState.LoadMoreSuccess(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        UserLikesViewState.Error(result.message)
                    )
                }
            }
        }
    }

}