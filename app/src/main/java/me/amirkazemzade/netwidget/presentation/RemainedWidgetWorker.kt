package me.amirkazemzade.netwidget.presentation

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.usecases.FetchRemainedUseCase
import me.amirkazemzade.netwidget.ui.widgets.remained.updateAllRemainedWidgets
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

private const val TAG = "RemainedWidgetUpdateWorker"

class RemainedWidgetUpdateWorker(
    private val context: Context,
    params: WorkerParameters,
    private val fetchRemainedUseCase: FetchRemainedUseCase,
    private val remainedLocalDataSource: RemainedLocalDataSource,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {

            Log.d(TAG, "Worker started - Run attempt: $runAttemptCount")

            val state = withTimeout(5.minutes) { // 30 second timeout
                fetchRemainedUseCase()
                    .filterNot { it is RequestStatus.Loading }
                    .first()
            }

            Log.d(TAG, "Fetched state: $state")

            when (state) {
                is RequestStatus.Success<Remained> -> {
                    remainedLocalDataSource.setRemained(state.data)

                    updateAllRemainedWidgets(context)

                    Result.success()
                }

                else -> Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Fetch Failed", e)
            Result.retry()
        }
    }
}

class RemainedWorkerFactory @Inject constructor(
    private val fetchRemainedUseCase: FetchRemainedUseCase,
    private val remainedLocalDataSource: RemainedLocalDataSource,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            RemainedWidgetUpdateWorker::class.java.name ->
                RemainedWidgetUpdateWorker(
                    context = appContext,
                    params = workerParameters,
                    fetchRemainedUseCase = fetchRemainedUseCase,
                    remainedLocalDataSource = remainedLocalDataSource,
                )

            else -> null
        }
    }
}
