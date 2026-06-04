package me.amirkazemzade.netwidget.ui.config

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.amirkazemzade.netwidget.BuildConfig
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.ui.config.components.DataDisplayModeCard
import me.amirkazemzade.netwidget.ui.config.components.SuffixSpellingModeCard
import me.amirkazemzade.netwidget.ui.config.components.WidgetConfigTopAppBar
import me.amirkazemzade.netwidget.ui.config.components.WidgetPreviewBox
import me.amirkazemzade.netwidget.ui.config.defaults.WidgetConfigDefaults
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WidgetConfigScreen(
    onUpdateWidget: () -> Unit,
    onNavigateBack: () -> Unit,
    showNavIcon: Boolean = false,
    viewModel: WidgetConfigViewModel = hiltViewModel(),
) {
    val configState by viewModel.currentConfigState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var remainedPercentage by remember { mutableFloatStateOf(0.67f) }

    val remained by remember {
        derivedStateOf {
            Remained(
                traffic = Traffic((remainedPercentage * 1024 * 10).roundToLong()),
                percentage = remainedPercentage
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is WidgetConfigEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                WidgetConfigEvent.Success -> {
                    onUpdateWidget()
                }
            }
        }
    }

    WidgetConfigScreenLayout(
        configState = configState,
        remained = remained,
        showNavIcon = showNavIcon,
        onNavigateBack = onNavigateBack,
        onSaveConfig = { viewModel.save() },
        onSelectDisplayMode = { displayMode: DataDisplayMode ->
            viewModel.updateDataDisplayMode(displayMode)
        },
        onSelectSpellingMode = { spellingMode: SpellingMode ->
            viewModel.updateSpellingMode(spellingMode)
        },
        onPercentageChanged = { percentage ->
            remainedPercentage = percentage
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun WidgetConfigScreenLayout(
    configState: WidgetConfigUiState,
    remained: Remained,
    showNavIcon: Boolean,
    onNavigateBack: () -> Unit,
    onSaveConfig: () -> Unit,
    onSelectDisplayMode: (DataDisplayMode) -> Unit,
    onSelectSpellingMode: (SpellingMode) -> Unit,
    onPercentageChanged: (Float) -> Unit,
) {
    Scaffold(
        topBar = {
            WidgetConfigTopAppBar(
                actionEnabled = !configState.isLoading && !configState.isSaving,
                showNavIcon = showNavIcon,
                onNavigateBack = onNavigateBack,
                onSaveConfig = onSaveConfig,
            )
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->

        val modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()

        if (configState.isLoading) {
            Box(
                modifier = modifier
            ) {
                LoadingIndicator()
            }
        } else {
            WidgetConfigContent(
                isSaving = configState.isSaving,
                dataDisplayMode = configState.remainedWidgetConfig.dataDisplayMode,
                spellingMode = configState.remainedWidgetConfig.spellingMode,
                remained = remained,
                onSelectDisplayMode = onSelectDisplayMode,
                onSelectSpellingMode = onSelectSpellingMode,
                onPercentageChanged = onPercentageChanged,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun WidgetConfigContent(
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    onSelectDisplayMode: (DataDisplayMode) -> Unit,
    onSelectSpellingMode: (SpellingMode) -> Unit,
    dataDisplayMode: DataDisplayMode = DataDisplayMode.PERCENTAGE,
    spellingMode: SpellingMode = SpellingMode.Short,
    remained: Remained = WidgetConfigDefaults.defaultRemainedValue,
    onPercentageChanged: (Float) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {

        Box(
            modifier = Modifier
                .height(4.dp)
        ) {
            if (isSaving) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        WidgetPreviewBox(
            remained = remained,
            selectedDataDisplayMode = dataDisplayMode,
            selectedSpellingMode = spellingMode,
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column {

                val modifier = Modifier.padding(16.dp)

                if (BuildConfig.DEBUG) {
                    Slider(
                        value = remained.percentage.coerceIn(0f, 1f),
                        onValueChange = onPercentageChanged,
                        modifier = modifier
                            .padding(horizontal = 16.dp),
                    )
                }

                DataDisplayModeCard(
                    enabled = !isSaving,
                    selectedMode = dataDisplayMode,
                    onSelectMode = onSelectDisplayMode,
                    modifier = modifier,
                )

                if (dataDisplayMode == DataDisplayMode.TRAFFIC) {
                    SuffixSpellingModeCard(
                        enabled = !isSaving,
                        selectedMode = spellingMode,
                        onSelectMode = onSelectSpellingMode,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

@PreviewDynamicColors
@Preview("Farsi", locale = "fa")
@Composable
private fun WidgetConfigScreenPreviewTheme() {
    PreviewTheme {
        WidgetConfigContent(
            dataDisplayMode = DataDisplayMode.TRAFFIC,
            onSelectDisplayMode = {},
            onSelectSpellingMode = {},
        )
    }
}