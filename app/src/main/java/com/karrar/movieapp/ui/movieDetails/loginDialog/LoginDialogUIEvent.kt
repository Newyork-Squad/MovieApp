package com.karrar.movieapp.ui.movieDetails.loginDialog

sealed interface LoginDialogUIEvent {
    object NavigateToLoginPage : LoginDialogUIEvent
    object CloseDialog : LoginDialogUIEvent
}