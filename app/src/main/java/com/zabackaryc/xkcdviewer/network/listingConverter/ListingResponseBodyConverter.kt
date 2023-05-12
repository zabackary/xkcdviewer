package com.zabackaryc.xkcdviewer.network.listingConverter

import com.zabackaryc.xkcdviewer.network.model.ComicListingApiModel
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter


class ListingResponseBodyConverter(private val baseUrl: String) :
    Converter<ResponseBody, List<ComicListingApiModel>> {

    override fun convert(body: ResponseBody): List<ComicListingApiModel> {
        return body.use { value ->
            Jsoup.parse(value.byteStream(), "UTF-8", baseUrl)
                .select("#middleContainer a")
                .map { comic ->
                    ComicListingApiModel(
                        id = comic.attr("href").removeSurrounding("/").toInt(),
                        date = comic.attr("title"),
                        title = comic.text()
                    )
                }
        }
    }
}
