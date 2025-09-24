package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Crew
import com.karrar.movieapp.ui.match.MatchUiState
import javax.inject.Inject

class CrewUIStateMapper @Inject constructor() : Mapper<Crew, MatchUiState.CrewUIState> {
    override fun map(input: Crew): MatchUiState.CrewUIState {
        return MatchUiState.CrewUIState(
            name = input.crewMemberName,
            job = input.crewMemberJob
        )
    }
}