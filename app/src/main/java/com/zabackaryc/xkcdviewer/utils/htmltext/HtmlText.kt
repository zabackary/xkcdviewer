package com.zabackaryc.xkcdviewer.utils.htmltext

import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat

private const val URL_TAG = "url_tag"

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    linkClicked: ((String) -> Unit)? = null,
    fontSize: TextUnit = 14.sp,
    flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT,
    urlSpanStyle: SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline
    )
) {
    val content = text.asHTML(fontSize, flags, urlSpanStyle)
    if (linkClicked != null) {
        ClickableText(
            modifier = modifier,
            text = content,
            style = style,
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = onTextLayout,
            onClick = {
                content
                    .getStringAnnotations(URL_TAG, it, it)
                    .firstOrNull()
                    ?.let { stringAnnotation -> linkClicked(stringAnnotation.item) }
            }
        )
    } else {
        Text(
            modifier = modifier,
            text = content,
            style = style,
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines,
            onTextLayout = onTextLayout
        )
    }

}

@Composable
private fun String.asHTML(
    fontSize: TextUnit,
    flags: Int,
    urlSpanStyle: SpanStyle
) = buildAnnotatedString {
    val spanned = HtmlCompat.fromHtml(this@asHTML, flags)
    val spans = spanned.getSpans(0, spanned.length, Any::class.java)

    append(spanned.toString())

    spans
        .filter { it !is BulletSpan }
        .forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is RelativeSizeSpan -> span.spanStyle(fontSize)
                is StyleSpan -> span.spanStyle()
                is UnderlineSpan -> span.spanStyle()
                is ForegroundColorSpan -> span.spanStyle()
                is TypefaceSpan -> span.spanStyle()
                is StrikethroughSpan -> span.spanStyle()
                is SuperscriptSpan -> span.spanStyle()
                is SubscriptSpan -> span.spanStyle()
                is URLSpan -> {
                    addStringAnnotation(
                        tag = URL_TAG,
                        annotation = span.url,
                        start = start,
                        end = end
                    )
                    urlSpanStyle
                }

                else -> {
                    null
                }
            }?.let { spanStyle ->
                addStyle(spanStyle, start, end)
            }
        }
}
