package com.karrar.movieapp.ui.movieDetails.loginDialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.DialogLoginBinding
import com.karrar.movieapp.ui.base.BaseDialogFragment
import com.karrar.movieapp.utilities.collectLast
import com.karrar.movieapp.utilities.setWidthPercent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginDialog : BaseDialogFragment<DialogLoginBinding>() {

    override val layoutIdFragment = R.layout.dialog_login
    override val viewModel: LoginDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWidthPercent(90)
        collectLast(viewModel.loginDialogUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.onGoToLoginClick()
        }

        binding.btnCancel.setOnClickListener {
            viewModel.onCancelClick()
        }
    }

    private fun onEvent(event: LoginDialogUIEvent) {
        when (event) {
            LoginDialogUIEvent.NavigateToLoginPage -> {
                val action =
                    LoginDialogDirections.actionLoginDialogToLoginFragment(R.id.action_loginDialog_to_loginFragment)
                findNavController().navigate(action)
            }

            LoginDialogUIEvent.CloseDialog -> {
                dismiss()
            }
        }
    }

}