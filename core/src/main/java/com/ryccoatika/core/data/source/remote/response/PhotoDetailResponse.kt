package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PhotoDetailResponse(
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
    val user: UserResponse? = null,
    @SerializedName("exif")
    val exif: ExifResponse? = null,
    @SerializedName("location")
    val location: LocationResponse? = null,
    @SerializedName("tags")
    val tags: List<TagResponse>? = null,
    @SerializedName("related_collections")
    val relatedCollectionResponse: RelatedCollectionResponse? = null,
    @SerializedName("views")
    val views: Int? = null,
    @SerializedName("downloads")
    val downloads: Int? = null
) {
    data class ExifResponse(
        @SerializedName("make")
        val make: String? = null,
        @SerializedName("model")
        val model: String? = null,
        @SerializedName("exposure_time")
        val exposureTime: String? = null,
        @SerializedName("aperture")
        val aperture: String? = null,
        @SerializedName("focal_length")
        val focalLength: String? = null,
        @SerializedName("iso")
        val iso: Int? = null
    )

    data class LocationResponse(
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("city")
        val city: String? = null,
        @SerializedName("country")
        val country: String? = null,
        @SerializedName("position")
        val position: PositionResponse? = null
    ) {
        data class PositionResponse(
            @SerializedName("latitude")
            val latitude: Double? = null,
            @SerializedName("longitude")
            val longitude: Double? = null
        )
    }
}