package com.ryccoatika.pictune.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashCollectionViewModel : ViewModel(), UnsplashCollectionView {

    private val disposable = CompositeDisposable()
    val observer = MutableLiveData<UnsplashCollectionViewState>()
    private val dataSource: UnsplashDataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashCollectionViewState>
        get() = observer

    override fun getRelatedCollections(id: Int) {
        Thread {
            Runnable {
                dataSource.getRelatedCollections(id).observeOn(Schedulers.io())
                    .map<UnsplashCollectionViewState>(UnsplashCollectionViewState::RelatedCollectionsOnSuccess)
                    .onErrorReturn(UnsplashCollectionViewState::RelatedCollectionsOnError)
                    .toFlowable()
                    .startWith(UnsplashCollectionViewState.RelatedCollectionsLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun getPhotosOfCollections(id: Int) {
        Thread {
            Runnable {
                dataSource.getPhotosOfCollections(id).observeOn(Schedulers.io())
                    .map<UnsplashCollectionViewState>(UnsplashCollectionViewState::PhotosOnSuccess)
                    .onErrorReturn(UnsplashCollectionViewState::PhotosOnError)
                    .toFlowable()
                    .startWith(UnsplashCollectionViewState.PhotosLoading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMorePhotos(id: Int, page: Int) {
        Thread {
            Runnable {
                dataSource.getPhotosOfCollections(id = id, page = page).observeOn(Schedulers.io())
                    .map<UnsplashCollectionViewState>(UnsplashCollectionViewState::LoadMorePhotosSuccess)
                    .onErrorReturn(UnsplashCollectionViewState::LoadMorePhotosError)
                    .toFlowable()
                    .startWith(UnsplashCollectionViewState.LoadMorePhotosLoading)
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