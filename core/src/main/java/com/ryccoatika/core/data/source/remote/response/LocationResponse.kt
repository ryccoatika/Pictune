package com.ryccoatika.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("position")
    val position: PositionResponse? = null
) {
    data class PositionResponse(
        @SerializedName("latitude")
        val latitude: Double? = null,
        @SerializedName("longitude")
        val longitude: Double? = null
    )
}