package me.amirkazemzade.netwidget.ui.config.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.SpellingMode

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun SuffixSpellingModeCard(
    selectedMode: SpellingMode,
    onSelectMode: (SpellingMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = stringResource(R.string.suffix_display_mode),
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.padding(16.dp)
        )
        SuffixSpellingModeRadioButton(
            enabled = enabled,
            label = stringResource(R.string.short_text),
            spellingMode = SpellingMode.Short,
            selectedMode = selectedMode,
            onSelect = onSelectMode,
        )
        SuffixSpellingModeRadioButton(
            enabled = enabled,
            label = stringResource(R.string.full),
            spellingMode = SpellingMode.Full,
            selectedMode = selectedMode,
            onSelect = onSelectMode,
        )
    }
}