package com.ryccoatika.pictune.favorite

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import com.ryccoatika.pictune.db.room.AppDatabase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class FavoriteViewModel(val context: Context): ViewModel(), FavoriteView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<FavoriteViewState>()
    private val database = AppDatabase.getInstance(context)
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<FavoriteViewState>
        get() = observer

    override fun getAllFavoritePhotos() {
        Thread {
            Runnable {
                database?.let {
                    it.getPhotoDao().getAllData().observeOn(Schedulers.io())
                    .map<FavoriteViewState>(FavoriteViewState::SuccessGetFavorites)
                    .onErrorReturn(FavoriteViewState::Error)
                    .toFlowable()
                    .startWith(FavoriteViewState.Loading)
                    .subscribe(observer::postValue)
                    .let(disposable::add)
                }
            }.run()
        }.start()
    }

    override fun getUnsplashPhotoFromInternet(id: String) {
        Thread {
            Runnable {
                dataSource.getPhotoById(id).observeOn(Schedulers.io())
                    .map<FavoriteViewState>(FavoriteViewState::SuccessGetPhoto)
                    .onErrorReturn(FavoriteViewState::Error)
                    .toFlowable()
                    .startWith(FavoriteViewState.Loading)
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