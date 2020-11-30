package com.ryccoatika.core.domain.repository

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface IUnsplashRepository {
    // PHOTO
    fun discoverPhotos(
        orderBy: String? = null,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    suspend fun getRandomPhoto(
        id: String? = null,
        username: String? = null,
        query: String? = null,
        orientation: String? = null,
        count: Int? = null
    ): Resource<PhotoDetail>

    fun getPhoto(
        id: String
    ): Flow<Resource<PhotoDetail>>

    suspend fun triggerDownloadPhoto(id: String)

    // DISCOVER
    fun discoverCollections(
        page: Int? = null,
        perPage: Int? = null
    ): Flow<Resource<List<CollectionMinimal>>>

    fun getCollection(
        id: String
    ): Flow<Resource<CollectionDetail>>

    fun getCollectionPhotos(
        id: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    // USER
    fun getUser(
        username: String
    ): Flow<Resource<UserDetail>>

    fun getUserPhotos(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    fun getUserLikes(
        username: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    fun getUserCollections(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ): Flow<Resource<List<CollectionMinimal>>>

    // TOPIC
    fun discoverTopic(
        ids: String? = null,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null
    ): Flow<Resource<List<TopicMinimal>>>

    fun getTopicPhotos(
        idOrSlug: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null,
        orderBy: String? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    // SEARCH
    fun searchPhoto(
        query: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        color: String? = null,
        orientation: String? = null
    ): Flow<Resource<List<PhotoMinimal>>>

    fun searchCollection(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<Resource<List<CollectionMinimal>>>

    fun searchUser(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<Resource<List<UserMinimal>>>
}