package me.amirkazemzade.netwidget.ui.login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FieldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    ButtonWithLoadingState(
        enabled = !isLoading && enabled,
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .height(NetWidgetDimensions.fieldHeight)
            .fillMaxWidth()
    ) {
        if (isLoading) {
            LoadingIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
        } else {
            Text(text)
        }
    }
}

@Preview
@Composable
private fun PreviewFieldButton() {
    var isLoading by remember { mutableStateOf(false) }
    NetWidgetAppTheme {
        FieldButton(
            text = stringResource(R.string.continue_action), isLoading = isLoading, onClick = { isLoading = true })
    }
}

@Preview
@Composable
private fun PreviewFieldButtonDisabled() {
    NetWidgetAppTheme {
        FieldButton(
            enabled = false, text = stringResource(R.string.continue_action), onClick = { })
    }
}
