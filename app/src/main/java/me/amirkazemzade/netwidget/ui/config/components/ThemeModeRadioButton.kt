package me.amirkazemzade.netwidget.ui.config.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amirkazemzade.netwidget.domain.models.WidgetThemeMode

@Composable
fun ThemeModeRadioButton(
    label: String,
    themeMode: WidgetThemeMode,
    selectedThemeMode: WidgetThemeMode,
    onSelect: (WidgetThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    CardRadioButton(
        modifier = modifier,
        enabled = enabled,
        onSelect = onSelect,
        value = themeMode,
        selectedValue = selectedThemeMode,
        label = label,
    )
}
