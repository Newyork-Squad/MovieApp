package com.karrar.movieapp.domain.mappers

import com.karrar.movieapp.data.remote.response.CrewDto
import com.karrar.movieapp.domain.models.Crew
import javax.inject.Inject

class CrewMapper @Inject constructor() : Mapper<CrewDto, Crew> {
    override fun map(input: CrewDto): Crew {
        return Crew(
            id = input.id ?: 0,
            crewName = input.name ?: "unknown",
            job = input.job ?: "unknown",
        )
    }
}
