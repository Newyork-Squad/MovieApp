package com.karrar.movieapp.ui.match.questions

import androidx.annotation.DrawableRes

data class Choice(
    val name: String,
    val description: String = "",
    @DrawableRes val icon: Int? = null,
    val isSelected: Boolean = false,
)
