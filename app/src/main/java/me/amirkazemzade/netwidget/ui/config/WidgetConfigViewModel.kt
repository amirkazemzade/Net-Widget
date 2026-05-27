package me.amirkazemzade.netwidget.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import me.amirkazemzade.netwidget.domain.usecases.GetRemainedWidgetConfigUseCase
import me.amirkazemzade.netwidget.domain.usecases.SetRemainedWidgetConfigUseCase
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    getRemainedWidgetConfigUseCase: GetRemainedWidgetConfigUseCase,
    private val setRemainedWidgetConfigUseCase: SetRemainedWidgetConfigUseCase,
) : ViewModel() {

    private val _event = MutableSharedFlow<WidgetConfigEvent>()
    val event = _event.asSharedFlow()

    private val _currentConfigState = MutableStateFlow(WidgetConfigUiState())
    val currentConfigState = _currentConfigState.asStateFlow()

    init {
        viewModelScope.launch {
            getRemainedWidgetConfigUseCase()
                .collect { config ->
                    _currentConfigState.update {
                        it.copy(
                            isLoading = false,
                            remainedWidgetConfig = config
                        )
                    }
                }
        }
    }

    fun updateDataDisplayMode(
        dataDisplayMode: DataDisplayMode,
    ) {
        _currentConfigState.update { widgetConfigUiState ->
            widgetConfigUiState.copy(
                remainedWidgetConfig = widgetConfigUiState.remainedWidgetConfig.copy(
                    dataDisplayMode = dataDisplayMode
                )
            )
        }
    }

    fun updateSpellingMode(spellingMode: SpellingMode) {
        _currentConfigState.update { widgetConfigUiState ->
            widgetConfigUiState.copy(
                remainedWidgetConfig = widgetConfigUiState.remainedWidgetConfig.copy(
                    spellingMode = spellingMode
                )
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            setRemainedWidgetConfigUseCase(
                config = currentConfigState.value.remainedWidgetConfig
            ).collect { newStatus ->
                when (newStatus) {
                    RequestStatus.Loading -> {
                        _currentConfigState.update {
                            it.copy(
                                isSaving = true,
                            )
                        }

                    }

                    is RequestStatus.Error -> {
                        _event.emit(WidgetConfigEvent.Error(newStatus.message))
                        _currentConfigState.update {
                            it.copy(isSaving = false)
                        }
                    }

                    is RequestStatus.Success<*> -> {
                        _event.emit(WidgetConfigEvent.Success)
                        _currentConfigState.update {
                            it.copy(isSaving = false)
                        }

                    }
                }
            }
        }
    }
}