package com.karrar.movieapp.domain.usecases.home.getData.movie

import androidx.paging.PagingData
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.domain.usecases.GetMediaByGenreIDUseCase
import com.karrar.movieapp.domain.usecases.GetTopVisitedMovieGenreUseCase
import com.karrar.movieapp.utilities.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetMoviesMatchingUserVibeUseCase @Inject constructor(
    private val getTopVisitedMovieGenreUseCase: GetTopVisitedMovieGenreUseCase,
    private val getMediaByGenreIDUseCase: GetMediaByGenreIDUseCase,
) {
    suspend operator fun invoke(): Flow<PagingData<Media>> {
        val topGenre = getTopVisitedMovieGenreUseCase().last()
        return getMediaByGenreIDUseCase(Constants.MOVIE_CATEGORIES_ID, topGenre.genreID)
    }
}