package me.amirkazemzade.netwidget.ui.widgets.remained

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.coroutines.flow.map
import me.amirkazemzade.netwidget.MainActivity
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.domain.models.WidgetThemeMode
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalAmountFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalPercentageFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.calculateOptimalSuffixFontSizeSp
import me.amirkazemzade.netwidget.ui.utils.toReadablePercentText
import me.amirkazemzade.netwidget.ui.utils.toReadableTrafficParts

class RemainedGlanceWidget1x1 : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId: Int = GlanceAppWidgetManager(context).getAppWidgetId(id)

        val dataSource = RemainedLocalDataSource(context)
        val dataDisplayModeFlow = dataSource.getDataDisplayMode(appWidgetId)
            .map { it ?: DataDisplayMode.PERCENTAGE }

        provideContent {
            val remained by dataSource.remainedData.collectAsState(initial = null)
            val dataDisplayMode by dataDisplayModeFlow
                .collectAsState(initial = DataDisplayMode.PERCENTAGE)

            val widgetSize = LocalSize.current

            GlanceTheme {
                Content(
                    widgetSize = widgetSize,
                    remained = remained,
                    dataDisplayMode = dataDisplayMode,
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
            GlanceTheme {
                Content(
                    widgetSize = widgetSize,
                    remained = remained,
                    dataDisplayMode = DataDisplayMode.PERCENTAGE,
                )
            }
        }
    }

    @Composable
    fun Content(
        widgetSize: DpSize = LocalSize.current,
        remained: Remained?,
        dataDisplayMode: DataDisplayMode = DataDisplayMode.PERCENTAGE,
        spellingMode: SpellingMode = SpellingMode.Short,
        themeMode: WidgetThemeMode = WidgetThemeMode.OPAQUE,
    ) {
        val context = LocalContext.current

        // Maintain square aspect ratio: the launcher cell may not be square (e.g. 70dp x 95dp),
        // so we take the minimum dimension to ensure a 1:1 aspect ratio square card.
        val cellWidth = if (widgetSize.width.isSpecified && widgetSize.width > 0.dp) widgetSize.width else 80.dp
        val cellHeight = if (widgetSize.height.isSpecified && widgetSize.height > 0.dp) widgetSize.height else 80.dp
        val squareSize = minOf(cellWidth, cellHeight)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val cornerRadius = minOf(24.dp, squareSize * 0.28f)
        val innerPadding = if (squareSize < 75.dp) 4.dp else 8.dp
        val gaugeSize = squareSize - (innerPadding * 2)
        // Safe inner circle diameter inside the gauge arc
        val availableDiameterDp = gaugeSize.value * 0.60f

        val squareContainerModifier = GlanceModifier
            .size(squareSize)
            .cornerRadius(cornerRadius)
            .background(GlanceTheme.colors.widgetBackground)
            .padding(innerPadding)

        // The outer container fills the OS-provided cell and centers the square card within it.
        // Both containers are clickable so the entire cell responds to clicks, while the inner
        // card maintains its rounded ripple and strictly square 1:1 aspect ratio.
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(
                    onClick = actionStartActivity(intent),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = squareContainerModifier.clickable(
                    onClick = actionStartActivity(intent),
                ),
                contentAlignment = Alignment.Center,
            ) {
                when (remained) {
                    null -> NotFoundContent(availableDiameterDp = availableDiameterDp)
                    else -> GaugeContent(
                        remained = remained,
                        dataDisplayMode = dataDisplayMode,
                        availableDiameterDp = availableDiameterDp,
                        gaugeSize = gaugeSize,
                    )
                }
            }
        }
    }

    @Composable
    private fun NotFoundContent(
        availableDiameterDp: Float,
    ) {
        val context = LocalContext.current
        val text = context.getString(R.string.no_content_available)
        val fontSizeSp = calculateOptimalPercentageFontSizeSp(
            text = text,
            availableDiameterDp = availableDiameterDp,
            resources = context.resources,
            maxFontSizeSp = 13f,
            minFontSizeSp = 8f,
        )

        Text(
            text = text,
            maxLines = 2,
            style = TextStyle(
                fontSize = fontSizeSp.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary,
                textAlign = TextAlign.Center,
            )
        )
    }

    @Composable
    private fun GaugeContent(
        remained: Remained,
        dataDisplayMode: DataDisplayMode,
        availableDiameterDp: Float,
        gaugeSize: Dp,
    ) {
        val context = LocalContext.current
        val isLow = remained.percentage < 0.33f

        val primaryColor = GlanceTheme.colors.primary.getColor(context).toArgb()
        val errorColor = GlanceTheme.colors.error.getColor(context).toArgb()
        val trackColor = GlanceTheme.colors.surfaceVariant.getColor(context).toArgb()

        val gaugeBitmap = CircularGaugeDrawer.drawGaugeBitmap(
            percentage = remained.percentage,
            isLow = isLow,
            themeMode = WidgetThemeMode.OPAQUE,
            sizePx = 320,
            primaryColor = primaryColor,
            errorColor = errorColor,
            trackColor = trackColor,
        )

        val primaryTextColor = if (isLow) GlanceTheme.colors.error else GlanceTheme.colors.onSurface
        val secondaryTextColor = GlanceTheme.colors.onSurfaceVariant

        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(gaugeBitmap),
                contentDescription = context.getString(R.string.remained_internet),
                modifier = GlanceModifier.fillMaxSize(),
            )

            when (dataDisplayMode) {
                DataDisplayMode.PERCENTAGE -> {
                    val percentText = remained.percentage.toReadablePercentText(context)
                    val fontSizeSp = calculateOptimalPercentageFontSizeSp(
                        text = percentText,
                        availableDiameterDp = availableDiameterDp,
                        resources = context.resources,
                    )
                    Text(
                        text = percentText,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = fontSizeSp.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center,
                        )
                    )
                }

                DataDisplayMode.TRAFFIC -> {
                    // For the 1x1 widget, always use short text (e.g. GB, MB) as there is no space for full text.
                    // Keep the number centered in the circle, and place the suffix in the empty space below the arc.
                    val (amount, suffix) = remained.traffic.toReadableTrafficParts(context, SpellingMode.Short)

                    val amountFontSizeSp = calculateOptimalAmountFontSizeSp(
                        amountText = amount,
                        availableDiameterDp = availableDiameterDp,
                        resources = context.resources,
                    )

                    val gapWidthDp = gaugeSize.value * 0.55f
                    val suffixFontSizeSp = calculateOptimalSuffixFontSizeSp(
                        suffixText = suffix,
                        availableWidthDp = gapWidthDp,
                        resources = context.resources,
                    )

                    // 1. Number kept centered in the circle
                    Text(
                        text = amount,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = amountFontSizeSp.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center,
                        )
                    )

                    // 2. Suffix placed in the empty space below the arc
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(bottom = 2.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Text(
                            text = suffix,
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = suffixFontSizeSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryTextColor,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidget1x1PreviewSquareOpaque() {
    GlanceTheme {
        val widgetSize = DpSize(80.dp, 80.dp)
        Box(modifier = GlanceModifier.size(width = widgetSize.width, height = widgetSize.height)) {
            RemainedGlanceWidget1x1().Content(
                widgetSize = widgetSize,
                remained = Remained(
                    traffic = Traffic(7 * 1024),
                    percentage = 0.67f,
                ),
                dataDisplayMode = DataDisplayMode.PERCENTAGE,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidget1x1PreviewNonSquareTall() {
    GlanceTheme {
        // Simulating a tall non-square 1x1 OS cell (e.g. 70dp x 95dp)
        val widgetSize = DpSize(70.dp, 95.dp)
        Box(modifier = GlanceModifier.size(width = widgetSize.width, height = widgetSize.height)) {
            RemainedGlanceWidget1x1().Content(
                widgetSize = widgetSize,
                remained = Remained(
                    traffic = Traffic(7 * 1024),
                    percentage = 0.67f,
                ),
                dataDisplayMode = DataDisplayMode.PERCENTAGE,
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun RemainedGlanceWidget1x1PreviewNonSquareWide() {
    GlanceTheme {
        // Simulating a wide non-square 1x1 OS cell (e.g. 100dp x 75dp)
        val widgetSize = DpSize(100.dp, 75.dp)
        Box(modifier = GlanceModifier.size(width = widgetSize.width, height = widgetSize.height)) {
            RemainedGlanceWidget1x1().Content(
                widgetSize = widgetSize,
                remained = Remained(
                    traffic = Traffic(5 * 1024),
                    percentage = 0.5f,
                ),
                dataDisplayMode = DataDisplayMode.TRAFFIC,
            )
        }
    }
}
