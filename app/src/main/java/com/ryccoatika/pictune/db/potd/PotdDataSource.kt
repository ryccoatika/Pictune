package com.ryccoatika.pictune.db.potd

import com.ryccoatika.pictune.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PotdDataSource {
    @GET("https://api.nasa.gov/planetary/apod")
    fun getNasaPotd(
        @Query("api_key")
        apiKey: String = BuildConfig.NASA_API_KEY,
        @Query("hd")
        hd: Boolean = true
    ): Single<PotdNasaResponse>

    @GET("https://www.nationalgeographic.com/photography/photo-of-the-day/_jcr_content/.gallery.json")
    fun getNatGeoPotd(): Single<PotdNatGeoResponse>

    @GET("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1")
    fun getBingPotd(): Single<PotdBingResponse>
}
