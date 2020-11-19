package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class SearchCollectionResponse(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    @SerializedName("results")
    val results: List<CollectionResponse>? = null
)