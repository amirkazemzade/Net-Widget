package me.amirkazemzade.netwidget.ui.widgets.remained

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager

/**
 * Updates all instances of 1x4 and 1x1 Remained widgets.
 *
 * Instead of relying on Glance's internal updateAll(), which uses a DataStore
 * receiver mapping that can conflate providers in R8-minified builds
 * (Google Issue #371791302), this queries the platform AppWidgetManager directly
 * by ComponentName. Each instance maintains its own independent configuration.
 */
suspend fun updateAllRemainedWidgets(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val glanceManager = GlanceAppWidgetManager(context)

    // Strictly update 1x1 widgets
    val ids1x1 = appWidgetManager.getAppWidgetIds(
        ComponentName(context, RemainedGlanceWidgetReceiver1x1::class.java)
    )
    for (id in ids1x1) {
        try {
            val glanceId = glanceManager.getGlanceIdBy(id)
            RemainedGlanceWidget1x1().update(context, glanceId)
        } catch (_: Exception) {
            // Ignore unplaced or invalid widget id
        }
    }

    // Strictly update 1x4 widgets
    val ids1x4 = appWidgetManager.getAppWidgetIds(
        ComponentName(context, RemainedGlanceWidgetReceiver1x4::class.java)
    )
    for (id in ids1x4) {
        try {
            val glanceId = glanceManager.getGlanceIdBy(id)
            RemainedGlanceWidget1x4().update(context, glanceId)
        } catch (_: Exception) {
            // Ignore unplaced or invalid widget id
        }
    }
}
