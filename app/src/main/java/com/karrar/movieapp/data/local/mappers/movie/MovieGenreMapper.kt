package com.karrar.movieapp.data.local.mappers.movie

import com.karrar.movieapp.data.local.database.entity.movie.MovieGenreEntity
import com.karrar.movieapp.data.remote.response.genre.GenreDto
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class MovieGenreMapper @Inject constructor() : Mapper<GenreDto, MovieGenreEntity>{
    override fun map(input: GenreDto): MovieGenreEntity {
        return MovieGenreEntity(
            id = input.id ?: 0,
            name = input.name ?: "",
        )
    }
}