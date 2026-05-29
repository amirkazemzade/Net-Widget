package me.amirkazemzade.netwidget.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.Remained
import me.amirkazemzade.netwidget.domain.models.Traffic
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme
import me.amirkazemzade.netwidget.ui.theme.primaryMuteColor

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun DataCircularProgress(
    remained: Remained?,
) {
    val isLoading = remained == null
    Box(
        contentAlignment = Alignment.Center,
    ) {
        DataCircularWavyProgress(remained)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val (amount, unit) = formatRemained(
                remained,
                gigabyteUnit = stringResource(R.string.gigabyte_short),
                megabyteUnit = stringResource(R.string.megabyte_short),
            )
            Text(
                amount,
                style = MaterialTheme.typography.titleMediumEmphasized,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .whatIfMap(
                        isLoading,
                    ) {
                        it
                            .shimmer()
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryMuteColor,
                                shape = MaterialTheme.shapes.small,
                            )
                            .width(52.dp)
                    }
            )
            Text(
                unit,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.W400,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .whatIfMap(isLoading) {
                        it
                            .shimmer()
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryMuteColor,
                                shape = RoundedCornerShape(6.0.dp),
                            )
                            .width(32.dp)
                    }
            )
        }
    }
}


private fun formatRemained(
    remained: Remained?,
    gigabyteUnit: String,
    megabyteUnit: String,
): Pair<String, String> =
    if (remained == null) "   " to "  "
    else if (remained.traffic.amountInMb > 1024) "%.2f".format(remained.traffic.toGB()) to gigabyteUnit
    else remained.traffic.toMB().toString() to megabyteUnit

@PreviewLightDark
@PreviewDynamicColors
@Composable
fun DataCircularProgressPreview() {
    val remained = Remained(traffic = Traffic(amountInMb = 2048), percentage = 0.5f)
    NetWidgetAppTheme {
        Surface {
            Column {
                DataCircularProgress(remained)
                Box(modifier = Modifier.height(4.dp))
                Card {
                    DataCircularProgress(remained)
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewDynamicColors
@Composable
fun DataCircularProgressLoadingPreview() {
    NetWidgetAppTheme {
        Surface {
            Column {
                DataCircularProgress(null)
                Box(modifier = Modifier.height(4.dp))
                Card {
                    DataCircularProgress(null)
                }
            }
        }
    }
}

