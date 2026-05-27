package me.amirkazemzade.netwidget.ui.config.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> CardRadioButton(
    value: T,
    selectedValue: T,
    enabled: Boolean,
    label: String,
    onSelect: (T) -> Unit,
    modifier: Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                onSelect(value)
            }
            .padding(horizontal = 8.dp)
    ) {
        RadioButton(
            enabled = enabled,
            selected = selectedValue == value,
            onClick = { onSelect(value) },
        )
        Text(
            text = label
        )
    }
}