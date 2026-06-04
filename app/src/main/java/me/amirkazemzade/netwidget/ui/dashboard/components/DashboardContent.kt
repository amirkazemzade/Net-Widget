package me.amirkazemzade.netwidget.ui.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardContent(
    remained: Remained?,
    onReloadClicked: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = NetWidgetDimensions.medium, horizontal = NetWidgetDimensions.large)
    ) {
        RemainedCard(remained, onReloadClicked)
    }
}

@PreviewLightDark
@Composable
private fun DashboardContentPreviewTheme() {
    PreviewTheme {
        DashboardContent(remainedMockValue)
    }
}

@PreviewLightDark
@Composable
private fun DashboardContentWithMBPreviewTheme() {
    PreviewTheme {
        DashboardContent(remainedMockLessThan1GValue)
    }
}

@PreviewLightDark
@Composable
private fun DashboardContentLoadingPreviewTheme() {
    PreviewTheme {
        DashboardContent(null)
    }
}

val remainedMockValue: Remained
    get() {
        val amount = 15460L
        val totalAmount = 20000L
        return Remained(Traffic(amount), percentage = amount * 1f / totalAmount)
    }

val remainedMockLessThan1GValue: Remained
    get() {
        val amount = 999L
        val totalAmount = 20000L
        return Remained(Traffic(amount), percentage = amount * 1f / totalAmount)
    }
