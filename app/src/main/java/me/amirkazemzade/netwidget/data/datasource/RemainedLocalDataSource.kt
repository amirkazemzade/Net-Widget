package me.amirkazemzade.netwidget.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetSharedDataStore by preferencesDataStore("remained_local_data_source")

@Singleton
class RemainedLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val dataStore: DataStore<Preferences> = context.widgetSharedDataStore

    companion object {

        val TRAFFIC_IN_MB_KEY = longPreferencesKey("traffic_in_mb")
        val PERCENTAGE_KEY = floatPreferencesKey("percentage")

        val DATA_DISPLAY_MODE = stringPreferencesKey("data_display_mode")
        val SPELLING_MODE = stringPreferencesKey("spelling_mode")
    }

    val remainedData: Flow<Remained?> = dataStore.data.map { preferences ->
        preferences.toRemained()
    }

    val dataDisplayMode: Flow<DataDisplayMode?> = dataStore.data.map { preferences ->
        preferences.toDataDisplayMode()
    }

    val spellingMode: Flow<SpellingMode?> = dataStore.data.map { preferences ->
        preferences.toSpellingMode()
    }

    suspend fun setRemained(remained: Remained) {
        dataStore.edit { preferences ->
            preferences[TRAFFIC_IN_MB_KEY] = remained.traffic.amountInMb
            preferences[PERCENTAGE_KEY] = remained.percentage
        }
    }

    suspend fun setDataDisplayMode(mode: DataDisplayMode) {
        dataStore.edit { preferences ->
            preferences[DATA_DISPLAY_MODE] = mode.name
        }
    }

    suspend fun setSpellingMode(mode: SpellingMode) {
        dataStore.edit { preferences ->
            preferences[SPELLING_MODE] = mode.name
        }
    }


    private fun Preferences.toRemained(): Remained? {
        val trafficInMb = this[TRAFFIC_IN_MB_KEY] ?: return null
        val percentage = this[PERCENTAGE_KEY] ?: return null

        return Remained(
            traffic = Traffic(amountInMb = trafficInMb),
            percentage = percentage
        )
    }

    private fun Preferences.toDataDisplayMode(): DataDisplayMode? {
        val rawValue = this[DATA_DISPLAY_MODE] ?: return null
        return DataDisplayMode.valueOfOrNull(rawValue)
    }

    private fun Preferences.toSpellingMode(): SpellingMode? {
        val rawValue = this[SPELLING_MODE] ?: return null
        return SpellingMode.valueOfOrNull(rawValue)
    }
}