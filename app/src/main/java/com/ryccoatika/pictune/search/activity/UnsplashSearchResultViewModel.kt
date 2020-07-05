package com.ryccoatika.pictune.search.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashSearchResultViewModel : ViewModel(), UnsplashSearchResultView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashSearchResultViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashSearchResultViewState>
        get() = observer

    override fun searchPhotos(
        query: String,
        orderBy: String?,
        contentFilter: String?,
        color: String?,
        orientation: String?
    ) {
        Thread {
            Runnable {
                dataSource.searchPhotos(
                    query = query,
                    orderBy = orderBy,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                ).observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::PhotosSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreSearchPhotos(
        query: String,
        page: Int,
        orderBy: String?,
        contentFilter: String?,
        color: String?,
        orientation: String?
    ) {
        Thread {
            Runnable {
                dataSource.searchPhotos(
                    query = query,
                    page = page,
                    orderBy = orderBy,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                ).observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::LoadMorePhotosSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.LoadMoreLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun searchCollections(query: String) {
        Thread {
            Runnable {
                dataSource.searchCollections(query)
                    .observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::CollectionsSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreSearchCollections(query: String, page: Int) {
        Thread {
            Runnable {
                dataSource.searchCollections(query, page = page)
                    .observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::LoadMoreCollectionsSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.LoadMoreLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun searchUsers(query: String) {
        Thread {
            Runnable {
                dataSource.searchUsers(query)
                    .observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::UsersSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMoreSearchUsers(query: String, page: Int) {
        Thread {
            Runnable {
                dataSource.searchUsers(query, page = page)
                    .observeOn(Schedulers.io())
                    .map<UnsplashSearchResultViewState>(UnsplashSearchResultViewState::LoadMoreUsersSuccess)
                    .onErrorReturn(UnsplashSearchResultViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashSearchResultViewState.LoadMoreLoading)
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