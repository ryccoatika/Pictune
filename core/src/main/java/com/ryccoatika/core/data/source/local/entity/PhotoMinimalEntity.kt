package com.ryccoatika.core.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoMinimalEntity(
    @PrimaryKey
    var id: String,
    var width: Int,
    var height: Int,
    var color: String,
    var description: String,
    var altDescription: String,
    @Embedded
    var urls: UrlsEntity
)