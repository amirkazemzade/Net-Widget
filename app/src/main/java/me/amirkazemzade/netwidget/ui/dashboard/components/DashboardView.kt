package me.amirkazemzade.netwidget.ui.dashboard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
fun DashboardView(
    remainedState: Status<Remained>,
    onLogout: () -> Unit,
    isLoggingOut: Boolean = false,
    onRefreshRemained: () -> Unit = {},
) {
    Scaffold(
        topBar = { DashboardTopAppBar(isLoggingOut, onLogout) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            when (remainedState) {
                is RequestStatus.Error -> ErrorText(
                    remainedState.message,
                    onRetry = onRefreshRemained
                )

                is RequestStatus.Success<Remained> -> DashboardContent(
                    remainedState.data,
                    onReloadClicked = onRefreshRemained
                )

                else -> DashboardContent(
                    null,
                    onReloadClicked = onRefreshRemained
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun DashboardScreenPreviewTheme() {
    var isLoggingOut by remember { mutableStateOf(false) }

    PreviewTheme {
        DashboardView(
            remainedState = remainedMockSuccessResponse,
            isLoggingOut = isLoggingOut,
            onLogout = { isLoggingOut = true }
        )
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenLoggingOutPreviewTheme() {
    PreviewTheme {
        DashboardView(
            remainedState = remainedMockSuccessResponse,
            isLoggingOut = true,
            onLogout = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenErrorPreviewTheme() {
    PreviewTheme {
        DashboardView(
            remainedState = RequestStatus.Error("Error Message"),
            isLoggingOut = false,
            onLogout = {}
        )
    }
}

val remainedMockSuccessResponse: RequestStatus.Success<Remained>
    get() {
        val amount = 15460L
        val totalAmount = 20000L
        return RequestStatus.Success(
            Remained(Traffic(amount), percentage = amount * 1f / totalAmount)
        )
    }
