package me.amirkazemzade.netwidget.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.amirkazemzade.netwidget.data.api.AuthApi
import me.amirkazemzade.netwidget.data.converters.toCaptchaBase64
import me.amirkazemzade.netwidget.data.converters.toCookie
import me.amirkazemzade.netwidget.data.converters.toHeaderValue
import me.amirkazemzade.netwidget.data.models.requests.LoginWithPasswordBody
import me.amirkazemzade.netwidget.data.models.requests.RequestLoginBody
import me.amirkazemzade.netwidget.domain.models.AuthenticatedResult
import me.amirkazemzade.netwidget.domain.models.CaptchaBase64
import me.amirkazemzade.netwidget.domain.models.Cookie
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authApi: AuthApi) : AuthRepository {
    override suspend fun checkLoginStatus(
        cookie: Cookie,
    ): Flow<RequestStatus<AuthenticatedResult<Unit>>> = flow {
        emit(RequestStatus.Loading)
        val response = authApi.getFiscalId(cookie = cookie.toHeaderValue())
        if (response.isSuccessful) {
            val newCookie = response.headers().values("Set-Cookie")
                .firstOrNull { it.contains("session=", ignoreCase = true) }
                ?.toCookie() ?: response.headers()["Set-Cookie"]?.toCookie()
            emit(RequestStatus.Success(AuthenticatedResult(Unit, newCookie ?: cookie)))
        } else {
            emit(RequestStatus.Error(response.message(), response.code()))
        }
    }

    override suspend fun getCaptcha(
        cookie: Cookie?,
    ): Flow<RequestStatus<AuthenticatedResult<CaptchaBase64>>> = flow {
        emit(RequestStatus.Loading)
        val response = authApi.captcha(cookie = cookie?.toHeaderValue())
        if (response.isSuccessful) {
            val data = response.body()!!.toCaptchaBase64()
            val cookie = response.headers().values("Set-Cookie")
                .firstOrNull { it.contains("session=", ignoreCase = true) }
                ?.toCookie() ?: response.headers()["Set-Cookie"]?.toCookie()
            emit(RequestStatus.Success(AuthenticatedResult(data, cookie)))
        } else {
            emit(RequestStatus.Error(response.message(), response.code()))
        }
    }

    override suspend fun requestLoginForUser(
        cookie: Cookie,
        username: String,
        captchaResult: String,
    ): Flow<RequestStatus<AuthenticatedResult<Unit>>> = flow {
        emit(RequestStatus.Loading)
        val response = authApi.requestLogin(
            cookie = cookie.toHeaderValue(),
            body = RequestLoginBody(
                username = username,
                captcha = captchaResult,
            )
        )
        if (response.isSuccessful) {
            val data = response.body()!!
            if (!data.ok) {
                emit(RequestStatus.Error(data.message))
                return@flow
            }
            emit(RequestStatus.Success(AuthenticatedResult(Unit, cookie)))
        } else {
            emit(RequestStatus.Error(response.message(), response.code()))
        }
    }

    override suspend fun login(
        cookie: Cookie,
        username: String,
        password: String,
        captchaResult: String,
    ): Flow<RequestStatus<AuthenticatedResult<Unit>>> = flow {
        emit(RequestStatus.Loading)

        val response = authApi.login(
            cookie = cookie.toHeaderValue(),
            body = LoginWithPasswordBody(
                username = username,
                password = password,
                captcha = captchaResult,
            )
        )

        if (response.isSuccessful) {
            val data = response.body()!!
            if (!data.ok) {
                emit(RequestStatus.Error(data.message))
                return@flow
            }

            authApi.home(cookie.toHeaderValue())

            emit(RequestStatus.Success(AuthenticatedResult(Unit, cookie)))
        } else {
            emit(RequestStatus.Error(response.message(), response.code()))
        }
    }
}