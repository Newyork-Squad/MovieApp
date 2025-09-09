package com.karrar.movieapp.ui.profile

data class ProfileUIState(
    val avatarPath: String = "",
    val name: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: Boolean = false,
) {
    val displayName: String
        get() = when {
            !isLoggedIn -> "Login or Sign Up"
            name.isEmpty() -> "Tap to add your name"
            else -> name
        }

    val displayUsername: String
        get() = when {
            !isLoggedIn -> "to personalize your profile"
            else -> "@$username"
        }
}
