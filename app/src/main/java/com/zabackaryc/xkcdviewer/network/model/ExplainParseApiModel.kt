package com.zabackaryc.xkcdviewer.network.model

import com.google.gson.annotations.SerializedName

data class ExplainParseApiModel(
    @SerializedName("title")
    val title: String,
    @SerializedName("pageid")
    val pageId: Long,
    @SerializedName("text")
    val text: ExplainParseTextApiModel
    )

data class ExplainParseTextApiModel(
    @SerializedName("*")
    val content: String
)
