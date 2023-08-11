package com.zabackaryc.xkcdviewer.network.model

import com.google.gson.annotations.SerializedName

data class ExplainApiModel(
    @SerializedName("parse")
    val parse: ExplainParseApiModel
)
