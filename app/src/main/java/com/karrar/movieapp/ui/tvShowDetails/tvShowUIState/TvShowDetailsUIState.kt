package com.karrar.movieapp.ui.tvShowDetails.tvShowUIState

import com.karrar.movieapp.ui.models.ActorUiState
import com.karrar.movieapp.ui.models.CrewUIState
import com.karrar.movieapp.ui.models.MediaUiState

data class TvShowDetailsUIState(
    val tvShowDetailsResult: TvShowDetailsResultUIState = TvShowDetailsResultUIState(),
    val seriesCastResult: List<ActorUiState> = listOf(),
    val seriesCrewResult: List<CrewUIState> = listOf(),
    val seriesSeasonsResult: List<SeasonUIState> = listOf(),
    val similarTvShowsResult: List<MediaUiState> = listOf(),
    val seriesReviewsResult: List<ReviewUIState> = listOf(),
    val detailItemResult: List<DetailItemUIState> = listOf(),
    val ratingValue: Float = 0F,
    val isLoading: Boolean = false,
    val isLogin: Boolean = false,
    val errorUIState: List<Error> = emptyList()
)
