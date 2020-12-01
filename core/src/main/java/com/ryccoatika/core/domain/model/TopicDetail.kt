package com.ryccoatika.core.domain.model

data class TopicDetail(
    var id: String,
    var slug: String,
    var title: String,
    var description: String,
    var publishedAt: String,
    var updatedAt: String,
    var startsAt: String,
    var endsAt: String,
    var featured: Boolean,
    var totalPhotos: Int,
    var links: LinksTopic,
    var status: String,
    var owners: List<UserDetail>,
    var coverPhoto: PhotoMinimal
)