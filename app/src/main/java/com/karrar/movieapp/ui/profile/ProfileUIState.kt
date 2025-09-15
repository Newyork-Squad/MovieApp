package com.karrar.movieapp.ui.profile

data class ProfileUIState(
    val avatarPath: String = "",
    val name: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: Boolean = false,
){
    val isRealUser: Boolean
        get() = isLoggedIn && !isGuest
}
