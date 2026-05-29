package me.amirkazemzade.netwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.amirkazemzade.netwidget.ui.config.WidgetConfigScreen
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme
import me.amirkazemzade.netwidget.ui.widgets.remained.RemainedGlanceWidget1x4

@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // If user exits without saving, the widget placement is canceled
        setResult(RESULT_CANCELED)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        // Close if couldn't retrieve the widget id
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID || appWidgetId == null) {
            finish()
            return
        }

        setContent {
            NetWidgetAppTheme {
                val surface = MaterialTheme.colorScheme.surface
                val window = this.window

                SideEffect {
                    @Suppress("DEPRECATION")
                    window.statusBarColor = surface.toArgb()
                    @Suppress("DEPRECATION")
                    window.navigationBarColor = surface.toArgb()
                }

                WidgetConfigScreen(
                    showNavIcon = false,
                    onNavigateBack = {
                        finish()
                    },
                    onUpdateWidget = {
                        completeWidgetConfiguration(
                            context = this,
                            appWidgetId = appWidgetId,
                        )
                    }
                )
            }
        }

    }

    private fun completeWidgetConfiguration(
        context: Context,
        appWidgetId: Int,
    ) {

        // Tell launcher configuration succeeded
        val result = Intent().apply {
            putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId
            )
        }

        setResult(RESULT_OK, result)

        lifecycleScope.launch {
            RemainedGlanceWidget1x4().update(
                context = context,
                id = GlanceAppWidgetManager(this@WidgetConfigActivity)
                    .getGlanceIdBy(appWidgetId)
            )

            finish()
        }
    }
}
