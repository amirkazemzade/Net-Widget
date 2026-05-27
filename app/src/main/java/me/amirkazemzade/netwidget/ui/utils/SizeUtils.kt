package me.amirkazemzade.netwidget.ui.utils

import android.content.res.Resources
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.TypedValue

fun textFitsInContainer(
    text: String,
    textSizeSp: Float,
    containerWidthDp: Float,
    resources: Resources,
): Boolean {

    val textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        textSizeSp,
        resources.displayMetrics
    )

    val containerWidthPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        containerWidthDp,
        resources.displayMetrics
    )


    val paint = TextPaint().apply {
        this.textSize = textSizePx
        this.isAntiAlias = true
        this.typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC)
    }
    val bounds = Rect()
    paint.getTextBounds(text, 0, text.length, bounds)
    val actualTextWidthPx = bounds.width().toFloat()

    val safetyBufferPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        8f,
        resources.displayMetrics
    )

    return (actualTextWidthPx + safetyBufferPx) < containerWidthPx
}