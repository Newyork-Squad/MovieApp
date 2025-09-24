package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Crew
import com.karrar.movieapp.ui.match.questions.MatchQuestionUiState
import javax.inject.Inject

class CrewUIStateMapper @Inject constructor() : Mapper<Crew, MatchQuestionUiState.CrewUIState> {
    override fun map(input: Crew): MatchQuestionUiState.CrewUIState {
        return MatchQuestionUiState.CrewUIState(
            name = input.crewMemberName,
            job = input.crewMemberJob
        )
    }
}