package com.karrar.movieapp.domain.mappers.actor

import com.karrar.movieapp.BuildConfig
import com.karrar.movieapp.data.remote.response.actor.ProfileDto
import com.karrar.movieapp.domain.mappers.Mapper
import javax.inject.Inject

class ActorImagesMapper @Inject constructor() : Mapper<ProfileDto, String> {
    override fun map(input: ProfileDto): String {
        return BuildConfig.IMAGE_BASE_PATH + input.filePath
    }
}