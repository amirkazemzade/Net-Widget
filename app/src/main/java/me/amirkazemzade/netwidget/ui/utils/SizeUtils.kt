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

data class AutoSizeTrafficFont(
    val amountSp: Float,
    val suffixSp: Float,
)

/**
 * Calculates optimal font size (in SP) for a single-line percentage text so that it fits
 * comfortably inside a circular area of diameter [availableDiameterDp] without overflowing.
 */
fun calculateOptimalPercentageFontSizeSp(
    text: String,
    availableDiameterDp: Float,
    resources: Resources? = null,
    maxFontSizeSp: Float = 24f,
    minFontSizeSp: Float = 9f,
): Float {
    if (text.isEmpty() || availableDiameterDp <= 0f) return maxFontSizeSp

    val calculatedSp = runCatching {
        val displayMetrics = resources?.displayMetrics ?: error("No resources")
        val availableDiameterPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            availableDiameterDp,
            displayMetrics
        )
        val testSizeSp = 100f
        val testSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            testSizeSp,
            displayMetrics
        )

        val paint = TextPaint().apply {
            textSize = testSizePx
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val textWidth = maxOf(bounds.width().toFloat(), paint.measureText(text))
        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent

        val diagonal = kotlin.math.sqrt(textWidth * textWidth + textHeight * textHeight)
        if (diagonal <= 0f) return@runCatching maxFontSizeSp

        // 0.85 safety factor gives clearance from the circular gauge rim
        val safeDiameterPx = availableDiameterPx * 0.85f
        testSizeSp * (safeDiameterPx / diagonal)
    }.getOrElse {
        val estWidthPerChar = 0.58f
        val estHeight = 1.0f
        val w = text.length * estWidthPerChar
        val h = estHeight
        val diag = kotlin.math.sqrt(w * w + h * h)
        if (diag > 0) (availableDiameterDp * 0.85f / diag) else maxFontSizeSp
    }

    return calculatedSp.coerceIn(minFontSizeSp, maxFontSizeSp)
}

/**
 * Calculates optimal font size (in SP) for the centered traffic amount number
 * inside the circular gauge area.
 */
fun calculateOptimalAmountFontSizeSp(
    amountText: String,
    availableDiameterDp: Float,
    resources: Resources? = null,
    maxFontSizeSp: Float = 22f,
    minFontSizeSp: Float = 10f,
): Float {
    if (amountText.isEmpty() || availableDiameterDp <= 0f) return maxFontSizeSp

    val calculatedSp = runCatching {
        val displayMetrics = resources?.displayMetrics ?: error("No resources")
        val availableDiameterPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            availableDiameterDp,
            displayMetrics
        )
        val testSizeSp = 100f
        val testSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            testSizeSp,
            displayMetrics
        )

        val paint = TextPaint().apply {
            textSize = testSizePx
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val bounds = Rect()
        paint.getTextBounds(amountText, 0, amountText.length, bounds)
        val textWidth = maxOf(bounds.width().toFloat(), paint.measureText(amountText))
        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent

        val safeWidthPx = availableDiameterPx * 0.85f
        val safeHeightPx = availableDiameterPx * 0.55f

        val scaleW = safeWidthPx / textWidth
        val scaleH = safeHeightPx / textHeight
        testSizeSp * minOf(scaleW, scaleH)
    }.getOrElse {
        val estW = amountText.length * 0.55f
        val estH = 1.0f
        val scaleW = (availableDiameterDp * 0.85f) / (estW * 20f)
        val scaleH = (availableDiameterDp * 0.55f) / (estH * 20f)
        20f * minOf(scaleW, scaleH)
    }

    return calculatedSp.coerceIn(minFontSizeSp, maxFontSizeSp)
}

/**
 * Calculates optimal font size (in SP) for the traffic suffix (GB, MB, etc.)
 * placed in the empty opening below the arc.
 */
fun calculateOptimalSuffixFontSizeSp(
    suffixText: String,
    availableWidthDp: Float,
    resources: Resources? = null,
    maxFontSizeSp: Float = 12f,
    minFontSizeSp: Float = 8f,
): Float {
    if (suffixText.isEmpty() || availableWidthDp <= 0f) return maxFontSizeSp

    val calculatedSp = runCatching {
        val displayMetrics = resources?.displayMetrics ?: error("No resources")
        val availableWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            availableWidthDp,
            displayMetrics
        )
        val testSizeSp = 100f
        val testSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            testSizeSp,
            displayMetrics
        )

        val paint = TextPaint().apply {
            textSize = testSizePx
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val bounds = Rect()
        paint.getTextBounds(suffixText, 0, suffixText.length, bounds)
        val textWidth = maxOf(bounds.width().toFloat(), paint.measureText(suffixText))

        val safeWidthPx = availableWidthPx * 0.88f
        testSizeSp * (safeWidthPx / textWidth)
    }.getOrElse {
        val estW = suffixText.length * 0.55f
        val scaleW = (availableWidthDp * 0.88f) / (estW * 12f)
        12f * scaleW
    }

    return calculatedSp.coerceIn(minFontSizeSp, maxFontSizeSp)
}

/**
 * Calculates optimal font sizes (in SP) for a stacked two-line traffic text (amount + suffix).
 */
fun calculateOptimalTrafficFontSizeSp(
    amountText: String,
    suffixText: String,
    availableDiameterDp: Float,
    resources: Resources? = null,
    maxAmountSp: Float = 20f,
    minAmountSp: Float = 8f,
    maxSuffixSp: Float = 13f,
    minSuffixSp: Float = 6f,
): AutoSizeTrafficFont {
    if (availableDiameterDp <= 0f) {
        return AutoSizeTrafficFont(maxAmountSp, maxSuffixSp)
    }

    val result = runCatching {
        val displayMetrics = resources?.displayMetrics ?: error("No resources")
        val availableDiameterPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            availableDiameterDp,
            displayMetrics
        )

        val testAmountSp = 100f
        val testSuffixSp = 60f

        val testAmountPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, testAmountSp, displayMetrics)
        val testSuffixPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, testSuffixSp, displayMetrics)

        val amountPaint = TextPaint().apply {
            textSize = testAmountPx
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        val suffixPaint = TextPaint().apply {
            textSize = testSuffixPx
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val amountBounds = Rect()
        amountPaint.getTextBounds(amountText, 0, amountText.length, amountBounds)
        val amountWidth = maxOf(amountBounds.width().toFloat(), amountPaint.measureText(amountText))
        val amountHeight = amountPaint.fontMetrics.let { it.descent - it.ascent }

        val suffixBounds = Rect()
        suffixPaint.getTextBounds(suffixText, 0, suffixText.length, suffixBounds)
        val suffixWidth = maxOf(suffixBounds.width().toFloat(), suffixPaint.measureText(suffixText))
        val suffixHeight = suffixPaint.fontMetrics.let { it.descent - it.ascent }

        val totalWidth = maxOf(amountWidth, suffixWidth)
        val spacing = 2f * displayMetrics.density * (testAmountSp / 20f)
        val totalHeight = amountHeight + suffixHeight + spacing

        val diagonal = kotlin.math.sqrt(totalWidth * totalWidth + totalHeight * totalHeight)
        if (diagonal <= 0f) return@runCatching AutoSizeTrafficFont(maxAmountSp, maxSuffixSp)

        val safeDiameterPx = availableDiameterPx * 0.84f
        val targetScale = safeDiameterPx / diagonal

        val calculatedAmountSp = (testAmountSp * targetScale).coerceIn(minAmountSp, maxAmountSp)
        val calculatedSuffixSp = (testSuffixSp * targetScale).coerceIn(minSuffixSp, maxSuffixSp)

        AutoSizeTrafficFont(
            amountSp = calculatedAmountSp,
            suffixSp = calculatedSuffixSp,
        )
    }.getOrElse {
        val wA = amountText.length * 0.55f
        val hA = 1.0f
        val wS = suffixText.length * 0.50f * 0.6f
        val hS = 0.6f
        val totalW = maxOf(wA, wS)
        val totalH = hA + hS + 0.15f
        val diag = kotlin.math.sqrt(totalW * totalW + totalH * totalH)
        val scale = if (diag > 0) (availableDiameterDp * 0.84f / diag) / 20f else 1f

        AutoSizeTrafficFont(
            amountSp = (20f * scale).coerceIn(minAmountSp, maxAmountSp),
            suffixSp = (12f * scale).coerceIn(minSuffixSp, maxSuffixSp),
        )
    }

    return result
}
