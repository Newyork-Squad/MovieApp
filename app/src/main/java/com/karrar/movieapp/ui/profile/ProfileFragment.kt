package com.karrar.movieapp.ui.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentProfileBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.collectLast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_profile
    override val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeProfileState()
        observeDarkMode()
        setupDarkModeSwitch()
        collectEvents()
    }
    private fun setupUI() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        setTitle(false, getString(R.string.profile))
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.background_screen)
        activity?.window?.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.background_screen)
    }

    private fun observeProfileState() {
        collectLast(viewModel.profileDetailsUIState) {
            updateProfileTexts()
        }
    }

    private fun observeDarkMode() {
        collectLast(viewModel.darkMode) { darkMode ->
            if (binding.switchDarkMode.isChecked != darkMode) {
                binding.switchDarkMode.isChecked = darkMode
            }
        }
    }

    private fun setupDarkModeSwitch() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkMode(isChecked)
        }
    }

    private fun collectEvents() {
        collectLast(viewModel.profileUIEvent) {
            it.getContentIfNotHandled()?.let { onEvent(it) }
        }
    }

    private fun onEvent(event: ProfileUIEvent) {
        val currentDestination = findNavController().currentDestination?.id
        val action = when (event) {
            ProfileUIEvent.DialogLogoutEvent ->
                ProfileFragmentDirections.actionProfileFragmentToLogoutDialog()
            ProfileUIEvent.LoginEvent ->
                ProfileFragmentDirections.actionProfileFragmentToLoginFragment(Constants.PROFILE)
            ProfileUIEvent.RatedMoviesEvent ->
                ProfileFragmentDirections.actionProfileFragmentToRatedMoviesFragment()
            ProfileUIEvent.WatchHistoryEvent ->
                ProfileFragmentDirections.actionProfileFragmentToWatchHistoryFragment()
            ProfileUIEvent.MyCollectionsEvent ->
                ProfileFragmentDirections.actionProfileFragmentToMyListFragment()
            ProfileUIEvent.EditProfileEvent ->
                ProfileFragmentDirections.actionProfileFragmentToEditProfileConfirmDialog()
            ProfileUIEvent.ShowLanguagePicker ->
                ProfileFragmentDirections.actionProfileFragmentToLanguagePickerDialog()
            ProfileUIEvent.ShowContentPreferences ->
                ProfileFragmentDirections.actionProfileFragmentToContentPreferencesDialog()
        }
        if (currentDestination == R.id.profileFragment) {
            findNavController().navigate(action)
        }    }

    private fun updateProfileTexts() {
        val state = viewModel.profileDetailsUIState.value

        binding.textNameActor.text = when {
            !state.isLoggedIn || state.isGuest -> getString(R.string.login_or_sign_up)
            state.name.isEmpty() -> getString(R.string.tap_to_add_your_name)
            else -> state.name
        }

        binding.textUsername.text = when {
            !state.isLoggedIn || state.isGuest -> getString(R.string.to_personalize_your_profile)
            else -> "@${state.username}"
        }
    }
}