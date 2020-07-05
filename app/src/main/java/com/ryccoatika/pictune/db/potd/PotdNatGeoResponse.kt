package com.ryccoatika.pictune.db.potd

import com.google.gson.annotations.SerializedName

data class PotdNatGeoResponse (
    @SerializedName("items")
    val items: List<NatGeoItem?>? = listOf()
) {
    data class NatGeoItem (
        @SerializedName("image")
        val image: NatGeoImage? = NatGeoImage()
    )

    data class NatGeoImage (
        @SerializedName("id")
        val id: String? = "",
        @SerializedName("uri")
        val uri: String? = "",
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("caption")
        val caption: String? = ""
    )
}