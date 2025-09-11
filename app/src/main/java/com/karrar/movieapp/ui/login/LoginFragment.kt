package com.karrar.movieapp.ui.login

import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
            it.getContentIfNotHandled()?.let(::onEvent)
        }

        collectLast(viewModel.loginUIState) { uiState ->
            if (uiState.showSignUpDialog) showSignUpDialog()
            else dismissSignUpDialog()
        }
    }

    private fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.LoginEvent ->
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToProfileFragment())

            LoginUIEvent.SignUpEvent ->
                openBrowser(BuildConfig.TMDB_SIGNUP_URL)

            LoginUIEvent.ForgotPasswordEvent ->
                openBrowser(BuildConfig.TMDB_FORGOTPASSWORD_URL)

            LoginUIEvent.JoinAsGuestEvent ->
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showSignUpDialog() {
        if (signUpDialog?.isShowing == true) return

        val dialogBinding = DialogSignupBinding.inflate(layoutInflater).apply {
            viewModel = this@LoginFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        signUpDialog = createSignUpDialog(dialogBinding).apply {
            setOnDismissListener {
                if (viewModel.loginUIState.value.showSignUpDialog) {
                    viewModel.onClickCancelSignUp()
                }
            }
            show()
        }
    }

    private fun createSignUpDialog(binding: DialogSignupBinding): AlertDialog {
        return AlertDialog.Builder(requireContext(), R.style.SignUpDialogStyle)
            .setView(binding.root)
            .setCancelable(true)
            .create().apply {
                window?.apply {
                    setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bg_dialog
                        )
                    )

                    val horizontalMargin =
                        resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
                    val bottomMargin = resources.getDimensionPixelSize(R.dimen.dialog_bottom_margin)

                    attributes = attributes?.apply {
                        width = resources.displayMetrics.widthPixels - (2 * horizontalMargin)
                        gravity = Gravity.BOTTOM
                        y = bottomMargin
                    }
                }
            }
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
