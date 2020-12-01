package com.ryccoatika.core.data

import com.ryccoatika.core.data.source.remote.ApiResponse
import com.ryccoatika.core.data.source.remote.RemoteDataSource
import com.ryccoatika.core.domain.model.*
import com.ryccoatika.core.domain.repository.IUnsplashRepository
import com.ryccoatika.core.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class UnsplashRepository(
    private val remoteDataSource: RemoteDataSource
) : IUnsplashRepository {

    override fun discoverPhotos(
        orderBy: String?,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.discoverPhoto(orderBy, page, perPage).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toPhotoMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override suspend fun getRandomPhoto(
        id: String?,
        username: String?,
        query: String?,
        orientation: String?,
        count: Int?
    ): Resource<PhotoDetail> = withContext(Dispatchers.IO) {
            when (val apiResponse = remoteDataSource.getRandomPhoto(id, username, query, orientation, count).first()) {
                is ApiResponse.Success ->
                    Resource.Success(apiResponse.data.toPhotoDetailDomain())
                is ApiResponse.Empty ->
                    Resource.Empty()
                is ApiResponse.Error ->
                    Resource.Error(apiResponse.message)
            }
    }

    override fun getPhoto(id: String): Flow<Resource<PhotoDetail>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getPhoto(id).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.toPhotoDetailDomain()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<PhotoDetail>())
                is ApiResponse.Error ->
                    emit(Resource.Error<PhotoDetail>(apiResponse.message))
            }
        }
    }

    override suspend fun triggerDownloadPhoto(id: String) = remoteDataSource.triggerDownloadPhoto(id)

    override fun discoverCollections(page: Int?, perPage: Int?): Flow<Resource<List<CollectionMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.discoverCollection(page, perPage).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toCollectionMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<CollectionMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<CollectionMinimal>>(apiResponse.message))
            }
        }
    }

    override fun getCollection(id: String): Flow<Resource<CollectionDetail>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getCollection(id).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.toCollectionDomain()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<CollectionDetail>())
                is ApiResponse.Error ->
                    emit(Resource.Error<CollectionDetail>(apiResponse.message))
            }
        }
    }

    override fun getCollectionPhotos(
        id: String,
        page: Int?,
        perPage: Int?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getCollectionPhotos(id, page, perPage, orientation).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toPhotoMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override fun getUser(username: String): Flow<Resource<UserDetail>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUser(username).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.toUserDomain()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<UserDetail>())
                is ApiResponse.Error ->
                    emit(Resource.Error<UserDetail>(apiResponse.message))
            }
        }
    }

    override fun getUserPhotos(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserPhotos(username, page, perPage, orderBy, orientation).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toPhotoMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override fun getUserLikes(
        username: String,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserLikes(username, page, perPage).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toPhotoMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override fun getUserCollections(
        username: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        orientation: String?
    ): Flow<Resource<List<CollectionMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserCollections(username, page, perPage, orderBy, orientation).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toCollectionMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<CollectionMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<CollectionMinimal>>(apiResponse.message))
            }
        }
    }

    override fun discoverTopic(
        ids: String?,
        page: Int?,
        perPage: Int?,
        orderBy: String?
    ): Flow<Resource<List<TopicMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.discoverTopic(ids, page, perPage, orderBy).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toTopicMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<TopicMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<TopicMinimal>>(apiResponse.message))
            }
        }
    }

    override fun getTopicPhotos(
        idOrSlug: String,
        page: Int?,
        perPage: Int?,
        orientation: String?,
        orderBy: String?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getTopicPhotos(idOrSlug, page, perPage, orientation, orderBy).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.map { it.toPhotoMinimalDomain() }))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override fun searchPhoto(
        query: String,
        page: Int?,
        perPage: Int?,
        orderBy: String?,
        color: String?,
        orientation: String?
    ): Flow<Resource<List<PhotoMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.searchPhoto(query, page, perPage, orderBy, color, orientation).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.results?.map { it.toPhotoMinimalDomain() } ?: listOf()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<PhotoMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<PhotoMinimal>>(apiResponse.message))
            }
        }
    }

    override fun searchCollection(
        query: String,
        page: Int?,
        perPage: Int?
    ): Flow<Resource<List<CollectionMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.searchCollection(query, page, perPage).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.results?.map { it.toCollectionMinimalDomain() } ?: listOf()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<CollectionMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<CollectionMinimal>>(apiResponse.message))
            }
        }
    }

    override fun searchUser(query: String, page: Int?, perPage: Int?): Flow<Resource<List<UserMinimal>>> {
        return flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.searchUser(query, page, perPage).first()) {
                is ApiResponse.Success ->
                    emit(Resource.Success(apiResponse.data.results?.map { it.toUserMinimalDomain() } ?: listOf()))
                is ApiResponse.Empty ->
                    emit(Resource.Empty<List<UserMinimal>>())
                is ApiResponse.Error ->
                    emit(Resource.Error<List<UserMinimal>>(apiResponse.message))
            }
        }
    }
}