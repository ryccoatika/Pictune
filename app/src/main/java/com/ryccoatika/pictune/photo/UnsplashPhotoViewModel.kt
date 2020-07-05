package com.ryccoatika.pictune.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashPhotoViewModel: ViewModel(), UnsplashPhotoView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashPhotoViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN), false
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashPhotoViewState>
        get() = observer

    override fun getPhotoStatistics(id: String) {
        Thread {
            Runnable {
                dataSource.getPhotoStatistics(id).observeOn(Schedulers.io())
                    .map<UnsplashPhotoViewState>(UnsplashPhotoViewState::LoadStats)
                    .onErrorReturn(UnsplashPhotoViewState::Error)
                    .toFlowable()
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
        super.onCleared()
    }
}