package me.amirkazemzade.netwidget.ui.config.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amirkazemzade.netwidget.domain.models.SpellingMode

@Composable
fun SuffixSpellingModeRadioButton(
    label: String,
    spellingMode: SpellingMode,
    selectedMode: SpellingMode,
    onSelect: (SpellingMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    CardRadioButton(
        modifier = modifier,
        enabled = enabled,
        onSelect = onSelect,
        value = spellingMode,
        selectedValue = selectedMode,
        label = label
    )
}

