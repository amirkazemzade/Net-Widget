package me.amirkazemzade.netwidget.ui.widgets.remained

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun setRemainedWidgetPreview(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        return
    }
    fun Class<GlanceAppWidgetReceiver>.hasPreviewForCategory(widgetCategory: Int): Boolean {
        val component = ComponentName(context, this)
        val providerInfo =
            (context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager)
                .installedProviders
                .first { providerInfo -> providerInfo.provider == component }
        return providerInfo.generatedPreviewCategories.and(widgetCategory) != 0
    }

    val receiverClasses = listOf(
        RemainedGlanceWidgetReceiver1x4::class.java as Class<GlanceAppWidgetReceiver>,
        RemainedGlanceWidgetReceiver1x1::class.java as Class<GlanceAppWidgetReceiver>,
    )
    val glanceAppWidgetManager = GlanceAppWidgetManager(context)
    withContext(Dispatchers.Default) {
        try {
            for (receiver in receiverClasses) {
                if (receiver.hasPreviewForCategory(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN)) {
                    Log.i("Widget", "Skipped updating previews for $receiver")
                    continue
                }
                if (
                    glanceAppWidgetManager.setWidgetPreviews(receiver.kotlin) ==
                    GlanceAppWidgetManager.SET_WIDGET_PREVIEWS_RESULT_RATE_LIMITED
                ) {
                    Log.e("Widget", "Failed to set previews for $receiver, rate limited")
                }
            }
        } catch (e: Exception) {
            Log.e("Widget", "Error thrown when calling setWidgetPreview", e)
        }
    }

}