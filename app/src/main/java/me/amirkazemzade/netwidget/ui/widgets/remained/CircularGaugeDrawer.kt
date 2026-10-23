package me.amirkazemzade.netwidget.ui.widgets.remained

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import me.amirkazemzade.netwidget.domain.models.WidgetThemeMode

object CircularGaugeDrawer {

    const val START_ANGLE = 140f
    const val TOTAL_SWEEP_ANGLE = 260f

    fun drawGaugeBitmap(
        percentage: Float,
        isLow: Boolean,
        themeMode: WidgetThemeMode,
        sizePx: Int = 320,
        primaryColor: Int? = null,
        errorColor: Int? = null,
        trackColor: Int? = null,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val strokeWidth = sizePx * 0.11f
        val padding = strokeWidth / 2f + sizePx * 0.05f
        val rect = RectF(padding, padding, sizePx - padding, sizePx - padding)

        val clampedProgress = percentage.coerceIn(0f, 1f)
        val activeSweep = clampedProgress * TOTAL_SWEEP_ANGLE

        val (actualTrackColor, actualActiveColor) = when (themeMode) {
            WidgetThemeMode.OPAQUE -> {
                val track = trackColor ?: Color.argb(40, 128, 128, 128)
                val active = if (isLow) {
                    errorColor ?: Color.rgb(229, 57, 53)
                } else {
                    primaryColor ?: Color.rgb(63, 110, 237)
                }
                track to active
            }

            WidgetThemeMode.TRANSPARENT -> {
                val track = trackColor ?: Color.argb(45, 255, 255, 255)
                val active = if (isLow) {
                    Color.rgb(255, 82, 82)
                } else {
                    Color.rgb(0, 220, 255)
                }
                track to active
            }
        }

        // Draw background track
        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
            color = actualTrackColor
        }
        canvas.drawArc(rect, START_ANGLE, TOTAL_SWEEP_ANGLE, false, trackPaint)

        // Draw active arc and optional glow blur
        if (activeSweep > 0f) {
            if (themeMode == WidgetThemeMode.TRANSPARENT) {
                val blurRadius = sizePx * 0.045f
                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    this.strokeWidth = strokeWidth * 1.55f
                    strokeCap = Paint.Cap.ROUND
                    color = actualActiveColor
                    alpha = 110
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawArc(rect, START_ANGLE, activeSweep, false, glowPaint)
            }

            val activePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                this.strokeWidth = strokeWidth
                strokeCap = Paint.Cap.ROUND
                color = actualActiveColor
            }
            canvas.drawArc(rect, START_ANGLE, activeSweep, false, activePaint)
        }

        return bitmap
    }
}
