package me.amirkazemzade.netwidget.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.amirkazemzade.netwidget.data.datasource.RemainedLocalDataSource
import me.amirkazemzade.netwidget.domain.models.DataDisplayMode
import me.amirkazemzade.netwidget.domain.models.RemainedWidgetConfig
import me.amirkazemzade.netwidget.domain.models.SpellingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRemainedWidgetConfigUseCase @Inject constructor(
    private val remainedLocalDataSource: RemainedLocalDataSource,
) {
    operator fun invoke(): Flow<RemainedWidgetConfig> {
        return combine(
            flow = remainedLocalDataSource.dataDisplayMode,
            flow2 = remainedLocalDataSource.spellingMode,
        ) { displayMode, spellingMode ->
            RemainedWidgetConfig(
                dataDisplayMode = displayMode ?: DataDisplayMode.PERCENTAGE,
                spellingMode = spellingMode ?: SpellingMode.Short
            )
        }
    }
}