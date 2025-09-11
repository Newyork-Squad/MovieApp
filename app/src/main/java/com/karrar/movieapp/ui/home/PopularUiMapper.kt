package com.karrar.movieapp.ui.home

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.PopularMovie
import com.karrar.movieapp.ui.home.homeUiState.PopularUiState
import java.util.Locale.getDefault
import javax.inject.Inject


class PopularUiMapper @Inject constructor() : Mapper<PopularMovie, PopularUiState> {
    override fun map(input: PopularMovie): PopularUiState {
        val formattedRate = String.format(getDefault(), "%.1f", input.movieRate)

        return PopularUiState(
            input.movieID,
            input.title,
            input.imageUrl,
            formattedRate,
            input.genre
        )
    }
}