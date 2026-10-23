package me.amirkazemzade.netwidget.ui.utils

import android.content.Context
import android.icu.text.NumberFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic

fun Traffic.toReadableTrafficParts(
    context: Context,
    spellingMode: SpellingMode = SpellingMode.Short,
): Pair<String, String> {
    val (amount, suffix) = when {
        amountInMb >= 1024 -> {
            toGB() to when (spellingMode) {
                SpellingMode.Full -> context.getString(R.string.gigabyte_full)
                SpellingMode.Short -> context.getString(R.string.gigabyte_short)
            }
        }

        else -> toMB() to when (spellingMode) {
            SpellingMode.Full -> context.getString(R.string.megabyte_full)
            SpellingMode.Short -> context.getString(R.string.megabyte_short)
        }
    }

    val currentLocale = context.resources.configuration.locales[0]
    val numberFormatter = NumberFormat.getInstance(currentLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = if (amountInMb >= 1024) 2 else 0
    }
    val amountFormatted = numberFormatter.format(amount)
    return amountFormatted to suffix
}

fun Traffic.toReadableTrafficText(
    context: Context,
    spellingMode: SpellingMode = SpellingMode.Short,
): String {
    val (amount, suffix) = toReadableTrafficParts(context, spellingMode)
    return "$amount $suffix"
}

fun Float.toReadablePercentText(context: Context): String {
    val currentLocale = context.resources.configuration.locales[0]
    val percentFormatter = NumberFormat.getPercentInstance(currentLocale).apply {
        maximumFractionDigits = 0
    }
    return percentFormatter.format(this)
}

@Composable
fun Traffic.toReadableTrafficText(
    spellingMode: SpellingMode = SpellingMode.Short,
): String = toReadableTrafficText(LocalContext.current, spellingMode)

@Composable
fun Float.toReadablePercentText(): String = toReadablePercentText(LocalContext.current)

