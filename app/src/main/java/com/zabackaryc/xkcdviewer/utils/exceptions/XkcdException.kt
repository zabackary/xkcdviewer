package com.zabackaryc.xkcdviewer.utils.exceptions

import com.google.gson.annotations.SerializedName

data class XkcdException(
    @SerializedName("id")
    val id: Int,

    /**
     * A message to display with the comic.
     */
    @SerializedName("message")
    val message: String? = null,

    /**
     * The comic is broken if this is not null. To fix, rotate by this value.
     */
    @SerializedName("rotate")
    val rotate: Int? = null,

    /**
     * There isn't a 2x version of the comic. This breaks some comics, e.g. #1667
     */
    val srcsetUnavailable: Boolean? = null,

    /**
     * Larger version of the comic.
     */
    @SerializedName("largeUrl")
    val largeUrl: String? = null,

    /**
     * The comic is interactive and the placeholder static image is wrong/invalid. Use this instead.
     */
    @SerializedName("previewUrl")
    val previewUrl: String? = null,

    /**
     * The comic is a animation but it takes a long time (days, hours, etc.) to play. This is faster.
     */
    @SerializedName("animatedUrl")
    val animatedUrl: String? = null
)
