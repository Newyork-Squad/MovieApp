package com.karrar.movieapp.data.local.mappers.series

import com.karrar.movieapp.data.local.database.entity.series.RecentSeriesViewedEntity
import com.karrar.movieapp.data.remote.response.tvShow.TvShowDetailsDto
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class RecentSeriesViewedMapper @Inject constructor() :
    Mapper<TvShowDetailsDto, RecentSeriesViewedEntity> {
    override fun map(input: TvShowDetailsDto): RecentSeriesViewedEntity {
        return RecentSeriesViewedEntity(
            id = input.id ?: 0,
            seriesName = input.name ?: "",
            seriesImageUrl = input.posterPath ?: "",
            seriesRate = input.voteAverage?.toFloat() ?: 0f,
        )
    }
}