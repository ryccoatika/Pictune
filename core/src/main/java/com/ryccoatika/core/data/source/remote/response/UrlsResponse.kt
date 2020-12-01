package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UrlsResponse(
    @SerializedName("raw")
    val raw: String? = null,
    @SerializedName("full")
    val full: String? = null,
    @SerializedName("regular")
    val regular: String? = null,
    @SerializedName("small")
    val small: String? = null,
    @SerializedName("thumb")
    val thumb: String? = null
)