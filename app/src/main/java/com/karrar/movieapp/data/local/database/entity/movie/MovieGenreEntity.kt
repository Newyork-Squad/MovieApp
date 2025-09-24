package com.karrar.movieapp.data.local.database.entity.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MOVIE_GENRE_TABLE")
data class MovieGenreEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val visitCount: Int = 0,
)