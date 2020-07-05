package com.ryccoatika.pictune.db.potd

import com.google.gson.annotations.SerializedName

data class PotdBingResponse(
    @SerializedName("images")
    val images: List<PotdBingImage?>? = listOf()
) {
    data class PotdBingImage(
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("url")
        val url: String? = ""
    )
}