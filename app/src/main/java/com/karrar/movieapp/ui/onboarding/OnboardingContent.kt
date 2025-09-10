package com.karrar.movieapp.ui.onboarding

import androidx.annotation.DrawableRes

data class OnboardingContent(
    val title: String,
    val description: String,
    @DrawableRes val imageResId: Int
)
