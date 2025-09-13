package com.karrar.movieapp.ui.match_result

interface MatchResultInteractionListener {
    fun onClickViewDetails()
    fun onClickBack()
    fun onClickYoutubeTrailer(movieId : Int)
    fun onClickSaveMovie(movieId : Int)
}