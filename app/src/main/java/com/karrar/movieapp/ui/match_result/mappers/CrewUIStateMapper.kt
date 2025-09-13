package com.karrar.movieapp.ui.match_result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Crew
import com.karrar.movieapp.ui.match_result.MatchResultUiState
import javax.inject.Inject

class CrewUIStateMapper @Inject constructor() : Mapper<Crew, MatchResultUiState.CrewUIState> {
    override fun map(input: Crew): MatchResultUiState.CrewUIState {
        return MatchResultUiState.CrewUIState(
            name = input.crewMemberName,
            job = input.crewMemberJob
        )
    }
}