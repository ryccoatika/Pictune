package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LinkPhotoResponse (
    @SerializedName("self")
    val self: String? = null,
    @SerializedName("html")
    val html: String? = null,
    @SerializedName("download")
    val download: String? = null,
    @SerializedName("download_location")
    val downloadLocation: String? = null
)