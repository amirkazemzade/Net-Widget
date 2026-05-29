package me.amirkazemzade.netwidget.ui.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions

@Composable
fun FieldColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .width(NetWidgetDimensions.fieldWidth),
        content = content,
    )
}