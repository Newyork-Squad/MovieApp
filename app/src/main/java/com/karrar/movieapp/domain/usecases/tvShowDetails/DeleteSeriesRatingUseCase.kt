package com.karrar.movieapp.domain.usecases.tvShowDetails

import com.karrar.movieapp.data.repository.SeriesRepository
import javax.inject.Inject

class DeleteSeriesRatingUseCase @Inject constructor(
    private val seriesRepository: SeriesRepository
) {
    suspend operator fun invoke(tvShowId: Int) =
        seriesRepository.deleteTvShowRating(tvShowId)
}
