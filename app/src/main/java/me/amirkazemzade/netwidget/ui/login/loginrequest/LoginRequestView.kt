package me.amirkazemzade.netwidget.ui.login.loginrequest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.CaptchaBase64
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.ui.login.components.FieldButton
import me.amirkazemzade.netwidget.ui.login.components.FieldColumn
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions
import me.amirkazemzade.netwidget.ui.theme.PreviewTheme

@Composable
fun LoginRequestView(
    captchaState: Status<CaptchaBase64>,
    onFetchCaptcha: () -> Unit,
    onLoginRequest: (username: String, captchaResult: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val loginRequestState = rememberSaveable(saver = LoginRequestStateSaver) { LoginRequestState() }

    LoginRequestView(
        state = loginRequestState,
        captchaState = captchaState,
        onFetchCaptcha = onFetchCaptcha,
        onLoginRequest = onLoginRequest,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
fun LoginRequestView(
    state: LoginRequestState,
    captchaState: Status<CaptchaBase64>,
    onFetchCaptcha: () -> Unit,
    onLoginRequest: (username: String, captchaResult: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val captchaFocusRequester = remember { FocusRequester() }
    val usernameRequiredMessage = stringResource(R.string.username_required)
    val captchaRequiredMessage = stringResource(R.string.enter_captcha)
    val shape = MaterialTheme.shapes.large

    FieldColumn(modifier = modifier) {
        OutlinedTextField(
            value = state.username,
            label = { Text(stringResource(R.string.username)) },
            shape = shape,
            onValueChange = state::setUsername,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { captchaFocusRequester.requestFocus() }),
            singleLine = true,
            isError = state.usernameError != null,
            supportingText = {
                if (state.usernameError != null) {
                    Text(state.usernameError!!)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .semantics {
                    contentType = ContentType.Username
                    this.shape = shape
                }
        )

        Box(modifier = Modifier.height(NetWidgetDimensions.medium))

        CaptchaRow(
            captchaState = captchaState,
            onFetchCaptcha = onFetchCaptcha,
        )
        OutlinedTextField(
            value = state.captcha,
            label = { Text(stringResource(R.string.captcha)) },
            shape = shape,
            onValueChange = state::setCaptcha,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    callOnLoginRequest(
                        state,
                        usernameRequiredMessage,
                        captchaRequiredMessage,
                        onLoginRequest
                    )
                }),
            singleLine = true,
            isError = state.captchaError != null,
            supportingText = {
                if (state.captchaError != null) {
                    Text(state.captchaError!!)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(captchaFocusRequester)
        )

        Box(modifier = Modifier.height(NetWidgetDimensions.large))

        FieldButton(
            text = stringResource(R.string.continue_action),
            enabled = !isLoading,
            onClick = {
                callOnLoginRequest(
                    state,
                    usernameRequiredMessage,
                    captchaRequiredMessage,
                    onLoginRequest
                )
            },
        )
    }
}

private fun callOnLoginRequest(
    state: LoginRequestState,
    usernameRequiredMessage: String,
    captchaRequiredMessage: String,
    onLoginRequest: (String, String) -> Unit,
) {
    if (!state.validate(usernameRequiredMessage, captchaRequiredMessage)) return
    onLoginRequest(state.username, state.captcha)
}


@Preview
@Composable
fun LoginRequestViewPreviewTheme() {
    PreviewTheme {
        LoginRequestView(
            captchaState = Status.Idle,
            onFetchCaptcha = {},
            onLoginRequest = { _, _ -> },
            isLoading = false
        )
    }
}
