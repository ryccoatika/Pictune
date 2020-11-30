package com.ryccoatika.pictune.settings.autowallpaper.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.usecase.HistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyInteractor: HistoryUseCase
) : ViewModel() {

    private val _viewState = MutableLiveData<HistoryViewState>(HistoryViewState.Loading)
    private var getAllPhotoJob: Job? = null

    val viewState: LiveData<HistoryViewState>
        get() = _viewState

    fun getAllPhotoHistory() {
        getAllPhotoJob?.cancel()
        getAllPhotoJob = viewModelScope.launch {
            historyInteractor.getAllPhotoHistory().collect { result ->
                when (result) {
                    is Resource.Loading -> _viewState.postValue(HistoryViewState.Loading)
                    is Resource.Empty -> _viewState.postValue(HistoryViewState.Empty)
                    is Resource.Error -> _viewState.postValue(HistoryViewState.Error(result.message))
                    is Resource.Success -> _viewState.postValue(HistoryViewState.Success(result.data))
                }
            }
        }
    }

    fun deleteAllPhotoFromHistory() {
        viewModelScope.launch {
            historyInteractor.deleteAllPhotoFromHistory()
        }
    }
}