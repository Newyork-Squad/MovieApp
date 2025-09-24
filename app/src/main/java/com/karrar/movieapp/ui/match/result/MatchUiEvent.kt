package com.karrar.movieapp.ui.match.result

sealed interface MatchUiEvent {

    object NavigateBack: MatchUiEvent
    data class ViewMovieDetails(val movieId: Int) : MatchUiEvent
    data class PlayYoutubeTrailer(val movieId : Int) : MatchUiEvent
    data class SaveMovie(val movieId : Int) : MatchUiEvent

    object NavigateToResults : MatchUiEvent

    object ShowNoMoviesToast : MatchUiEvent
    object ShowLoginDialogEvent: MatchUiEvent
}