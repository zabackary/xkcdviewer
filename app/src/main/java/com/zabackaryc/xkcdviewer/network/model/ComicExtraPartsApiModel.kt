package com.zabackaryc.xkcdviewer.network.model

import com.google.gson.annotations.SerializedName

data class ComicExtraPartsApiModel(
    @SerializedName("headerextra")
    val headerExtra: String,
    @SerializedName("imgAttr")
    val imgAttr: String,
    @SerializedName("post")
    val post: String,
    @SerializedName("pre")
    val pre: String
)
