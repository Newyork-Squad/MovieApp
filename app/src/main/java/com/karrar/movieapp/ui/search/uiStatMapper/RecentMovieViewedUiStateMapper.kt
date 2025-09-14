package com.karrar.movieapp.ui.search.uiStatMapper

import com.karrar.movieapp.data.local.database.entity.WatchHistoryEntity
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState
import javax.inject.Inject

class RecentMovieViewedUiStateMapper @Inject constructor() :
    Mapper<WatchHistoryEntity, RecentMovieViewedUiState> {
    override fun map(input: WatchHistoryEntity): RecentMovieViewedUiState {
        return RecentMovieViewedUiState(
            mediaID = input.id,
            mediaType = input.mediaType,
            mediaImage = input.posterPath,
            mediaRate = input.voteAverage,
            mediaName = input.movieTitle
        )
    }
}