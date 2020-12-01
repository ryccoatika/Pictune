package com.ryccoatika.pictune.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val unsplashInteractor: UnsplashUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<UserViewState>()
    private var getUserJob: Job? = null

    val viewState: LiveData<UserViewState>
        get() = _viewState

    fun getUser(username: String) {
        getUserJob?.cancel()
        getUserJob = viewModelScope.launch {
            unsplashInteractor.getUser(username).collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(
                        UserViewState.Loading
                    )
                    is Resource.Empty,
                    is Resource.Error -> _viewState.postValue(
                        UserViewState.Error(result.message)
                    )
                    is Resource.Success -> _viewState.postValue(
                        UserViewState.Success(result.data)
                    )
                }
            }
        }
    }
}