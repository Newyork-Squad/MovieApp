package com.karrar.movieapp.data.local.mappers.movie

import com.karrar.movieapp.data.local.database.entity.movie.RecentMovieViewedEntity
import com.karrar.movieapp.data.remote.response.movie.MovieDetailsDto
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class RecentMovieViewedMapper @Inject constructor() :
    Mapper<MovieDetailsDto, RecentMovieViewedEntity> {
    override fun map(input: MovieDetailsDto): RecentMovieViewedEntity {
       return RecentMovieViewedEntity(
            id = input.id ?: 0,
            movieName = input.originalTitle ?: "",
            movieImageUrl = input.posterPath ?: "",
            movieRate = input.voteAverage?.toFloat() ?: 0f,
        )
    }
}