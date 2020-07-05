package com.ryccoatika.pictune.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FavoritePhotoEntity.TABLE_NAME)
data class FavoritePhotoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_ID)
    val photoId: String
) {
    companion object {
        const val TABLE_NAME = "unsplash_photos"
        const val COLUMN_ID = "idPhoto"
    }
}