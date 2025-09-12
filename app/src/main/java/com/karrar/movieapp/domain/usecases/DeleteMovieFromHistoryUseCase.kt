package com.karrar.movieapp.domain.usecases

import com.karrar.movieapp.data.local.database.entity.WatchHistoryEntity
import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class DeleteMovieFromHistoryUseCase @Inject constructor (
    private val movieRepository: MovieRepository,

) {
   suspend operator fun invoke(movie: WatchHistoryEntity) {
       movieRepository.deleteMovieFromHistory(movie)
}}