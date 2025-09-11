package com.karrar.movieapp.ui.movieDetails.rateDialog

sealed interface RateDialogUIEvent {
    object CloseDialog : RateDialogUIEvent
    data class ChooseRate(val rate: Float) : RateDialogUIEvent
    data class SubmitRate(val rate: Float) : RateDialogUIEvent
    data class ShowMessage(val message: String) : RateDialogUIEvent
}