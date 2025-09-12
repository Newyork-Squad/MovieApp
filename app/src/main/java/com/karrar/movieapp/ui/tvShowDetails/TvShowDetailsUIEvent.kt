package com.karrar.movieapp.ui.tvShowDetails


sealed interface TvShowDetailsUIEvent {
    object ClickBackEvent : TvShowDetailsUIEvent
    object ClickPlayTrailerEvent : TvShowDetailsUIEvent
    object MessageAppear : TvShowDetailsUIEvent
    object ClickReviewsEvent : TvShowDetailsUIEvent
    object ClickSeasonsEvent : TvShowDetailsUIEvent
    data class ClickSeasonEvent(val seasonId: Int) : TvShowDetailsUIEvent
    data class ClickCastEvent(val castID: Int) : TvShowDetailsUIEvent
    data class ClickTvShowEvent(val thShowId: Int) : TvShowDetailsUIEvent
}