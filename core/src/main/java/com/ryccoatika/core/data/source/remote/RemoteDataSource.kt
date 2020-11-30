package com.ryccoatika.core.data.source.remote

import com.ryccoatika.core.data.source.remote.response.*
import com.ryccoatika.core.data.source.remote.retrofit.UnsplashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RemoteDataSource(
    private val unsplashService: UnsplashService
) {
    // PHOTO
    suspend fun discoverPhoto(
        orderBy: String? = null,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<ApiResponse<List<PhotoMinimalResponse>>> {
        return flow {
            val photoList = unsplashService.discoverPhoto(orderBy, page, perPage)
            if (photoList.isNotEmpty())
                emit(ApiResponse.Success(photoList))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getRandomPhoto(
        id: String? = null,
        username: String? = null,
        query: String? = null,
        orientation: String? = null,
        count: Int? = null
    ): Flow<ApiResponse<PhotoDetailResponse>> {
        return flow {
            val photo = unsplashService.getRandomPhoto(id, username, query, orientation, count)
            if (photo.id != null)
                emit(ApiResponse.Success(photo))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPhoto(id: String): Flow<ApiResponse<PhotoDetailResponse>> {
        return flow {
            val photo = unsplashService.getPhoto(id)
            if (photo.id != null)
                emit(ApiResponse.Success(photo))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    fun triggerDownloadPhoto(id: String) {
        GlobalScope.launch {
            unsplashService.triggerDownloadPhoto(id)
        }.start()
    }

    // COLLECTION
    suspend fun discoverCollection(
        page: Int? = null,
        perPage: Int? = null
    ): Flow<ApiResponse<List<CollectionResponse>>> {
        return flow {
            val listCollection = unsplashService.discoverCollection(page, perPage)
            if (listCollection.isNotEmpty())
                emit(ApiResponse.Success(listCollection))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCollection(
        id: String
    ): Flow<ApiResponse<CollectionResponse>> {
        return flow {
            val collection = unsplashService.getCollection(id)
            if (collection.id != null)
                emit(ApiResponse.Success(collection))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCollectionPhotos(
        id: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null
    ): Flow<ApiResponse<List<PhotoMinimalResponse>>> {
        return flow {
            val listPhoto = unsplashService.getCollectionPhotos(id, page, perPage, orientation)
            if (listPhoto.isNotEmpty())
                emit(ApiResponse.Success(listPhoto))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    // USER
    suspend fun getUser(
        username: String
    ): Flow<ApiResponse<UserResponse>> {
        return flow {
            val user = unsplashService.getUser(username)
            if (user.id != null)
                emit(ApiResponse.Success(user))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserPhotos(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null,
    ): Flow<ApiResponse<List<PhotoMinimalResponse>>> {
        return flow {
            val listPhoto = unsplashService.getUserPhotos(username, page, perPage, orderBy, orientation)
            if (listPhoto.isNotEmpty())
                emit(ApiResponse.Success(listPhoto))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserLikes(
        username: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<ApiResponse<List<PhotoMinimalResponse>>> {
        return flow {
            val listPhoto = unsplashService.getUserLikes(username, page, perPage)
            if (listPhoto.isNotEmpty())
                emit(ApiResponse.Success(listPhoto))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserCollections(
        username: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        orientation: String? = null
    ): Flow<ApiResponse<List<CollectionResponse>>> {
        return flow {
        val listCollection = unsplashService.getUserCollections(username, page, perPage, orderBy, orientation)
        if (listCollection.isNotEmpty())
            emit(ApiResponse.Success(listCollection))
        else
            emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    // TOPIC
    suspend fun discoverTopic(
        ids: String? = null,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null
    ): Flow<ApiResponse<List<TopicResponse>>> {
        return flow {
            val listTopic = unsplashService.discoverTopic(ids, page, perPage, orderBy)
            if (listTopic.isNotEmpty())
                emit(ApiResponse.Success(listTopic))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getTopicPhotos(
        idOrSlug: String,
        page: Int? = null,
        perPage: Int? = null,
        orientation: String? = null,
        orderBy: String? = null
    ): Flow<ApiResponse<List<PhotoMinimalResponse>>> {
        return flow {
            val listPhoto = unsplashService.getTopicPhotos(idOrSlug, page, perPage, orientation, orderBy)
            if (listPhoto.isNotEmpty())
                emit(ApiResponse.Success(listPhoto))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    // SEARCH
    suspend fun searchPhoto(
        query: String,
        page: Int? = null,
        perPage: Int? = null,
        orderBy: String? = null,
        color: String? = null,
        orientation: String? = null
    ): Flow<ApiResponse<SearchPhotoResponse>> {
        return flow {
            val searchPhoto = unsplashService.searchPhoto(query, page, perPage, orderBy, color, orientation)
            if (!searchPhoto.results.isNullOrEmpty())
                emit(ApiResponse.Success(searchPhoto))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchCollection(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<ApiResponse<SearchCollectionResponse>> {
        return flow {
            val searchCollection = unsplashService.searchCollection(query, page, perPage)
            if (!searchCollection.results.isNullOrEmpty())
                emit(ApiResponse.Success(searchCollection))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchUser(
        query: String,
        page: Int? = null,
        perPage: Int? = null
    ): Flow<ApiResponse<SearchUserResponse>> {
        return flow {
            val searchUser = unsplashService.searchUser(query, page, perPage)
            if (!searchUser.results.isNullOrEmpty())
                emit(ApiResponse.Success(searchUser))
            else
                emit(ApiResponse.Empty)
        }.catch {
            emit(ApiResponse.Error(it.message.toString()))
            it.printStackTrace()
        }.flowOn(Dispatchers.IO)
    }
}