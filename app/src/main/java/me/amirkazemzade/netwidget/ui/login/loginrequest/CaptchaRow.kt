package me.amirkazemzade.netwidget.ui.login.loginrequest

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.CaptchaBase64
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme

@Composable
fun CaptchaRow(
    captchaState: Status<CaptchaBase64>,
    onFetchCaptcha: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val fillColor = if (isSystemInDarkTheme()) Color(0xff101010) else Color(0xffeeeeee)
        CaptchaImage(
            captchaState,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .background(fillColor, MaterialTheme.shapes.large)
        )
        Box(modifier = Modifier.width(8.dp))
        FilledTonalIconButton(
            enabled = captchaState !is RequestStatus.Loading,
            onClick = onFetchCaptcha,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .size(52.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_refresh_24),
                contentDescription = stringResource(R.string.refresh),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "Farsi", locale = "fa")
@Composable
fun CaptchaRowIdlePreview() {
    NetWidgetAppTheme {
        Surface {
            CaptchaRow(
                captchaState = Status.Idle,
                onFetchCaptcha = {}
            )
        }
    }
}


@PreviewLightDark
@Preview(name = "Farsi", locale = "fa")
@Composable
fun CaptchaRowLoadingPreview() {
    NetWidgetAppTheme {
        Surface {
            CaptchaRow(
                captchaState = RequestStatus.Loading,
                onFetchCaptcha = {}
            )
        }
    }
}


@PreviewLightDark
@Preview(name = "Farsi", locale = "fa")
@Composable
fun CaptchaRowSuccessPreview() {
    NetWidgetAppTheme {
        Surface {
            CaptchaRow(
                // Red dot 1x1 pixel base64
                captchaState = RequestStatus.Success(
                    CaptchaBase64(
                        """
                             iVBORw0KGgoAAAANSUhEUgAAAFAAAABBCAIAAADJ+bTEAAABZklEQVR4nO2ZUQ6DIAyGddl9uP8xehKvsAcT4xBKqQjlt9/TkiH2o1JA123bljfxGR1Ab1wYHRdGx4XRcWF0XBgdF0bHhdFxYXRcGB0XRseF0XFhdL7CdiGE4zcRPRNMD1b+U8vZ80pn8ygY3d2zGeZV5yUt3NbW1HTQF63hoetIZPiaXptuIQRFYOUM27RVUxAGs12KwnZqdatIyhuP61Rpsh6OQrTTspPnCEXdet1eOr21rE3pPsx3HgQ+UUzPbTIs74WIxs7h2lHOzuFDI9fjXLXqQLMsdc5q20VBeh6W3C/3V/PDAxGd+6yq1VyGZ9lUV5EVNmIrfJ7lpSstbMSWQR2P6Y1HVbkSJjkhbD+9O7qo7J6HFfs2ySWxsNlzQnLc27/xmCi9wgv/Dg92Zu/NcsW05zJsxLZIVZwWl6VIoHbc+fbZ87CFpWhPtTASYePCtyU8LD7Sj+LC6LgwOi6Mjguj8wNf+KaAp1GZPgAAAABJRU5ErkJggg==
                         """
                            .trimIndent()
                    )
                ),
                onFetchCaptcha = {}
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "Farsi", locale = "fa")
@Composable
fun CaptchaRowErrorPreview() {
    NetWidgetAppTheme {
        Surface {
            CaptchaRow(
                captchaState = RequestStatus.Error("Error fetching captcha"),
                onFetchCaptcha = {}
            )
        }
    }
}
