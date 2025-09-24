package com.karrar.movieapp.domain.mappers.movie

import com.karrar.movieapp.data.local.database.entity.movie.MovieGenreEntity
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Genre
import javax.inject.Inject

class MovieGenreMapper @Inject constructor() : Mapper<MovieGenreEntity, Genre> {
    override fun map(input: MovieGenreEntity): Genre {
        return Genre(
            genreID = input.id,
            genreName = input.name,
            visitCount = input.visitCount
        )
    }
}