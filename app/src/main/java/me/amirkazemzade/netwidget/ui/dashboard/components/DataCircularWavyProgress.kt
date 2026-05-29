package me.amirkazemzade.netwidget.ui.dashboard.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions
import me.amirkazemzade.netwidget.ui.theme.primaryMuteColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DataCircularWavyProgress(
    remained: Remained?,
    modifier: Modifier = Modifier,
) {
    val waveSpeed = 16.dp
    val modifier = modifier.size(100.dp)
    val trackStroke = Stroke(
        NetWidgetDimensions.medium.value,
        cap = StrokeCap.Companion.Round
    )
    val stroke = Stroke(
        NetWidgetDimensions.medium.value + 2.dp.value,
        cap = StrokeCap.Companion.Round
    )
    val color = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primaryMuteColor
    val wavelength = 32.dp
    if (remained == null) {
        CircularWavyProgressIndicator(
            modifier = modifier,
            waveSpeed = waveSpeed,
            stroke = stroke,
            trackStroke = trackStroke,
            color = color,
            trackColor = trackColor,
            wavelength = wavelength,
        )
    } else {
        CircularWavyProgressIndicator(
            modifier = modifier,
            progress = { -> remained.percentage },
            waveSpeed = waveSpeed,
            stroke = stroke,
            trackStroke = trackStroke,
            color = color,
            trackColor = trackColor,
            wavelength = wavelength,
        )
    }
}