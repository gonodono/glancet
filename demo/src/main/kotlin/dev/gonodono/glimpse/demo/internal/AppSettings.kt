package dev.gonodono.glimpse.demo.internal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val Context.appSettings: AppSettings
    get() = AppSettings(appSettingsDataStore)

@JvmInline
internal value class AppSettings(private val settings: DataStore<Preferences>) {

    val finishOnPin: Flow<Boolean>
        get() = settings.data.map { it[FinishOnPin] ?: true }

    suspend fun toggleFinishOnPin() {
        settings.edit { preferences ->
            val current = preferences[FinishOnPin] ?: true
            preferences[FinishOnPin] = !current
        }
    }
}

private val Context.appSettingsDataStore: DataStore<Preferences>
        by preferencesDataStore("app_settings")

private val FinishOnPin = booleanPreferencesKey("finish_on_pin")