package com.ryccoatika.core.data.source.remote.response.unsplash

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    @SerializedName("results")
    val results: List<UserResponse>? = null
)