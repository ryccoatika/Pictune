package com.ryccoatika.core.domain.model

data class Collection(
    var id: String,
    var title: String,
    var description: String,
    var publishedAt: String,
    var lastCollectedAt: String,
    var updatedAt: String,
    var curated: Boolean,
    var featured: Boolean,
    var totalPhotos: Int,
    var private: Boolean,
    var shareKey: String,
    var tags: List<Tag>,
    var links: LinksCollection,
    var user: User,
    var coverPhoto: PhotoMinimal,
)