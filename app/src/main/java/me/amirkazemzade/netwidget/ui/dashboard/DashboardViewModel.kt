package me.amirkazemzade.netwidget.ui.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.domain.usecases.FetchRemainedUseCase
import me.amirkazemzade.netwidget.domain.usecases.LogoutUseCase
import me.amirkazemzade.netwidget.presentation.RemainedWidgetUpdateWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val fetchRemainedUseCase: FetchRemainedUseCase,
    private val remainedLocalDataSource: RemainedLocalDataSource,
) : ViewModel() {

    companion object {
        private const val AUTO_REFRESH_INTERVAL_MINUTES = 15L
    }

    private val _logoutState = MutableStateFlow<Status<Unit>>(Status.Idle)

    val logoutState = _logoutState.asStateFlow()
    private val _remainedState = MutableStateFlow<Status<Remained>>(Status.Idle)

    val remainedState: StateFlow<Status<Remained>> = _remainedState.asStateFlow()

    init {
        refreshRemained()
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(AUTO_REFRESH_INTERVAL_MINUTES.minutes)
                refreshRemained(showLoading = false)
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            logoutUseCase()
                .collect { newState ->
                    if (newState is RequestStatus.Success) {
                        clearWorkerSchedules(context)
                    }
                    _logoutState.update { newState }
                }
        }
    }

    fun refreshRemained(showLoading: Boolean = true) {
        viewModelScope.launch {
            fetchRemainedUseCase().collect { newState ->
                if (newState is RequestStatus.Loading && !showLoading) {
                    return@collect
                }
                _remainedState.update { newState }
                if (newState is RequestStatus.Success) {
                    updateWidgets(newState.data)
                }
            }
        }
    }

    fun scheduleWidgetUpdates(context: Context) {
        Log.i("DashboardViewModel", "Scheduling the worker")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<RemainedWidgetUpdateWorker>(
            repeatInterval = AUTO_REFRESH_INTERVAL_MINUTES,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()


        val operation = WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                uniqueWorkName = "remained_widget_update_worker",
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = workRequest
            )

        Log.i(
            "DashboardViewModel",
            "Scheduled the worker successfully. ${operation.result}, ${operation.state.value}"
        )
    }

    fun clearWorkerSchedules(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    private suspend fun updateWidgets(remained: Remained) {
        remainedLocalDataSource.setRemained(remained)
    }
}
