package com.karrar.movieapp.domain.mappers.movie

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.local.database.entity.movie.RecentMovieViewedEntity
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import javax.inject.Inject

class RecentMovieViewedMapper  @Inject constructor() : Mapper<RecentMovieViewedEntity, Media> {
    override fun map(input: RecentMovieViewedEntity): Media {
        return Media(
            mediaID = input.id,
            mediaName = input.movieName,
            mediaImage = BuildConfig.IMAGE_BASE_PATH + input.movieImageUrl,
            mediaRate = input.movieRate,
            mediaDate = "",
            mediaType = MediaType.MOVIE.value,
        )
    }
}
