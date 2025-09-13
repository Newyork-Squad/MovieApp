package com.karrar.movieapp.data.local.database.entity.series

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Recent_SERIES_VIEWED_TABLE")
data class RecentSeriesViewedEntity (
    @PrimaryKey val id: Int,
    val seriesName: String,
    val seriesImageUrl: String,
    val seriesRate: Float,
)
