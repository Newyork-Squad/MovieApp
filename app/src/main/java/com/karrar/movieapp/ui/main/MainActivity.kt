package com.karrar.movieapp.ui.main

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var currentLanguage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialMode = runBlocking { viewModel.darkMode.first() }

        AppCompatDelegate.setDefaultNightMode(
            if (initialMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setTheme(R.style.Theme_MovieApp)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        installSplashScreen()
        val language = runBlocking { viewModel.language.first() }
        updateLocale(language)
        viewModel.getData()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.darkMode.collect { darkMode ->
                val mode = if (darkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }

                if (AppCompatDelegate.getDefaultNightMode() != mode) {
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.language.collect { language ->
                if (currentLanguage == null) {
                    currentLanguage = language
                    updateLocale(language)
                } else if (currentLanguage != language) {
                    currentLanguage = language

                    lifecycleScope.launch {
                        try {
                            viewModel.clearCache(language)
                            updateLocale(language)
                            viewModel.refreshData()
                            recreate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            recreate()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.exploringFragment,
                R.id.matchScreenFragment,
                R.id.profileFragment,
            )
        )
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavigation.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        setBottomNavigationVisibility(navController)
        setNavigationController(navController)

        lifecycleScope.launchWhenResumed {
            viewModel.mainUiState.collect {
                if (!it.isFirstLaunch) navController.navigate(R.id.onboardingFragment)
            }
        }
    }

    private fun setBottomNavigationVisibility(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible =
                when (destination.id) {
                    R.id.loginFragment, R.id.onboardingFragment,
                    R.id.watchHistoryFragment, R.id.myListFragment,
                    R.id.ratedMoviesFragment, R.id.edit_profile,
                    R.id.createSavedList, R.id.listDetailsFragment,
                    R.id.allMovieFragment, R.id.matchQuestionsFragment,
                    R.id.actorDetailsFragment, R.id.actorGalleryFragment,
                        -> {
                        false
                    }
                    else -> true
                }
        }
    }

    private fun setNavigationController(navController: NavController) {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            navController.popBackStack(item.itemId, inclusive = false)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    fun updateLocale(language: String) {
        val locale = when (language) {
            "Arabic" -> Locale("ar")
            else -> Locale("en")
        }
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        if (::binding.isInitialized) {
            window.decorView.layoutDirection =
                if (locale.language == "ar") View.LAYOUT_DIRECTION_RTL
                else View.LAYOUT_DIRECTION_LTR
        }
    }

}