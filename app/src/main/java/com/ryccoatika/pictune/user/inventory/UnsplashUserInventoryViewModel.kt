package com.ryccoatika.pictune.user.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashUserInventoryViewModel : ViewModel(), UnsplashUserInventoryView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashUserInventoryViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashUserInventoryViewState>
        get() = observer

    override fun getUserPhotos(username: String) {
        Thread {
            Runnable {
                dataSource.getUserPhotosByUsername(username).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::SuccessPhotos)
                    .onErrorReturn(UnsplashUserInventoryViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreUserPhotos(username: String, page: Int) {
        Thread {
            Runnable {
                dataSource.getUserPhotosByUsername(username, page = page).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::LoadMoreSuccessPhotos)
                    .onErrorReturn(UnsplashUserInventoryViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.LoadMoreLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun getUserLikes(username: String) {
        Thread {
            Runnable {
                dataSource.getUserLikesByUsername(username).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::SuccessLikes)
                    .onErrorReturn(UnsplashUserInventoryViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreUserLikes(username: String, page: Int) {
        Thread {
            Runnable {
                dataSource.getUserLikesByUsername(username, page = page).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::LoadMoreSuccessLikes)
                    .onErrorReturn(UnsplashUserInventoryViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.LoadMoreLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun getUserCollections(username: String) {
        Thread {
            Runnable {
                dataSource.getUserCollectionsByUsername(username).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::SuccessCollections)
                    .onErrorReturn(UnsplashUserInventoryViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreUserCollections(username: String, page: Int) {
        Thread {
            Runnable {
                dataSource.getUserCollectionsByUsername(username, page = page).observeOn(Schedulers.io())
                    .map<UnsplashUserInventoryViewState>(UnsplashUserInventoryViewState::LoadMoreSuccessCollections)
                    .onErrorReturn(UnsplashUserInventoryViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashUserInventoryViewState.LoadMoreLoading)
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