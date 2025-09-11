package com.karrar.movieapp.ui.category.uiState

data class MediaUIState(
    val mediaID: Int,
    val mediaImage: String,
    val mediaType: String,
    val mediaName: String,
    val mediaRate: String,
    val mediaGenres: String = "",
    val mediaDate :String = ""
)
