package com.ryccoatika.core.domain.repository

import androidx.annotation.StringRes
import com.ryccoatika.core.R

enum class DiscoverPhotoOrder(
    @StringRes
    val titleRes: Int,
    val value: String
) {
    LATEST(R.string.latest, "latest"),
    OLDEST(R.string.oldest, "oldest"),
    POPULAR(R.string.popular, "popular")
}