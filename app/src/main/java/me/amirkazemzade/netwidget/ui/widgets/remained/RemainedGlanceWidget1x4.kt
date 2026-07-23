package me.amirkazemzade.netwidget.ui.widgets.remained

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.NumberFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.flow.map
import me.amirkazemzade.netwidget.MainActivity
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.ui.utils.textFitsInContainer
import me.amirkazemzade.netwidget.ui.widgets.components.dynamicPercentagePadding
import kotlin.math.roundToLong

class RemainedGlanceWidget1x4 : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId: Int = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val dataSource = RemainedLocalDataSource(context)
        val dataDisplayModeFlow = dataSource.getDataDisplayMode(appWidgetId)
                .map { it ?: DataDisplayMode.PERCENTAGE }
        val spellingModeFlow = dataSource.getSpellingMode(appWidgetId)
            .map { it ?: SpellingMode.Short }

        provideContent {
            val remained by dataSource.remainedData.collectAsState(initial = null)
            val dataDisplayMode by dataDisplayModeFlow
                .collectAsState(initial = DataDisplayMode.PERCENTAGE)
            val spellingMode by spellingModeFlow
                .collectAsState(initial = SpellingMode.Short)

            val widgetSize = LocalSize.current

            GlanceTheme {
                Content(
                    widgetSize = widgetSize,
                    remained = remained,
                    dataDisplayMode = dataDisplayMode,
                    spellingMode = spellingMode,
                )
            }
        }
    }

    override suspend fun providePreview(
        context: Context,
        widgetCategory: Int,
    ) {
        provideContent {
            val remained = Remained(
                traffic = Traffic(7 * 1024),
                percentage = 0.67f,
            )
            val widgetSize = LocalSize.current
            val dataDisplayMode = DataDisplayMode.PERCENTAGE

            GlanceTheme {
                Content(
                    widgetSize = widgetSize,
                    remained = remained,
                    dataDisplayMode = dataDisplayMode,
                )
            }
        }

    }

    @Composable
    fun Content(
        widgetSize: DpSize,
        remained: Remained?,
        dataDisplayMode: DataDisplayMode = DataDisplayMode.PERCENTAGE,
        spellingMode: SpellingMode = SpellingMode.Short,
    ) {
        val context = LocalContext.current

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(
                    onClick = actionStartActivity(
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            when (remained) {
                null -> NotFoundContent()
                else -> PillContent(
                    widgetSize = widgetSize,
                    remained = remained,
                    dataDisplayMode = dataDisplayMode,
                    spellingMode = spellingMode,
                )
            }
        }
    }

    @Composable
    private fun NotFoundContent() {
        val context = LocalContext.current
        Text(
            text = context.getString(R.string.no_content_available),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary,
            )
        )

    }

    @SuppressLint("LocalContextResourcesRead")
    @Composable
    private fun PillContent(
        widgetSize: DpSize,
        remained: Remained,
        dataDisplayMode: DataDisplayMode,
        spellingMode: SpellingMode,
    ) {
        val context = LocalContext.current

        val breakPoint = 0.33f
        val isLow = remained.percentage < breakPoint

        val pillWidth = (widgetSize.width - 24.dp) * remained.percentage

        val textPadding = dynamicPercentagePadding(
            remained.percentage,
            maxPadding = 16.dp,
            minPadding = 4.dp
        )

        val dataInfoText = when (dataDisplayMode) {
            DataDisplayMode.PERCENTAGE -> remained.percentage.toReadablePercentText()

            DataDisplayMode.TRAFFIC -> remained.traffic.toReadableTrafficText(
                spellingMode = spellingMode
            )
        }

        val fontSize = when (dataDisplayMode) {
            DataDisplayMode.PERCENTAGE -> 36.sp
            DataDisplayMode.TRAFFIC if (spellingMode == SpellingMode.Short) -> 24.sp
            DataDisplayMode.TRAFFIC -> 20.sp
        }

        val textFits = textFitsInContainer(
            text = dataInfoText,
            textSizeSp = fontSize.value + 1,
            containerWidthDp = (pillWidth - 24.dp).value,
            resources = context.resources
        )

        val pillColor =
            if (isLow) GlanceTheme.colors.error
            else GlanceTheme.colors.primary

        val textStyle = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )

        val showTextInside = !isLow && (remained.percentage >= 0.5f || textFits)

        PillUi(
            pillWidth = pillWidth,
            pillColor = pillColor,
            showTextInside = showTextInside,
            textPadding = textPadding,
            dataInfoText = dataInfoText,
            textStyle = textStyle
        )
    }

    @Composable
    private fun PillUi(
        pillWidth: Dp,
        pillColor: ColorProvider,
        showTextInside: Boolean,
        textPadding: Dp,
        dataInfoText: String,
        textStyle: TextStyle,
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surfaceVariant)
                .cornerRadius(16.dp)
        ) {

            Box(
                modifier = GlanceModifier
                    .width(max(pillWidth, 2.dp))
                    .fillMaxHeight()
                    .background(pillColor)
                    .cornerRadius(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {

                if (showTextInside) {
                    Text(
                        modifier = GlanceModifier.padding(end = textPadding),
                        text = dataInfoText,
                        maxLines = 1,
                        style = textStyle.copy(
                            color = GlanceTheme.colors.onPrimary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            if (!showTextInside) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .padding(start = pillWidth + textPadding),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = dataInfoText,
                        style = textStyle.copy(color = pillColor)
                    )
                }
            }
        }
    }
}

@SuppressLint("LocalContextConfigurationRead")
@Composable
private fun Traffic.toReadableTrafficText(
    spellingMode: SpellingMode = SpellingMode.Short,
): String {
    val context = LocalContext.current
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

    return "$amountFormatted $suffix"
}

@SuppressLint("LocalContextConfigurationRead")
@Composable
private fun Float.toReadablePercentText(): String {
    val context = LocalContext.current
    val currentLocale = context.resources.configuration.locales[0]

    val percentFormatter = NumberFormat.getPercentInstance(currentLocale).apply {
        maximumFractionDigits = 0
    }
    return percentFormatter.format(this)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreviewNoContent() {
    GlanceTheme {

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = null,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview100Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(10 * 1024),
            percentage = 1f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview100PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(10 * 1024),
            percentage = 1f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview80Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(8 * 1024),
            percentage = 0.8f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview80PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(8 * 1024),
            percentage = 0.8f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview60Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(6 * 1024),
            percentage = 0.6f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview60PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(6 * 1024),
            percentage = 0.6f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview50Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(5 * 1024),
            percentage = 0.5f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview50PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(5 * 1024),
            percentage = 0.5f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview40Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(4 * 1024),
            percentage = 0.4f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview40PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(4 * 1024),
            percentage = 0.4f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview33Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic((3.3 * 1024).roundToLong()),
            percentage = 0.33f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview33PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic((3.3 * 1024).roundToLong()),
            percentage = 0.33f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview32Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic((3.2 * 1024).roundToLong()),
            percentage = 0.32f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview32PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic((3.2 * 1024).roundToLong()),
            percentage = 0.32f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview20Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(2 * 1024),
            percentage = 0.2f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview20PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(2 * 1024),
            percentage = 0.2f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview9PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(905),
            percentage = 0.09f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview0Percent() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(0 * 1024),
            percentage = 0.0f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidgetPreview0PercentTraffic() {
    GlanceTheme {
        val remained = Remained(
            traffic = Traffic(0),
            percentage = 0.0f,
        )

        val widgetSize = DpSize(280.dp, 80.dp)

        Box(
            modifier = GlanceModifier.size(
                width = widgetSize.width,
                height = widgetSize.height,
            )
        ) {
            RemainedGlanceWidget1x4().Content(
                widgetSize = widgetSize,
                remained = remained,
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}
