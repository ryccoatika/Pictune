package com.ryccoatika.pictune.settings.autowallpaper.history

import com.ryccoatika.core.domain.model.PhotoMinimal

sealed class HistoryViewState {
    data class Error(val message: String?) : HistoryViewState()

    data class Success(val data: List<PhotoMinimal>?) : HistoryViewState()
    object Loading : HistoryViewState()
    object Empty : HistoryViewState()
}