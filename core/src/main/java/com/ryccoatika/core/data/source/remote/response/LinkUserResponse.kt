package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LinkUserResponse (
    @SerializedName("self")
    val self: String? = null,
    @SerializedName("html")
    val html: String? = null,
    @SerializedName("photos")
    val photos: String? = null,
    @SerializedName("likes")
    val likes: String? = null,
    @SerializedName("portfolio")
    val portfolio: String? = null,
    @SerializedName("following")
    val following: String? = null,
    @SerializedName("followers")
    val followers: String? = null
)