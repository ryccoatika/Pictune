package com.ryccoatika.pictune.download

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadDataSource {
    @Streaming
    @GET
    fun downloadImage(
        @Url
        url: String
    ): Call<ResponseBody>
}