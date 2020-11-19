package com.ryccoatika.core.domain.model

data class User(
    var id: String,
    var updatedAt: String,
    var username: String,
    var name: String,
    var firstName: String,
    var lastName: String,
    var twitterUsername: String,
    var portfolioUrl: String,
    var bio: String,
    var location: String,
    var links: LinksUser,
    var profileImage: ProfileImage,
    var instagramUsername: String,
    var totalCollection: Int,
    var totalLikes: Int,
    var totalPhotos: Int,
    var acceptedTos: Boolean
) {
    data class ProfileImage(
        var small: String,
        var medium: String,
        var large: String
    )
}