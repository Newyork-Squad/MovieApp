package com.karrar.movieapp.ui.movieDetails.loginDialog
import com.karrar.movieapp.ui.base.BaseInteractionListener

interface LoginDialogInteractionListener : BaseInteractionListener {
    fun onGoToLoginClick()
    fun onCancelClick()
}