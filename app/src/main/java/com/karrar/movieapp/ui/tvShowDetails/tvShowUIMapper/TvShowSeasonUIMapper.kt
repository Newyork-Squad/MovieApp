package com.karrar.movieapp.ui.tvShowDetails.tvShowUIMapper

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Season
import com.karrar.movieapp.ui.tvShowDetails.tvShowUIState.SeasonUIState
import com.karrar.movieapp.utilities.Constants
import javax.inject.Inject

class TvShowSeasonUIMapper @Inject constructor() : Mapper<Season, SeasonUIState> {
    override fun map(input: Season): SeasonUIState {
        return SeasonUIState(
            seasonName = input.seasonName,
            seasonNumber = input.seasonNumber,
            imageUrl = input.imageUrl,
            episodeCount = input.episodeCount,
            seasonYear = input.seasonYear.take(Constants.SEASON_YEAR_LENGTH),
            seasonDescription = input.seasonDescription,
            seasonRate = input.seasonRate.toString().take(Constants.RATE_DISPLAY_LENGTH)
        )
    }
}
