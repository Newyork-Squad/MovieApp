package com.karrar.movieapp.domain.mappers.movie

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.remote.response.MovieDto
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.utilities.Constants
import javax.inject.Inject

class MovieMapper @Inject constructor() : Mapper<MovieDto, Media> {
    override fun map(input: MovieDto): Media {
        return Media(
            mediaID = input.id ?: 0,
            mediaImage = BuildConfig.IMAGE_BASE_PATH + input.posterPath,
            mediaType = MediaType.MOVIE.value,
            mediaName = input.originalTitle ?: "",
            mediaDate = input.releaseDate ?: "",
            mediaRate = input.voteAverage?.toFloat() ?: 0f,
            genresIds = input.genreIds ?: emptyList(),
        )
    }
}