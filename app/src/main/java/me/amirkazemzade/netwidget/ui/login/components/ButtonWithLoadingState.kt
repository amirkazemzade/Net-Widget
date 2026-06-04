package me.amirkazemzade.netwidget.ui.login.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ButtonWithLoadingState(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = ButtonDefaults.shape,
    content: @Composable () -> Unit,
) {
    Button(
        enabled = !isLoading && enabled,
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = if (isLoading) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified,
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.alpha(if (isLoading) 1f else 0f)
            )
            Box(modifier = Modifier.alpha(if (isLoading) 0f else 1f)) {
                content()
            }
        }
    }
}


@Preview(name = "Normal State", showBackground = true)
@Composable
fun ButtonWithLoadingStatePreviewTheme() {
    PreviewTheme {
        ButtonWithLoadingState(onClick = {}, isLoading = false) {
            Text(stringResource(R.string.continue_action))
        }
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun ButtonWithLoadingStateLoadingPreviewTheme() {
    PreviewTheme {
        ButtonWithLoadingState(onClick = {}, isLoading = true) {
            Text(stringResource(R.string.continue_action))
        }
    }
}

@Preview(name = "Interactive", showBackground = true)
@Composable
fun ButtonWithLoadingStateInteractivePreviewTheme() {

    val infiniteTransition = rememberInfiniteTransition("infiniteTransition")
    val animationState by infiniteTransition.animateFloat(
        0f,
        1f,
        animationSpec = infiniteRepeatable(animation = tween(5000), repeatMode = RepeatMode.Reverse)
    )

    PreviewTheme {
        ButtonWithLoadingState(
            onClick = {},
            isLoading = animationState >= 0.5f,
        ) {
            Text(stringResource(R.string.continue_action))
        }
    }
}
