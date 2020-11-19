package com.ryccoatika.core.domain.model

data class PhotoMinimal(
    var id: String,
    var createdAt: String,
    var updatedAt: String,
    var promotedAt: String,
    var width: Int,
    var height: Int,
    var color: String,
    var blurHash: String,
    var description: String,
    var altDescription: String,
    var urls: Urls,
    var links: LinksPhoto,
    var likes: Int,
    var likedByUser: Boolean,
    var user: User
)