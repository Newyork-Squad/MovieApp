package com.karrar.movieapp.ui.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.DialogSignupBinding
import com.karrar.movieapp.databinding.FragmentLoginBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val layoutIdFragment = R.layout.fragment_login
    override val viewModel: LoginViewModel by viewModels()
    private var signUpDialog: AlertDialog? = null

    override fun onStart() {
        super.onStart()
        setTitle(false)
        collectLast(viewModel.loginEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }

        collectLast(viewModel.loginUIState) { uiState ->
            if (uiState.showSignUpDialog) {
                showSignUpDialog()
            } else {
                dismissSignUpDialog()
            }
        }

    }

    private fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.LoginEvent -> {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToProfileFragment())
            }

            LoginUIEvent.SignUpEvent -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TMDB_SIGNUP_URL))
                startActivity(browserIntent)
            }

            LoginUIEvent.ForgotPasswordEvent -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TMDB_FORGOTPASSWORD_URL))
                startActivity(browserIntent)
            }

            LoginUIEvent.JoinAsGuestEvent -> findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())

        }

    }


    private fun showSignUpDialog() {
        if (signUpDialog?.isShowing == true) return

        val dialogBinding = DialogSignupBinding.inflate(layoutInflater)
        dialogBinding.viewModel = viewModel
        dialogBinding.lifecycleOwner = viewLifecycleOwner

        AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
            .apply {

                setOnDismissListener {
                    if (viewModel.loginUIState.value.showSignUpDialog) {
                        viewModel.onClickCancelSignUp()
                    }
                }

                show()
            }.also { signUpDialog = it }
    }

    private fun dismissSignUpDialog() {
        signUpDialog?.dismiss()
        signUpDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissSignUpDialog()
    }


}

