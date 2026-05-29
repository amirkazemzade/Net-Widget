package me.amirkazemzade.netwidget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import me.amirkazemzade.netwidget.domain.models.RequestStatus
import me.amirkazemzade.netwidget.domain.usecases.CheckAuthUseCase
import me.amirkazemzade.netwidget.ui.navigation.Screen
import javax.inject.Inject

@HiltViewModel
class AuthStateViewModel @Inject constructor(
    checkAuthUseCase: CheckAuthUseCase,
) : ViewModel() {

    val authState = checkAuthUseCase()
        .map { state ->
            when (state) {
                is RequestStatus.Success -> Screen.Dashboard.route
                is RequestStatus.Error -> Screen.Login.route
                else -> null
            }
        }
        .filterNotNull()
        .distinctUntilChanged()
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            replay = 0
        )

}
