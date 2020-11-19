package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class CollectionResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("published_at")
    val publishedAt: String? = null,
    @SerializedName("last_collected_at")
    val lastCollectedAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("curated")
    val curated: Boolean? = null,
    @SerializedName("featured")
    val featured: Boolean? = null,
    @SerializedName("total_photos")
    val totalPhotos: Int? = null,
    @SerializedName("private")
    val private: Boolean? = null,
    @SerializedName("share_key")
    val shareKey: String? = null,
    @SerializedName("tags")
    val tags: List<TagResponse>? = null,
    @SerializedName("links")
    val links: LinkCollectionResponse? = null,
    @SerializedName("user")
    val user: UserResponse? = null,
    @SerializedName("cover_photo")
    val coverPhoto: PhotoMinimalResponse? = null
)