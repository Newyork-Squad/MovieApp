package com.karrar.movieapp.domain.usecases

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.ListMapper
import com.karrar.movieapp.domain.mappers.actor.ActorImagesMapper
import javax.inject.Inject

class GetActorImagesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val actorImagesMapper: ActorImagesMapper,
) {
    suspend operator fun invoke(
        actorId: Int
    ): List<String> {
        val actorImages = movieRepository.getActorImages(actorId = actorId)
        return ListMapper(actorImagesMapper).mapList(actorImages?.profiles)
    }
}