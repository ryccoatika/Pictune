package com.ryccoatika.pictune.search.activity.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchUserViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<SearchUserViewState>(SearchUserViewState.Loading)
    private var searchUserJob: Job? = null
    private var loadMoreUserJob: Job? = null

    val viewState: LiveData<SearchUserViewState>
        get() = _viewState

    fun searchUser(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ) {
        searchUserJob?.cancel()
        searchUserJob = viewModelScope.launch {
            unsplashInteractor.searchUser(query, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchUserViewState.Loading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchUserViewState.Empty
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchUserViewState.Success(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchUserViewState.Error(result.message)
                    )
                }
            }
        }
    }

    fun loadMoreUser(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ) {
        loadMoreUserJob?.cancel()
        loadMoreUserJob = viewModelScope.launch {
            unsplashInteractor.searchUser(query, page, perPage).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        SearchUserViewState.LoadMoreLoading
                    )
                    is Resource.Empty -> _viewState.postValue(
                        SearchUserViewState.LoadMoreEmpty
                    )
                    is Resource.Success -> _viewState.postValue(
                        SearchUserViewState.LoadMoreSuccess(result.data)
                    )
                    is Resource.Error -> _viewState.postValue(
                        SearchUserViewState.Error(result.message)
                    )
                }
            }
        }
    }

}