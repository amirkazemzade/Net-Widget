package me.amirkazemzade.netwidget.ui.config.defaults

import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.Traffic
import kotlin.math.roundToLong

object WidgetConfigDefaults {
    val defaultRemainedValue = Remained(
        traffic = Traffic((6.7f * 1024).roundToLong()),
        percentage = 0.67f,
    )
}