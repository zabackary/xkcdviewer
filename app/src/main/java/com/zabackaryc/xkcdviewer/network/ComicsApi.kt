package com.zabackaryc.xkcdviewer.network

import com.zabackaryc.xkcdviewer.network.model.ComicApiModel
import com.zabackaryc.xkcdviewer.network.model.ComicListingApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ComicsApi {
    @GET("/{comicId}/info.0.json")
    suspend fun getComic(@Path("comicId") comicId: Int): Response<ComicApiModel>

    @GET("/archive/")
    suspend fun getListing(): Response<List<ComicListingApiModel>>
}
