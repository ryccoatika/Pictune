package com.ryccoatika.core.domain.model

data class PhotoMinimal(
    var id: String,
    var width: Int,
    var height: Int,
    var color: String,
    var description: String,
    var altDescription: String,
    var urls: Urls
)