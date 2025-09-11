package com.karrar.movieapp.ui.movieDetails.loginDialog

import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.GetSessionIDUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginDialogViewModel @Inject constructor(
    private val sessionIDUseCase: GetSessionIDUseCase,
) : BaseViewModel(), LoginDialogInteractionListener {

    private val _loginDialogUIState = MutableStateFlow(LoginDialogUIState())
    val loginDialogUIState = _loginDialogUIState.asStateFlow()

    private val _loginDialogUIEvent = MutableStateFlow<Event<LoginDialogUIEvent?>>(Event(null))
    val loginDialogUIEvent = _loginDialogUIEvent.asStateFlow()

    init {
        getData()
    }

    override fun getData() {
        viewModelScope.launch {
            try {
                if (!sessionIDUseCase().isNullOrEmpty()) {
                    _loginDialogUIState.update { it.copy(isGuest = true) }
                }
            } catch (t: Throwable) {
            }
        }
    }

    override fun onGoToLoginClick() {
        viewModelScope.launch {
            _loginDialogUIEvent.update { Event(LoginDialogUIEvent.NavigateToLoginPage) }
        }
    }

    override fun onCancelClick() {
        viewModelScope.launch {
            _loginDialogUIEvent.update { Event(LoginDialogUIEvent.CloseDialog) }
        }
    }
}