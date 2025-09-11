package com.karrar.movieapp.ui.movieDetails.rateDialog
import com.karrar.movieapp.ui.base.BaseInteractionListener

interface RateDialogInteractionListener : BaseInteractionListener {
    fun onStarClick(newRate : Float)
    fun onSubmitClick()
    fun onCancelClick()
}