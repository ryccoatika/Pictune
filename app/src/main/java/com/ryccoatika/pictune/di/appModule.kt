package com.ryccoatika.pictune.di

import com.ryccoatika.pictune.collection.CollectionViewModel
import com.ryccoatika.pictune.collection.detail.CollectionDetailViewModel
import com.ryccoatika.pictune.favorite.FavoriteViewModel
import com.ryccoatika.pictune.photo.PhotoViewModel
import com.ryccoatika.pictune.photo.detail.PhotoDetailViewModel
import com.ryccoatika.pictune.search.SearchViewModel
import com.ryccoatika.pictune.search.activity.collections.SearchCollectionViewModel
import com.ryccoatika.pictune.search.activity.photos.SearchPhotoViewModel
import com.ryccoatika.pictune.search.activity.users.SearchUserViewModel
import com.ryccoatika.pictune.search.topic.TopicViewModel
import com.ryccoatika.pictune.settings.SettingViewModel
import com.ryccoatika.pictune.settings.autowallpaper.history.HistoryViewModel
import com.ryccoatika.pictune.user.UserViewModel
import com.ryccoatika.pictune.user.collections.UserCollectionsViewModel
import com.ryccoatika.pictune.user.likes.UserLikesViewModel
import com.ryccoatika.pictune.user.photos.UserPhotosViewModel
import com.ryccoatika.pictune.utils.PictuneSharedPreferences
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // photo
    viewModel { PhotoViewModel(get()) }
    viewModel { PhotoDetailViewModel(get(), get()) }
    // collection
    viewModel { CollectionViewModel(get()) }
    viewModel { CollectionDetailViewModel(get()) }
    // topic
    viewModel { TopicViewModel(get()) }
    // user
    viewModel { UserViewModel(get()) }
    viewModel { UserPhotosViewModel(get()) }
    viewModel { UserLikesViewModel(get()) }
    viewModel { UserCollectionsViewModel(get()) }
    // search
    viewModel { SearchViewModel(get()) }
    viewModel { SearchPhotoViewModel(get()) }
    viewModel { SearchCollectionViewModel(get()) }
    viewModel { SearchUserViewModel(get()) }
    // favorite
    viewModel { FavoriteViewModel(get()) }
    // setting
    viewModel { SettingViewModel(get()) }
    // history
    viewModel { HistoryViewModel(get()) }
}

val sharedPreferenceModule = module {
    single { PictuneSharedPreferences(get()) }
}