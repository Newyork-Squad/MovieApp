package com.karrar.movieapp.data.local.mappers.movie

import com.karrar.movieapp.data.local.database.entity.movie.UserMatchingMovieEntity
import com.karrar.movieapp.data.remote.response.MovieDto
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class UserMatchingMovieMapper @Inject constructor() : Mapper<MovieDto, UserMatchingMovieEntity> {
    override fun map(input: MovieDto): UserMatchingMovieEntity {
        return UserMatchingMovieEntity(
            id = input.id ?: 0,
            title = input.title ?: input.originalTitle ?: "",
            imageUrl = input.posterPath ?: "",
            movieRate = input.voteAverage?.toFloat() ?: 0.0f,
        )
    }
}