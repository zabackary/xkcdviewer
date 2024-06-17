package com.zabackaryc.xkcdviewer.ui.comic

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import com.zabackaryc.xkcdviewer.R
import com.zabackaryc.xkcdviewer.data.CachedComic
import com.zabackaryc.xkcdviewer.data.ListedComic

@Suppress("unused")
sealed class ComicActionType(
    /** The short name of the action */
    val shortName: String,
    /** The verbal form of the action, i.e. 'open xyz', for use in the screen reader label */
    val actionableName: String,
    /**
     * A contextual name, e.g. for toggleable actions or content previews.
     * Shown as supporting list item text or replacing the shortName in tooltips
     */
    val contextualName: ComicScope.() -> String? = { null },
    /** A callback when the action is run */
    val action: suspend ComicActionScope.() -> Unit,
    /** An icon for the action, either an [ImageVector] or [Int] representing a [DrawableRes] */
    val icon: ComicScope.() -> Any,
    /** Whether a badge should be shown */
    val showBadge: ComicScope.() -> Boolean = { false },
    /** Whether the action should be available */
    val show: ComicScope.() -> Boolean = { true },
    /** A stable ID for identifying an action */
    val id: Int,
    /** Whether the menu should close before the action is run */
    val closeBeforeRun: Boolean = true
) {
    open class ComicScope(val listedComic: ListedComic, val cachedComic: CachedComic)

    class ComicActionScope(
        listedComic: ListedComic,
        cachedComic: CachedComic,
        val viewModel: ComicViewModel,
        val context: Context,
        val snackbarHostState: SnackbarHostState
    ) : ComicScope(listedComic, cachedComic)

    data object FavoriteStatusActionType : ComicActionType(
        shortName = "Favorite",
        actionableName = "Toggle favorite on comic",
        contextualName = { if (listedComic.favorite) "Remove favorite" else "Add favorite" },
        action = {
            val newFavoriteValue = !listedComic.favorite
            viewModel.setFavoriteComic(listedComic.id, newFavoriteValue)
            snackbarHostState.showSnackbar(
                if (newFavoriteValue) "Added '${listedComic.title}' to favorites"
                else "Removed '${listedComic.title}' from favorites"
            )
        },
        icon = { if (listedComic.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder },
        id = 0,
        closeBeforeRun = false
    )

    data object ShareActionType : ComicActionType(
        shortName = "Share",
        actionableName = "Share comic",
        action = {
            viewModel.shareComic(context, listedComic, cachedComic)
        },
        icon = { Icons.Default.Share },
        id = 1
    )

    data object TranscriptActionType : ComicActionType(
        shortName = "Transcript",
        actionableName = "Open official transcript",
        action = {
            // TODO: implement
        },
        icon = { Icons.Default.Description },
        show = {
            cachedComic.transcript != null
        },
        id = 2
    )

    data object ExplainActionType : ComicActionType(
        shortName = "Explain",
        actionableName = "Open explain xkcd explanation",
        action = {
            // TODO: should respect explain setting
            viewModel.explainComicInBrowser(context, listedComic, true)
        },
        icon = { R.drawable.explainxkcd_icon },
        id = 3
    )

    data object LinkActionType : ComicActionType(
        shortName = "Linked URL",
        actionableName = "Open linked URL in browser",
        contextualName = { "Linked URL: ${cachedComic.link}" },
        action = {
            if (cachedComic.link != null) viewModel.openLinkedURL(context, cachedComic.link)
        },
        icon = { Icons.Default.Link },
        showBadge = { true },
        show = { cachedComic.link != null },
        id = 4
    )

    companion object {
        val allActionTypes = ComicActionType::class.sealedSubclasses.mapNotNull {
            it.objectInstance
        }
    }
}
