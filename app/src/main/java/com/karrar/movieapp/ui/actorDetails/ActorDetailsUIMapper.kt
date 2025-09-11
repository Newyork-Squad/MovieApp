package com.karrar.movieapp.ui.actorDetails

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.ActorDetails
import com.karrar.movieapp.utilities.convertToDayMonthYearFormat
import javax.inject.Inject

class ActorDetailsUIMapper @Inject constructor() : Mapper<ActorDetails, ActorDetailsUIState> {
    override fun map(input: ActorDetails): ActorDetailsUIState {
        val birthdayDate = input.actorBirthday
            .convertToDayMonthYearFormat("yyyy-MM-dd", "MMM dd, yyyy")?: "-"

        return ActorDetailsUIState(
            name = input.actorName,
            imageUrl = input.actorImage,
            gender = input.actorGender,
            birthday = birthdayDate,
            biography = input.actorBiography,
            placeOfBirth = input.actorPlaceOfBirth,
            knownFor = input.knownForDepartment,
        )
    }
}