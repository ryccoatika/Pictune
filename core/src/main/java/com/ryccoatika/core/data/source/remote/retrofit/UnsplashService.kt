package com.ryccoatika.core.data.source.remote.retrofit

import com.ryccoatika.core.BuildConfig
import com.ryccoatika.core.data.source.remote.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashService {

    // ----------------------------- PHOTOS -----------------------------
    @GET("/photos")
    suspend fun discoverPhotos(
        @Query("order_by")
        orderBy: String? = null,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<PhotoMinimalResponse>

    @GET("/photos/random")
    suspend fun getRandomPhoto(
        @Query("collections")
        collectionId: String? = null,
        @Query("username")
        username: String? = null,
        @Query("query")
        query: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("count")
        count: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): PhotoDetailResponse

    @GET("/photos/{id}")
    suspend fun getPhoto(
        @Path("id")
        id: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): PhotoDetailResponse

    @GET("/photos/{id}/download")
    suspend fun triggerDownloadPhoto(
        @Path("id")
        id: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    )

    // ----------------------------- COLLECTIONS -----------------------------
    @GET("/collections")
    suspend fun discoverCollections(
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<CollectionResponse>

    @GET("/collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id")
        id: Int,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<PhotoMinimalResponse>

    // ----------------------------- USERS -----------------------------

    @GET("/users/{username}")
    suspend fun getUser(
        @Query("username")
        username: String,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): UserResponse

    @GET("/users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username")
        username: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("order_by")
        orderBy: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<PhotoMinimalResponse>

    @GET("/users/{username}/likes")
    suspend fun getUserLikes(
        @Path("username")
        username: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<PhotoMinimalResponse>

    @GET("/users/{username}/collections")
    suspend fun getUserCollections(
        @Path("username")
        username: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("order_by")
        orderBy: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<CollectionResponse>

    // ----------------------------- TOPICS -----------------------------

    @GET("/topics")
    suspend fun discoverTopic(
        @Query("ids")
        ids: String? = null,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("order_by")
        orderBy: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<TopicResponse>

    @GET("/topics/{id_or_slug}/photos")
    suspend fun getTopicPhotos(
        @Path("id_or_slug")
        idOrSlug: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("order_by")
        orderBy: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): List<PhotoMinimalResponse>

    // ----------------------------- SEARCH -----------------------------

    @GET("/search/photos")
    suspend fun searchPhoto(
        @Query("query")
        query: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("order_by")
        orderBy: String? = null,
        @Query("color")
        color: String? = null,
        @Query("orientation")
        orientation: String? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): SearchPhotoResponse

    @GET("/search/collections")
    suspend fun searchCollection(
        @Query("query")
        query: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): SearchCollectionResponse

    @GET("/search/users")
    suspend fun searchUser(
        @Query("query")
        query: String,
        @Query("page")
        page: Int? = null,
        @Query("per_page")
        perPage: Int? = null,
        @Query("client_id")
        accessKey: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): SearchUserResponse
}

