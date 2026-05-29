package me.amirkazemzade.netwidget.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.amirkazemzade.netwidget.R
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.ui.login.loginpassword.LoginPasswordView
import me.amirkazemzade.netwidget.ui.login.loginrequest.LoginEvent
import me.amirkazemzade.netwidget.ui.login.loginrequest.LoginRequestView
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val captchaState by loginViewModel.captchaState.collectAsStateWithLifecycle()
    val loginRequestState by loginViewModel.loginRequestState.collectAsStateWithLifecycle()
    val loginWithPasswordState by loginViewModel.loginWithPasswordState.collectAsStateWithLifecycle()

    val autofillManager = LocalAutofillManager.current

    LaunchedEffect(Unit) {
        loginViewModel.event.collect { event ->
            when (event) {
                is LoginEvent.Error -> {
                    autofillManager?.cancel()

                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                LoginEvent.LoginRequestSuccess -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_requested_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                LoginEvent.LoginPasswordSuccess -> {
                    autofillManager?.commit()

                    Toast.makeText(
                        context,
                        context.getString(R.string.logged_in_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val currentLoginRequestState = loginRequestState) {
                is RequestStatus.Success -> {
                    LoginPasswordView(
                        username = currentLoginRequestState.data.username,
                        isLoading = loginWithPasswordState is RequestStatus.Loading,
                        onLogin = { password ->
                            loginViewModel.loginWithPassword(password)
                        }
                    )
                }

                else -> {
                    LoginRequestView(
                        captchaState = captchaState,
                        isLoading = currentLoginRequestState is RequestStatus.Loading,
                        onFetchCaptcha = { loginViewModel.fetchCaptcha() },
                        onLoginRequest = loginViewModel::loginRequest
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    NetWidgetAppTheme {
        LoginScreen()
    }
}
