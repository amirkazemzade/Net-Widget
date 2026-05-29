package me.amirkazemzade.netwidget.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.amirkazemzade.netwidget.domain.models.LoginRequest
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.models.Status
import me.amirkazemzade.netwidget.domain.repositories.CookieRepository
import me.amirkazemzade.netwidget.domain.usecases.FetchCaptchaUseCase
import me.amirkazemzade.netwidget.domain.usecases.LoginWithPasswordUseCase
import me.amirkazemzade.netwidget.domain.usecases.RequestLoginUseCase
import me.amirkazemzade.netwidget.ui.login.loginrequest.LoginEvent
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModel @Inject constructor(
    private val fetchCaptchaUseCase: FetchCaptchaUseCase,
    private val requestLoginUseCase: RequestLoginUseCase,
    private val loginWithPasswordUseCase: LoginWithPasswordUseCase,
    private val cookieRepository: CookieRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<LoginEvent>()
    val event = _event.asSharedFlow()

    private val _fetchCaptcha = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val captchaState = _fetchCaptcha
        .onStart { emit(Unit)}
        .flatMapLatest {
            Log.d("LoginScreen", "Fetch captcha triggered.")
            fetchCaptchaUseCase()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Status.Idle)

    private val _loginRequestState =
        MutableStateFlow<Status<LoginRequest>>(Status.Idle)
    val loginRequestState = _loginRequestState.asStateFlow()

    private val _loginWithPasswordState =
        MutableStateFlow<Status<Unit>>(Status.Idle)
    val loginWithPasswordState = _loginWithPasswordState.asStateFlow()

    fun fetchCaptcha() {
        viewModelScope.launch {
            _fetchCaptcha.emit(Unit)
        }
    }

    fun loginRequest(username: String, captchaResult: String) {
        val currentCaptchaState = captchaState.value
        if (currentCaptchaState !is RequestStatus.Success) return
        viewModelScope.launch {
            requestLoginUseCase(
                loginRequest = LoginRequest(
                    username = username,
                    captchaResult = captchaResult
                )
            ).collectLatest { newStatus ->
                _loginRequestState.value = newStatus

                when (newStatus) {
                    is RequestStatus.Error -> {
                        fetchCaptcha()
                        _event.emit(LoginEvent.Error(newStatus.message))
                    }

                    is RequestStatus.Success<*> -> {
                        _event.emit(LoginEvent.LoginRequestSuccess)
                    }

                    RequestStatus.Loading -> {}
                }
            }
        }
    }

    fun loginWithPassword(password: String) {
        val currentLoginRequestState = loginRequestState.value
        if (currentLoginRequestState !is RequestStatus.Success<LoginRequest>) return

        viewModelScope.launch {
            loginWithPasswordUseCase(
                username = currentLoginRequestState.data.username,
                password = password,
                captchaResult = currentLoginRequestState.data.captchaResult
            ).collectLatest { newStatus ->
                _loginWithPasswordState.value = newStatus

                when (newStatus) {
                    is RequestStatus.Error -> {
                        _event.emit(LoginEvent.Error(newStatus.message))
                    }

                    is RequestStatus.Success<*> -> {
                        setLoggedIn()
                        _event.emit(LoginEvent.LoginPasswordSuccess)
                    }

                    RequestStatus.Loading -> {}
                }
            }
        }
    }

    private fun setLoggedIn() {
        viewModelScope.launch {
            cookieRepository.setLoginState(true)
        }
    }

}