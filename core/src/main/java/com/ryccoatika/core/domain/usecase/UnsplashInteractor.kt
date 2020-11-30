package com.ryccoatika.core.domain.usecase

import com.ryccoatika.core.data.Resource
import com.ryccoatika.core.domain.model.*
import com.ryccoatika.core.domain.repository.IUnsplashRepository
import kotlinx.coroutines.flow.Flow

class UnsplashInteractor(
    private val unsplashRepository: IUnsplashRepository
) : UnsplashUseCase {
    override fun discoverPhotos(
        orderBy: String?,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.discoverPhotos(orderBy, page, perPage)

    override suspend fun getRandomPhoto(
        id: String?,
        username: String?,
        query: String?,
        orientation: String?,
        count: Int?
    ): Resource<PhotoDetail> = unsplashRepository.getRandomPhoto(id, username, query, orientation, count)

    override fun getPhoto(id: String): Flow<Resource<PhotoDetail>> =
        unsplashRepository.getPhoto(id)

    override suspend fun triggerDownloadPhoto(id: String) = unsplashRepository.triggerDownloadPhoto(id)

    override fun discoverCollections(page: Int?, perPage: Int?): Flow<Resource<List<CollectionMinimal>>> =
        unsplashRepository.discoverCollections(page, perPage)

    override fun getCollection(id: String): Flow<Resource<CollectionDetail>> =
        unsplashRepository.getCollection(id)

    override fun getCollectionPhotos(
        id: String,
        page: Int?,
        perPage: Int?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.getCollectionPhotos(id, page, perPage, orientation)

    override fun getUser(username: String): Flow<Resource<UserDetail>> =
        unsplashRepository.getUser(username)

    override fun getUserPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.getUserPhotos(username, page, perPage, orderBy, orientation)

    override fun getUserLikes(
        username: String,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.getUserLikes(username, page, perPage)

    override fun getUserCollections(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Flow<Resource<List<CollectionMinimal>>> =
        unsplashRepository.getUserCollections(username, page, perPage, orderBy, orientation)

    override fun discoverTopic(
        ids: String?,
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Flow<Resource<List<TopicMinimal>>> =
        unsplashRepository.discoverTopic(ids, page, perPage, orderBy)

    override fun getTopicPhotos(
        idOrSlug: String,
        page: Int?,
        perPage: Int?,
        orientation: String?,
        orderBy: String?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.getTopicPhotos(idOrSlug, page, perPage, orientation, orderBy)

    override fun searchPhoto(
        query: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        color: String?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> =
        unsplashRepository.searchPhoto(query, page, perPage, orderBy, color, orientation)

    override fun searchCollection(
        query: String,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<CollectionMinimal>>> =
        unsplashRepository.searchCollection(query, page, perPage)

    override fun searchUser(query: String, page: Int?, perPage: Int?): Flow<Resource<List<UserMinimal>>> =
        unsplashRepository.searchUser(query, page, perPage)
}