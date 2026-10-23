package me.amirkazemzade.netwidget.domain.models

data class RemainedWidgetConfig(
    val dataDisplayMode: DataDisplayMode,
    val spellingMode: SpellingMode,
    val themeMode: WidgetThemeMode = WidgetThemeMode.OPAQUE,
)
