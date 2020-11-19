package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LinkTopicResponse(
    @SerializedName("self")
    val self: String? = null,
    @SerializedName("html")
    val html: String? = null,
    @SerializedName("photos")
    val photos: String? = null
)