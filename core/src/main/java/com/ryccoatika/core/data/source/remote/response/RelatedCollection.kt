package com.ryccoatika.core.data.source.remote.response

data class RelatedCollection(
    val total: Int? = null,
    val type: String? = null,
    val results: List<CollectionResponse>? = null
)