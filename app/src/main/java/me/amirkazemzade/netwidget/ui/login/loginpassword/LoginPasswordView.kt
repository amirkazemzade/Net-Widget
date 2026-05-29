package me.amirkazemzade.netwidget.ui.login.loginpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.ui.login.components.FieldButton
import me.amirkazemzade.netwidget.ui.login.components.FieldColumn
import me.amirkazemzade.netwidget.ui.theme.NetWidgetDimensions
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme

@Composable
fun LoginPasswordView(
    username: String,
    onLogin: (password: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {

    val state = rememberSaveable(saver = LoginPasswordStateSaver) { LoginPasswordState() }

    LoginPasswordView(
        state = state,
        username = username,
        onLogin = onLogin,
        modifier = modifier,
        isLoading = isLoading,
    )
}

@Composable
fun LoginPasswordView(
    state: LoginPasswordState,
    username: String,
    onLogin: (password: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val passwordRequiredMessage = stringResource(R.string.password_required)
    val shape = MaterialTheme.shapes.large

    FieldColumn(modifier = modifier) {
        OutlinedTextField(
            value = username,
            label = { Text(stringResource(R.string.username)) },
            shape = shape,
            readOnly = true,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .semantics {
                    contentType = ContentType.Username
                    this.shape = shape
                }
        )

        Box(modifier = Modifier.height(NetWidgetDimensions.medium))

        OutlinedTextField(
            value = state.password,
            label = { Text(stringResource(R.string.password)) },
            onValueChange = state::setPassword,
            shape = shape,
            visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    callOnLogin(
                        state = state,
                        passwordRequiredMessage = passwordRequiredMessage,
                        onLogin = onLogin
                    )
                }
            ),
            trailingIcon = {
                IconButton(onClick = state::reverseShowPassword) {
                    val id =
                        if (state.showPassword) R.drawable.rounded_visibility_24
                        else R.drawable.rounded_visibility_off_24
                    val contentDescription =
                        if (state.showPassword) stringResource(R.string.hide_password)
                        else stringResource(R.string.show_password)
                    Icon(painter = painterResource(id), contentDescription = contentDescription)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .semantics {
                    contentType = ContentType.Password
                    this.shape = shape
                    this.password()
                },
        )

        Box(modifier = Modifier.height(NetWidgetDimensions.large))

        FieldButton(
            isLoading = isLoading,
            text = stringResource(R.string.login),
            onClick = {
                callOnLogin(
                    state = state,
                    passwordRequiredMessage = passwordRequiredMessage,
                    onLogin = onLogin
                )
            },
        )
    }

}

private fun callOnLogin(
    state: LoginPasswordState,
    passwordRequiredMessage: String,
    onLogin: (String) -> Unit,
) {
    if (!state.validate(passwordRequiredMessage)) return
    onLogin(state.password)
}


@PreviewLightDark
@Composable
private fun LoginPasswordViewPreview() {
    NetWidgetAppTheme {
        Surface {
            LoginPasswordView(
                username = "123456789",
                onLogin = {}
            )
        }
    }
}
