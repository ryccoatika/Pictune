package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class TopicResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("published_at")
    val publishedAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("starts_at")
    val startsAt: String? = null,
    @SerializedName("ends_at")
    val endsAt: String? = null,
    @SerializedName("featured")
    val featured: Boolean? = null,
    @SerializedName("total_photos")
    val totalPhotos: Int? = null,
    @SerializedName("links")
    val links: LinksTopicResponse? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("owners")
    val owners: List<UserResponse>? = null,
    @SerializedName("cover_photos")
    val coverPhoto: PhotoMinimalResponse? = null
)