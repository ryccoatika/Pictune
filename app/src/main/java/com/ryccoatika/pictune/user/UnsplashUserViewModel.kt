package com.ryccoatika.pictune.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryccoatika.pictune.BuildConfig
import com.ryccoatika.pictune.db.NetworkAdapter
import com.ryccoatika.pictune.db.UnsplashDataSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.URL

class UnsplashUserViewModel : ViewModel(), UnsplashUserView {

    private val disposable = CompositeDisposable()
    private val observer = MutableLiveData<UnsplashUserViewState>()
    private val dataSource = NetworkAdapter.providesHttpAdapter(
        URL(BuildConfig.UNSPLASH_ENDPOIN)
    ).create(UnsplashDataSource::class.java)

    override val state: LiveData<UnsplashUserViewState>
        get() = observer

    override fun unsplashGetUser(username: String) {
        Thread {
            Runnable {
                dataSource.getUserByUsername(username).observeOn(Schedulers.io())
                    .map<UnsplashUserViewState>(UnsplashUserViewState::UserSuccess)
                    .onErrorReturn(UnsplashUserViewState::UserError)
                    .toFlowable()
                    .startWith(UnsplashUserViewState.UserLoading)
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