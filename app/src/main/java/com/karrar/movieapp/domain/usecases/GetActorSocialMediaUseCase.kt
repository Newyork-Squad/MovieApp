package com.karrar.movieapp.domain.usecases

import com.karrar.movieapp.data.repository.MovieRepository
import com.karrar.movieapp.domain.mappers.actor.ActorSocialMediaMapper
import javax.inject.Inject

class GetActorSocialMediaUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
    private val socialMediaMapper: ActorSocialMediaMapper
) {
    suspend operator fun invoke(
        actorId: Int
    ): HashMap<String, String> {
        val socialMediaResponse = movieRepository.getActorSocialMediaIDs(actorId = actorId)
        if (socialMediaResponse == null) {
            return hashMapOf()
        }
        return socialMediaMapper.map(socialMediaResponse)
    }
}