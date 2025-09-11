package com.karrar.movieapp.ui.tvShowDetails.tvShowUIState

import androidx.lifecycle.ViewModel
import com.karrar.movieapp.ui.models.ActorUiState
import com.karrar.movieapp.ui.models.CrewUIState
import com.karrar.movieapp.ui.models.MediaUiState

sealed class DetailItemUIState(val priority: Int) {

    class Header(val data: TvShowDetailsResultUIState) : DetailItemUIState(0)

    class Seasons(val data: List<SeasonUIState>) : DetailItemUIState(1)

    class Cast(val data: List<ActorUiState>) : DetailItemUIState(2)

    class Crew(val data: List<CrewUIState>) : DetailItemUIState(3)

    class SimilarTvShows(val data: List<MediaUiState>) : DetailItemUIState(4)

    class Rating(val viewModel: ViewModel) : DetailItemUIState(5)

    object ReviewText : DetailItemUIState(6)

    class Comment(val data: ReviewUIState) : DetailItemUIState(7)
}
