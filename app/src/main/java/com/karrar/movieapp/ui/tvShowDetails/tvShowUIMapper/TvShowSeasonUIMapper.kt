package com.karrar.movieapp.ui.tvShowDetails.tvShowUIMapper

import android.annotation.SuppressLint
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Season
import com.karrar.movieapp.ui.tvShowDetails.tvShowUIState.SeasonUIState
import javax.inject.Inject

class TvShowSeasonUIMapper @Inject constructor() : Mapper<Season, SeasonUIState> {
    @SuppressLint("DefaultLocale")
    override fun map(input: Season): SeasonUIState {
        return SeasonUIState(
            seasonName = input.seasonName,
            seasonNumber = input.seasonNumber,
            imageUrl = input.imageUrl,
            episodeCount = input.episodeCount,
            seasonYear = input.seasonYear.take(4),
            seasonDescription = input.seasonDescription,
            seasonRate = input.seasonRate.toString().take(3)
        )
    }
}
