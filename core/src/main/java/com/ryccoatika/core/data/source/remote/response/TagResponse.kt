package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class TagResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("title")
    val title: String? = null
)