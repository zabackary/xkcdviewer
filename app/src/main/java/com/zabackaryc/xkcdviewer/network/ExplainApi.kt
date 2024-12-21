package com.zabackaryc.xkcdviewer.network

import com.zabackaryc.xkcdviewer.network.model.ExplainApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExplainApi {
    @GET("/wiki/api.php?action=parse&section=1&format=json&redirects")
    suspend fun getExplanation(@Query("page") comicId: Int): Response<ExplainApiModel>
}
