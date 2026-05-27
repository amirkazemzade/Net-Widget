package me.amirkazemzade.netwidget.ui.config

import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.RemainedWidgetConfig
import me.amirkazemzade.netwidget.domain.models.SpellingMode

data class WidgetConfigUiState(
    val isLoading: Boolean = true,
    val remainedWidgetConfig: RemainedWidgetConfig = RemainedWidgetConfig(
        dataDisplayMode = DataDisplayMode.PERCENTAGE,
        spellingMode = SpellingMode.Short,
    ),
    val isSaving: Boolean = false,
    val error: String? = null,
)
