package com.karrar.movieapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.startUp.MarkAsNotFirstLaunchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val markAsNotFirstLaunchUseCase: MarkAsNotFirstLaunchUseCase,
    ) : ViewModel() {
        fun markAsNotFirstLaunch() {
            viewModelScope.launch {
                markAsNotFirstLaunchUseCase()
            }
        }
    }
