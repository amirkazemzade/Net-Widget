package me.amirkazemzade.netwidget.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SplashScreen() {
    Scaffold {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ContainedLoadingIndicator()
        }
    }
}

@Preview
@Composable
private fun PreviewThemeSplashScreen() {
    PreviewTheme {
        SplashScreen()
    }
}
