package com.ryccoatika.core.domain.model

data class CollectionMinimal(
    var id: String,
    var title: String,
    var totalPhotos: Int,
    var coverPhoto: PhotoMinimal,
)