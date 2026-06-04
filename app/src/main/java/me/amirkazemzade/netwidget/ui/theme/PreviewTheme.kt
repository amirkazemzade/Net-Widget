package me.amirkazemzade.netwidget.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun PreviewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NetWidgetAppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@Composable
fun PreviewThemeScaffold(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    NetWidgetAppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor
    ) {
        Scaffold { padding ->
            content(padding)
        }
    }
}