package me.amirkazemzade.netwidget.ui.config.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.ui.config.preview.RemainedWidgetPreview

@Composable
fun WidgetPreviewBox(
    remained: Remained,
    selectedDataDisplayMode: DataDisplayMode,
    selectedSpellingMode: SpellingMode,
) {
    Box(
        modifier = Modifier.padding(horizontal = 32.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                )
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.preview),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }

        RemainedWidgetPreview(
            remained = remained,
            dataDisplayMode = selectedDataDisplayMode,
            spellingMode = selectedSpellingMode,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 64.dp)
                .fillMaxWidth()
        )
    }
}