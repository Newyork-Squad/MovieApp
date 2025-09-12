package com.karrar.movieapp.domain.usecases.mylist

import com.karrar.movieapp.data.remote.response.DefaultResponse
import com.karrar.movieapp.data.repository.MovieRepository
import javax.inject.Inject

class RemoveMovieFromCollectionUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        collectionId: String,
        movieId: Int
    ): DefaultResponse? {
        return repository.removeMovieFromCollection(sessionId, collectionId, movieId)
    }
}