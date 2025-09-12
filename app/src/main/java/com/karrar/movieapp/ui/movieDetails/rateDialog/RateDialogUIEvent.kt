package com.karrar.movieapp.ui.movieDetails.rateDialog

sealed interface RateDialogUIEvent {
    object CloseDialog : RateDialogUIEvent
    data class ShowMessage(val message: String) : RateDialogUIEvent
}