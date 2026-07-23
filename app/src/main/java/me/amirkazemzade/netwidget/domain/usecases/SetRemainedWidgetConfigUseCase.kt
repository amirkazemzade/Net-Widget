package me.amirkazemzade.netwidget.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.RemainedWidgetConfig
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetRemainedWidgetConfigUseCase @Inject constructor(
    private val remainedLocalDataSource: RemainedLocalDataSource,
) {
    operator fun invoke(
        widgetId: Int,
        config: RemainedWidgetConfig,
    ): Flow<RequestStatus<Unit>> = flow {
        emit(RequestStatus.Loading)

        try {
            remainedLocalDataSource.setDataDisplayMode(
                widgetId = widgetId,
                mode = config.dataDisplayMode
            )
            remainedLocalDataSource.setSpellingMode(
                widgetId = widgetId,
                mode = config.spellingMode
            )
            emit(RequestStatus.Success(Unit))
        } catch (e: Exception) {
            emit(RequestStatus.Error(message = e.localizedMessage ?: "Something went wrong"))

        }
    }
}