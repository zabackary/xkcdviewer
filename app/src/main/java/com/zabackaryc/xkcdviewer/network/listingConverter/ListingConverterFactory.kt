package com.zabackaryc.xkcdviewer.network.listingConverter

import android.util.Log
import com.zabackaryc.xkcdviewer.network.model.ComicListingApiModel
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import java.lang.reflect.Type


class ListingConverterFactory private constructor(private val baseUrl: String, private val applyToUrls: List<String>) :
    Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, List<ComicListingApiModel>>? {
        annotations.forEach { annotation ->
            if (annotation is GET && applyToUrls.contains(annotation.value)) {
                return ListingResponseBodyConverter(baseUrl)
            }
        }
        return null
    }

    companion object {
        fun create(baseUrl: String, applyToUrls: List<String>): ListingConverterFactory {
            return ListingConverterFactory(baseUrl, applyToUrls)
        }
    }
}
