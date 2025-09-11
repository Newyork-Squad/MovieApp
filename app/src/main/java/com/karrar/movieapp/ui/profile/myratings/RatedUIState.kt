package com.karrar.movieapp.ui.profile.myratings

data class RatedUIState(
    val id: Int,
    val title: String,
    val rating: Float,
    val genres: List<String>,
    val posterPath: String,
    var mediaType: String = "",
    val releaseDate: String,
)