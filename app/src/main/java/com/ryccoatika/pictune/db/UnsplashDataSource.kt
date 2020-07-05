package com.ryccoatika.pictune.db

import android.telecom.Call
import com.ryccoatika.pictune.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashDataSource {

    @GET("/photos/")
    fun discoverPhotos(
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY,
        @Query("order_by")
        orderBy: String = UnsplashPhotoResponse.ORDER_BY_LATEST,
        @Query("page")
        page: Int = 1
    ): Single<List<UnsplashPhotoResponse?>>

    @GET("/photos/random")
    fun getRandomPhoto(
        @Query("collections")
        collectionId: String? = null,
        @Query("username")
        username: String? = null,
        @Query("query")
        query: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashPhotoResponse>

    @GET("/photos/{id}")
    fun getPhotoById(
        @Path("id")
        id: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashPhotoResponse?>

    @GET("/photos/{id}/statistics")
    fun getPhotoStatistics(
        @Path("id")
        id: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashPhotoStatisticsResponse>

    @GET("/collections")
    fun discoverAllCollections(
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY,
        @Query("page")
        page: Int = 1,
        @Query("per_page")
        perPage: Int = 15
    ): Single<List<UnsplashCollectionResponse?>>

    @GET("/collections/featured")
    fun discoverFeaturedCollections(
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY,
        @Query("page")
        page: Int = 1,
        @Query("per_page")
        perPage: Int = 15
    ): Single<List<UnsplashCollectionResponse?>>

    @GET("/users/{username}")
    fun getUserByUsername(
        @Path("username")
        username: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashUserResponse>

    @GET("/users/{username}/photos")
    fun getUserPhotosByUsername(
        @Path("username")
        username: String,
        @Query("page")
        page: Int = 1,
        @Query("per_page")
        perPage: Int = 10,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<List<UnsplashPhotoResponse?>>

    @GET("/users/{username}/likes")
    fun getUserLikesByUsername(
        @Path("username")
        username: String,
        @Query("page")
        page: Int = 1,
        @Query("per_page")
        perPage: Int = 10,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<List<UnsplashPhotoResponse?>>

    @GET("/users/{username}/collections")
    fun getUserCollectionsByUsername(
        @Path("username")
        username: String,
        @Query("page")
        page: Int = 1,
        @Query("per_page")
        perPage: Int = 10,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<List<UnsplashCollectionResponse?>>

    @GET("/collections/{id}/related")
    fun getRelatedCollections(
        @Path("id")
        id: Int,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<List<UnsplashCollectionResponse?>>

    @GET("/collections/{id}/photos")
    fun getPhotosOfCollections(
        @Path("id")
        id: Int,
        @Query("page")
        page: Int = 1,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<List<UnsplashPhotoResponse?>>

    @GET("/search/photos")
    fun searchPhotos(
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("order_by")
        orderBy: String? = null,
        @Query("content_filter")
        contentFilter: String? = null,
        @Query("color")
        color: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashSearchPhotosResponse>

    @GET("/search/collections")
    fun searchCollections(
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashSearchCollectionsResponse>

    @GET("/search/users")
    fun searchUsers(
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Single<UnsplashSearchUsersResponse>

    @GET("/photos/{id}/download")
    fun requestDownload(
        @Path("id")
        id: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): Call
}