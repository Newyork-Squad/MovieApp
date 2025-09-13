package com.karrar.movieapp.ui.search.uiStatMapper

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState
import javax.inject.Inject

class RecentMovieViewedUiStateMapper @Inject constructor() :
    Mapper<Media, RecentMovieViewedUiState> {
    override fun map(input: Media): RecentMovieViewedUiState {
        return RecentMovieViewedUiState(
            input.mediaID,
            input.mediaType,
            input.mediaImage,
            "%.1f".format(input.mediaRate),
            input.mediaName
        )
    }
}