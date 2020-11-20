package com.ryccoatika.core.data.source.remote.response.unsplash

import com.google.gson.annotations.SerializedName

data class PhotoMinimalResponse (
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("promoted_at")
    val promotedAt: String? = null,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("blur_hash")
    val blurHash: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("alt_description")
    val altDescription: String? = null,
    @SerializedName("urls")
    val urls: UrlsResponse? = null,
    @SerializedName("links")
    val links: LinksPhotoResponse? = null,
    @SerializedName("likes")
    val likes: Int? = null,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean? = null,
    @SerializedName("user")
    val user: UserResponse? = null
)