package com.ryccoatika.pictune.search.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashCategoryViewModel : ViewModel(), UnsplashCategoryView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashCategoryViewState>()
    private val datasource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashCategoryViewState>
        get() = observer

    override fun searchCategory(category: String) {
        Thread {
            Runnable {
                datasource.searchPhotos(category).observeOn(Schedulers.io())
                    .map<UnsplashCategoryViewState>(UnsplashCategoryViewState::Success)
                    .onErrorReturn(UnsplashCategoryViewState::Error)
                    .toFlowable()
                    .startWith(UnsplashCategoryViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
            }.run()
        }.start()
    }

    override fun loadMore(category: String, page: Int) {
        Thread {
            Runnable {
                datasource.searchPhotos(category, page = page).observeOn(Schedulers.io())
                    .map<UnsplashCategoryViewState>(UnsplashCategoryViewState::LoadMoreSuccess)
                    .onErrorReturn(UnsplashCategoryViewState::LoadMoreError)
                    .toFlowable()
                    .startWith(UnsplashCategoryViewState.LoadMoreLoading)
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