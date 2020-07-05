package com.ryccoatika.pictune.discoverPhotos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashDiscoverPhotosViewModel : ViewModel(), UnsplashDiscoverPhotosView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashDiscoverPhotosViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(
            BuildConfig.UNSPLASH_ENDPOIN
        )
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashDiscoverPhotosViewState>
        get() = observer

    override fun unsplashDiscover(orderBy: String) {
        Thread {
            Runnable {
                dataSource.discoverPhotos(orderBy = orderBy).observeOn(Schedulers.io())
                    .map<UnsplashDiscoverPhotosViewState>(UnsplashDiscoverPhotosViewState::DiscoverSuccess)
                    .onErrorReturn(UnsplashDiscoverPhotosViewState::DiscoverError)
                    .toFlowable()
                    .startWith(UnsplashDiscoverPhotosViewState.DiscoverLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun unsplashLoadMore(page: Int, orderBy: String) {
        Thread {
            Runnable {
                dataSource.discoverPhotos(orderBy = orderBy, page = page).observeOn(Schedulers.io())
                    .map<UnsplashDiscoverPhotosViewState>(UnsplashDiscoverPhotosViewState::LoadMoreSuccess)
                    .onErrorReturn(UnsplashDiscoverPhotosViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashDiscoverPhotosViewState.LoadMoreLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
    }

}