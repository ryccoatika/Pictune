package com.ryccoatika.pictune.discoverCollections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashCollectionResponse
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashDiscoverCollectionsViewModel : ViewModel(), UnsplashDiscoverCollectionsView {

    companion object {
        const val COLLECTION_ALL = "collection_all"
        const val COLLECTION_FEATURED = "collection_featured"
    }

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashDiscoverCollectionsViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashDiscoverCollectionsViewState>
        get() = observer

    override fun discoverCollections(filter: String) {
        Thread {
            Runnable {
                val single = when (filter) {
                    COLLECTION_ALL -> {
                        dataSource.discoverAllCollections()
                    }
                    COLLECTION_FEATURED -> {
                        dataSource.discoverFeaturedCollections()
                    }
                    else -> null
                }
                single?.let {
                    it.observeOn(Schedulers.io())
                    .map<UnsplashDiscoverCollectionsViewState>(UnsplashDiscoverCollectionsViewState::DiscoverSuccess)
                    .onErrorReturn(UnsplashDiscoverCollectionsViewState::DiscoverError)
                    .toFlowable()
                    .startWith(UnsplashDiscoverCollectionsViewState.DiscoverLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
                }
            }.run()
        }.start()
    }

    override fun loadMoreCollections(page: Int, filter: String) {
        Thread {
            Runnable {
                val single = when (filter) {
                    COLLECTION_ALL -> {
                        dataSource.discoverAllCollections(page = page)
                    }
                    COLLECTION_FEATURED -> {
                        dataSource.discoverFeaturedCollections(page = page)
                    }
                    else -> null
                }
                single?.let {
                    it.observeOn(Schedulers.io())
                        .map<UnsplashDiscoverCollectionsViewState>(UnsplashDiscoverCollectionsViewState::LoadMoreSuccess)
                        .onErrorReturn(UnsplashDiscoverCollectionsViewState::LoadMoreError)
                        .toFlowable()
                        .startWith(UnsplashDiscoverCollectionsViewState.LoadMoreLoading)
                        .subscribe(observer::postValue)
                        .let(disposable::add)
                }
            }.run()
        }.start()
    }

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
    }
}