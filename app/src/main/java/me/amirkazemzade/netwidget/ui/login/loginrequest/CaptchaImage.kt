package me.amirkazemzade.netwidget.ui.login.loginrequest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.CaptchaBase64
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.ui.login.toImageBitmap

@Composable
fun CaptchaImage(
    state: Status<CaptchaBase64>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        RequestStatus.Loading, Status.Idle -> Box(
            modifier = modifier
                .shimmer()
                .background(
                    color = Color.DarkGray,
                    shape = MaterialTheme.shapes.large
                )
        )

        is RequestStatus.Error -> Text(
            text = stringResource(R.string.error),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
                .padding(16.dp)
        )


        is RequestStatus.Success<CaptchaBase64> -> {
            val imageColorFilter = if (isSystemInDarkTheme())
                remember {
                    ColorFilter.colorMatrix(
                        ColorMatrix(
                            floatArrayOf(
                                -1f, 0f, 0f, 0f, 255f, // Flip Red and shift to max
                                0f, -1f, 0f, 0f, 255f, // Flip Green and shift to max
                                0f, 0f, -1f, 0f, 255f, // Flip Blue and shift to max
                                0f, 0f, 0f, 1f, 0f  // Keep Alpha exactly as it is
                            )
                        )
                    )
                }
            else null

            Image(
                bitmap = state.data.value.toImageBitmap(),
                contentDescription = stringResource(R.string.captcha),
                colorFilter = imageColorFilter,
                modifier = modifier,
            )
        }
    }
}
