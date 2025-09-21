package com.karrar.movieapp.domain.mappers.movie

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.remote.response.RatedMoviesDto
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Rated
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.convertToDayMonthYearFormat
import javax.inject.Inject

class RatedMoviesMapper @Inject constructor() : Mapper<RatedMoviesDto, Rated> {
    override fun map(input: RatedMoviesDto): Rated {
        return Rated(
            id = input.id ?: 0,
            title = input.title ?: input.originalTitle ?: "",
            posterPath = BuildConfig.IMAGE_BASE_PATH + input.backdropPath,
            rating = input.rating ?: 0F,
            releaseDate = input.releaseDate?.convertToDayMonthYearFormat() ?: "",
            mediaType = Constants.MOVIE,
            genres = input.genreIds?.filterNotNull() ?: emptyList()
        )
    }
}