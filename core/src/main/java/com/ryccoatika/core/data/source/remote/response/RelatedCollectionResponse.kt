package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class RelatedCollectionResponse(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("results")
    val results: List<CollectionResponse>? = null
)