package com.zabackaryc.xkcdviewer.utils

import androidx.compose.runtime.Composable
import com.alorma.compose.settings.storage.base.SettingValueState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceFloatSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceIntSetSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceIntSettingState

sealed class SettingsItem<T>(val preferenceKey: String, val defaultValue: T) {
    @Composable
    open fun rememberPreferenceState(): SettingValueState<T> {
        throw NotImplementedError()
    }

    val currentValue: T
        @Composable
        get() = this.rememberPreferenceState().value

    sealed class BooleanSettingsItem(
        preferenceKey: String,
        defaultValue: Boolean
    ) : SettingsItem<Boolean>(preferenceKey, defaultValue) {
        @Composable
        override fun rememberPreferenceState() = rememberPreferenceBooleanSettingState(
            key = this.preferenceKey,
            defaultValue = this.defaultValue
        )
    }

    @Suppress("unused")
    sealed class IntSetSettingsItem(
        preferenceKey: String,
        defaultValue: Set<Int>
    ) : SettingsItem<Set<Int>>(preferenceKey, defaultValue) {
        @Composable
        override fun rememberPreferenceState() = rememberPreferenceIntSetSettingState(
            key = this.preferenceKey,
            defaultValue = this.defaultValue
        )
    }

    sealed class IntSettingsItem(
        preferenceKey: String,
        defaultValue: Int
    ) : SettingsItem<Int>(preferenceKey, defaultValue) {
        @Composable
        override fun rememberPreferenceState() = rememberPreferenceIntSettingState(
            key = this.preferenceKey,
            defaultValue = this.defaultValue
        )
    }

    @Suppress("unused")
    sealed class FloatSettingsItem(
        preferenceKey: String,
        defaultValue: Float
    ) : SettingsItem<Float>(preferenceKey, defaultValue) {
        @Composable
        override fun rememberPreferenceState() = rememberPreferenceFloatSettingState(
            key = this.preferenceKey,
            defaultValue = this.defaultValue
        )
    }

    data object AppNewsBanner : BooleanSettingsItem("app:news-banner", true)
    data object ComicActionsExpand : BooleanSettingsItem("comic:actions-expand", true)
    data object ComicSaveHistory : BooleanSettingsItem("comic:save-history", true)
    data object ComicDownload : IntSettingsItem("comic:download", Values.NONE.ordinal) {
        // Ordinal is serialized into preferences
        @Suppress("unused")
        enum class Values {
            ALL,
            FAVORITES,
            NONE
        }
    }

    data object ComicDarkTheme : BooleanSettingsItem("comic:dark-theme", true)
    data object ComicMetadataPopup : BooleanSettingsItem("comic:metadata-popup", true)
    data object ArticleDarkTheme : BooleanSettingsItem("article:dark-theme", true)
}
