package com.ryccoatika.core.domain.model

data class RelatedCollection(
    var total: Int,
    var type: String,
    var results: List<Collection>
)