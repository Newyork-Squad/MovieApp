package com.karrar.movieapp.domain.mappers.series

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.local.database.entity.series.RecentSeriesViewedEntity
import com.karrar.movieapp.domain.enums.MediaType
import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import javax.inject.Inject

class RecentSeriesViewedMapper  @Inject constructor() : Mapper<RecentSeriesViewedEntity, Media> {
    override fun map(input: RecentSeriesViewedEntity): Media {
        return Media(
            mediaID = input.id,
            mediaName = input.seriesName,
            mediaImage = BuildConfig.IMAGE_BASE_PATH + input.seriesImageUrl,
            mediaRate = input.seriesRate,
            mediaDate = "",
            mediaType = MediaType.MOVIE.value,
        )
    }
}
