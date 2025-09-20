package com.karrar.movieapp.domain.mappers.movie

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.local.database.entity.movie.UserMatchingMovieEntity
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import javax.inject.Inject

class UserMatchingMovieMapper @Inject constructor() : Mapper<UserMatchingMovieEntity, Media> {
    override fun map(input: UserMatchingMovieEntity): Media {
        return Media(
            mediaID = input.id,
            mediaName = input.title,
            mediaImage = BuildConfig.IMAGE_BASE_PATH + input.imageUrl,
            mediaRate = input.movieRate,
            mediaDate = "",
            mediaType = MediaType.MOVIE.value,
        )
    }
}