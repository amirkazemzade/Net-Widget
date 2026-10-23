package me.amirkazemzade.netwidget.ui.widgets.remained

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class RemainedGlanceWidgetReceiver1x1 : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = RemainedGlanceWidget1x1()
}
