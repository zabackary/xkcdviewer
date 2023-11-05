package com.zabackaryc.xkcdviewer.utils

sealed class SettingsItem<T>(val preferenceKey: String, val defaultValue: T) {
    sealed class BooleanSettingsItem(
        preferenceKey: String,
        defaultValue: Boolean
    ) : SettingsItem<Boolean>(preferenceKey, defaultValue)

    object ComicActionsExpand : BooleanSettingsItem("comic:actions-expand", true)
    object ComicSaveHistory : BooleanSettingsItem("comic:save-history", true)
    object ComicDownload : SettingsItem<Int>("comic:download", Values.NONE.ordinal) {
        // Ordinal is serialized into preferences
        enum class Values {
            ALL,
            FAVORITES,
            NONE
        }
    }

    object ComicDarkTheme : BooleanSettingsItem("comic:dark-theme", true)

    object ArticleDarkTheme : BooleanSettingsItem("article:dark-theme", true)
}
