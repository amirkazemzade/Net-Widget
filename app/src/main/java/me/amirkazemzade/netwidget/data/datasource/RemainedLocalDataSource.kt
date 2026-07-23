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
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic
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
    }

    fun dataDisplayModeKey(widgetId: Int) = stringPreferencesKey("data_display_mode_$widgetId")

    fun spellingModeKey(widgetId: Int) = stringPreferencesKey("spelling_mode_$widgetId")

    val remainedData: Flow<Remained?> = dataStore.data.map { preferences ->
        preferences.toRemained()
    }

    fun getDataDisplayMode(widgetId: Int): Flow<DataDisplayMode?> =
        dataStore.data.map { preferences ->
            preferences.toDataDisplayMode(widgetId)
        }


    fun getSpellingMode(widgetId: Int): Flow<SpellingMode?> = dataStore.data.map { preferences ->
        preferences.toSpellingMode(widgetId)
    }

    suspend fun setRemained(remained: Remained) {
        dataStore.edit { preferences ->
            preferences[TRAFFIC_IN_MB_KEY] = remained.traffic.amountInMb
            preferences[PERCENTAGE_KEY] = remained.percentage
        }
    }

    suspend fun setDataDisplayMode(widgetId: Int, mode: DataDisplayMode) {
        dataStore.edit { preferences ->
            preferences[dataDisplayModeKey(widgetId)] = mode.name
        }
    }

    suspend fun setSpellingMode(widgetId: Int, mode: SpellingMode) {
        dataStore.edit { preferences ->
            preferences[spellingModeKey(widgetId = widgetId)] = mode.name
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

    private fun Preferences.toDataDisplayMode(widgetId: Int): DataDisplayMode? {
        val rawValue = this[dataDisplayModeKey(widgetId)] ?: return null
        return DataDisplayMode.valueOfOrNull(rawValue)
    }

    private fun Preferences.toSpellingMode(widgetId: Int): SpellingMode? {
        val rawValue = this[spellingModeKey(widgetId)] ?: return null
        return SpellingMode.valueOfOrNull(rawValue)
    }
}