package me.amirkazemzade.netwidget.ui.config.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.domain.models.WidgetThemeMode
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalAmountFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalPercentageFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalSuffixFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.toReadablePercentText
import me.amirkazemzade.netwidget.ui.utils.toReadableTrafficParts
import me.amirkazemzade.netwidget.ui.widgets.remained.CircularGaugeDrawer

@Composable
fun RemainedWidget1x1Preview(
    dataDisplayMode: DataDisplayMode,
    modifier: Modifier = Modifier,
    spellingMode: SpellingMode = SpellingMode.Short,
    themeMode: WidgetThemeMode = WidgetThemeMode.OPAQUE,
    remained: Remained = Remained(
        traffic = Traffic(7 * 1024),
        percentage = 0.67f,
    ),
) {
    val context = LocalContext.current
    val isLow = remained.percentage < 0.33f

    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val errorColor = MaterialTheme.colorScheme.error.toArgb()
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()

    val gaugeBitmap = remember(remained.percentage, isLow, primaryColor, errorColor, trackColor) {
        CircularGaugeDrawer.drawGaugeBitmap(
            percentage = remained.percentage,
            isLow = isLow,
            themeMode = WidgetThemeMode.OPAQUE,
            sizePx = 320,
            primaryColor = primaryColor,
            errorColor = errorColor,
            trackColor = trackColor,
        )
    }

    val primaryTextColor = if (isLow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val containerModifier = Modifier
        .size(130.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.surface)
        .padding(8.dp)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = containerModifier,
            contentAlignment = Alignment.Center,
        ) {
            Image(
                bitmap = gaugeBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )

            val availableDiameterDp = (130f - 16f) * 0.60f

            when (dataDisplayMode) {
                DataDisplayMode.PERCENTAGE -> {
                    val percentText = remained.percentage.toReadablePercentText(context)
                    val fontSize = calculateOptimalPercentageFontSizeSp(
                        text = percentText,
                        availableDiameterDp = availableDiameterDp,
                        resources = context.resources,
                    )
                    Text(
                        text = percentText,
                        color = primaryTextColor,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                DataDisplayMode.TRAFFIC -> {
                    // For 1x1, always use short text (e.g. GB, MB)
                    val (amount, suffix) = remained.traffic.toReadableTrafficParts(context, SpellingMode.Short)
                    val amountFontSize = calculateOptimalAmountFontSizeSp(
                        amountText = amount,
                        availableDiameterDp = availableDiameterDp,
                        resources = context.resources,
                    )
                    val suffixFontSize = calculateOptimalSuffixFontSizeSp(
                        suffixText = suffix,
                        availableWidthDp = (130f - 16f) * 0.55f,
                        resources = context.resources,
                    )

                    // 1. Number kept centered in the circle
                    Text(
                        text = amount,
                        color = primaryTextColor,
                        fontSize = amountFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )

                    // 2. Suffix placed in the empty space below the arc
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 2.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Text(
                            text = suffix,
                            color = secondaryTextColor,
                            fontSize = suffixFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RemainedWidget1x1Preview() {
    PreviewTheme {
        RemainedWidget1x1Preview(
            dataDisplayMode = DataDisplayMode.PERCENTAGE,
            themeMode = WidgetThemeMode.OPAQUE,
        )
    }
}
