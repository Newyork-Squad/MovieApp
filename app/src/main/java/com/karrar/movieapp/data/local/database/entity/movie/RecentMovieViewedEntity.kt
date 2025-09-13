package com.karrar.movieapp.data.local.database.entity.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Recent_MOVIE_VIEWED_TABLE")
data class RecentMovieViewedEntity (
    @PrimaryKey val id: Int,
    val movieName: String,
    val movieImageUrl: String,
    val movieRate: Float,
)
