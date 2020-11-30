package com.ryccoatika.core.domain.model

data class PhotoDetail(
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
    var user: UserDetail,
    var exif: Exif,
    var location: Location,
    var tags: List<Tag>,
    var relatedCollections: RelatedCollection,
    var views: Int,
    var downloads: Int
) {
    data class Exif(
        var make: String,
        var model: String,
        var exposureTime: String,
        var aperture: String,
        var focalLength: String,
        var iso: Int
    )

    data class Location(
        var title: String,
        var name: String,
        var city: String,
        var country: String,
        var position: Position
    ) {
        data class Position(
            var latitude: Double,
            var longitude: Double
        )
    }
}