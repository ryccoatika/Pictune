package com.ryccoatika.core.utils

import com.ryccoatika.core.data.source.remote.response.unsplash.*
import com.ryccoatika.core.domain.model.*
import com.ryccoatika.core.domain.model.Collection

// ---------------------------------- TOPIC RESPONSE ----------------------------------
fun TopicResponse.toTopicDomain(): Topic = Topic(
    id = this.id ?: "undefined",
    slug = this.slug ?: "undefined",
    title = this.title ?: "undefined",
    description = this.description ?: "undefined",
    publishedAt = this.publishedAt ?: "undefined",
    updatedAt = this.updatedAt ?: "undefined",
    startsAt = this.startsAt ?: "undefined",
    endsAt = this.endsAt ?: "undefined",
    featured = this.featured ?: false,
    totalPhotos = this.totalPhotos ?: 0,
    links = this.links.toLinksTopicDomain(),
    status = this.status ?: "undefined",
    owners = this.owners.toListUserDomain(),
    coverPhoto = this.coverPhoto.toPhotoMinimalDomain()
)

// ---------------------------------- RELATED COLLECTION RESPONSE ----------------------------------
fun RelatedCollectionResponse?.toRelatedCollectionDomain(): RelatedCollection = RelatedCollection(
    total = this?.total ?: 0,
    type = this?.type ?: "undefined",
    results = this?.results.toListCollectionDomain()
)

fun List<RelatedCollectionResponse>?.toListRelatedCollectionDomain(): List<RelatedCollection> =
    this?.map { it.toRelatedCollectionDomain() } ?: listOf()

// ---------------------------------- URL RESPONSE ----------------------------------
fun UrlsResponse?.toUrlsDomain(): Urls = Urls(
    raw = this?.raw ?: "",
    full = this?.full ?: "",
    regular = this?.regular ?: "",
    small = this?.small ?: "",
    thumb = this?.thumb ?: ""
)

// ---------------------------------- TAG RESPONSE ----------------------------------
fun TagResponse.toTagDomain(): Tag = Tag(
    type = this.type ?: "undefined",
    title = this.title ?: "undefined"
)

fun List<TagResponse>?.toListTagDomain(): List<Tag> = this?.map { it.toTagDomain() } ?: listOf()

// ---------------------------------- LINKS ----------------------------------
fun LinksPhotoResponse?.toLinksPhotoDomain(): LinksPhoto = LinksPhoto(
    self = this?.self ?: "",
    html = this?.html ?: "",
    download = this?.download ?: "",
    downloadLocation = this?.downloadLocation ?: ""
)

fun LinksUserResponse?.toLinksUserDomain(): LinksUser = LinksUser(
    self = this?.self ?: "",
    html = this?.html ?: "",
    photos = this?.photos ?: "",
    likes = this?.likes ?: "",
    portfolio = this?.portfolio ?: "",
    following = this?.following ?: "",
    followers = this?.followers ?: ""
)

fun LinksTopicResponse?.toLinksTopicDomain(): LinksTopic = LinksTopic(
    self = this?.self ?: "",
    html = this?.html ?: "",
    photos = this?.photos ?: ""
)

fun LinksCollectionResponse?.toLinksCollectionDomain(): LinksCollection = LinksCollection(
    self = this?.self ?: "",
    html = this?.html ?: "",
    photos = this?.photos ?: "",
    related = this?.related ?: ""
)

// ---------------------------------- USER RESPONSE ----------------------------------

fun UserResponse.ProfileImageResponse?.toProfileImageDomain(): User.ProfileImage = User.ProfileImage(
    small = this?.small ?: "",
    medium = this?.medium ?: "",
    large = this?.large ?: ""
)

fun UserResponse?.toUserDomain(): User = User(
    id = this?.id ?: "undefined",
    updatedAt = this?.updatedAt ?: "undefined",
    username = this?.username ?: "undefined",
    name = this?.name ?: "undefined",
    firstName = this?.firstName ?: "undefined",
    lastName = this?.lastName ?: "undefined",
    twitterUsername = this?.twitterUsername ?: "undefined",
    portfolioUrl = this?.portfolioUrl ?: "undefined",
    bio = this?.bio ?: "undefined",
    location = this?.location ?: "undefined",
    links = this?.links.toLinksUserDomain(),
    profileImage = this?.profileImageResponse.toProfileImageDomain(),
    instagramUsername = this?.instagramUsername ?: "undefined",
    totalCollection = this?.totalCollection ?: 0,
    totalLikes = this?.totalLikes ?: 0,
    totalPhotos = this?.totalPhotos ?: 0,
    acceptedTos = this?.acceptedTos ?: false
)

fun List<UserResponse>?.toListUserDomain(): List<User> = this?.map { it.toUserDomain() } ?: listOf()

// ---------------------------------- PHOTO MINIMAL RESPONSE ----------------------------------

fun PhotoMinimalResponse?.toPhotoMinimalDomain(): PhotoMinimal = PhotoMinimal(
    id = this?.id ?: "undefined",
    createdAt = this?.createdAt ?: "undefined",
    updatedAt = this?.updatedAt ?: "undefined",
    promotedAt = this?.promotedAt ?: "undefined",
    width = this?.width ?: 0,
    height = this?.height ?: 0,
    color = this?.color ?: "undefined",
    blurHash = this?.blurHash ?: "undefined",
    description = this?.description ?: "undefined",
    altDescription = this?.altDescription ?: "undefined",
    urls = this?.urls.toUrlsDomain(),
    links = this?.links.toLinksPhotoDomain(),
    likes = this?.likes ?: 0,
    likedByUser = this?.likedByUser ?: false,
    user = this?.user.toUserDomain()
)

// ---------------------------------- PHOTO DETAIL RESPONSE ----------------------------------

fun PhotoDetailResponse.ExifResponse?.toExifDomain(): PhotoDetail.Exif = PhotoDetail.Exif(
    make = this?.make ?: "undefined",
    model = this?.model ?: "undefined",
    exposureTime = this?.exposureTime ?: "undefined",
    aperture = this?.aperture ?: "undefined",
    focalLength = this?.focalLength ?: "undefined",
    iso = this?.iso ?: 0
)

fun PhotoDetailResponse.LocationResponse.PositionResponse?.toPositionDomain(): PhotoDetail.Location.Position =
    PhotoDetail.Location.Position(
        latitude = this?.latitude ?: 0.0,
        longitude = this?.longitude ?: 0.0
    )

fun PhotoDetailResponse.LocationResponse?.toLocationDomain(): PhotoDetail.Location = PhotoDetail.Location(
    title = this?.title ?: "undefined",
    name = this?.name ?: "undefined",
    city = this?.city ?: "undefined",
    country = this?.country ?: "undefined",
    position = this?.position.toPositionDomain()
)

fun PhotoDetailResponse.toPhotoDetailDomain(): PhotoDetail = PhotoDetail(
    id = this.id ?: "undefined",
    createdAt = this.createdAt ?: "undefined",
    updatedAt = this.updatedAt ?: "undefined",
    promotedAt = this.promotedAt ?: "undefined",
    width = this.width ?: 0,
    height = this.height ?: 0,
    color = this.color ?: "undefined",
    blurHash = this.blurHash ?: "undefined",
    description = this.description ?: "undefined",
    altDescription = this.altDescription ?: "undefined",
    urls = this.urls.toUrlsDomain(),
    links = this.links.toLinksPhotoDomain(),
    likes = this.likes ?: 0,
    likedByUser = this.likedByUser ?: false,
    user = this.user.toUserDomain(),
    exif = this.exif.toExifDomain(),
    location = this.location.toLocationDomain(),
    tags = this.tags.toListTagDomain(),
    relatedCollections = this.relatedCollectionResponse.toListRelatedCollectionDomain(),
    views = this.views ?: 0,
    downloads = this.downloads ?: 0
)

// ---------------------------------- COLLECTION RESPONSE ----------------------------------

fun CollectionResponse.toCollectionDomain(): Collection = Collection(
    id = this.id ?: "undefined",
    title = this.title ?: "undefined",
    description = this.description ?: "undefined",
    publishedAt = this.publishedAt ?: "undefined",
    lastCollectedAt = this.lastCollectedAt ?: "undefined",
    updatedAt = this.updatedAt ?: "undefined",
    curated = this.curated ?: false,
    featured = this.featured ?: false,
    totalPhotos = this.totalPhotos ?: 0,
    private = this.private ?: false,
    shareKey = this.shareKey ?: "undefined",
    tags = this.tags.toListTagDomain(),
    links = this.links.toLinksCollectionDomain(),
    user = this.user.toUserDomain(),
    coverPhoto = this.coverPhoto.toPhotoMinimalDomain()
)

fun List<CollectionResponse>?.toListCollectionDomain(): List<Collection> =
    this?.map { it.toCollectionDomain() } ?: listOf()
