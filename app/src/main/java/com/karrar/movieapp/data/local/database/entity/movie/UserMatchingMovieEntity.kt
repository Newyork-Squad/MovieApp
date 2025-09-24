package com.karrar.movieapp.data.local.database.entity.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "USER_MATCHING_MOVIE_TABLE")
data class UserMatchingMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageUrl: String,
    val movieRate: Float,
)