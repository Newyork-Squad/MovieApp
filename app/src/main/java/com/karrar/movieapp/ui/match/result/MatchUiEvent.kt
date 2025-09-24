package com.karrar.movieapp.ui.match.result

sealed interface MatchUiEvent {

    object NavigateBack: MatchUiEvent
    object ViewMovieDetails : MatchUiEvent
    data class PlayYoutubeTrailer(val movieId : Int) : MatchUiEvent
    data class SaveMovie(val movieId : Int) : MatchUiEvent

    object NavigateToResults : MatchUiEvent

    object ShowNoMoviesToast : MatchUiEvent
}