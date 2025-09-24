package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Actor
import com.karrar.movieapp.ui.match.questions.MatchQuestionUiState
import javax.inject.Inject

class ActorUIStateMapper @Inject constructor() : Mapper<Actor, MatchQuestionUiState.ActorUiState> {
    override fun map(input: Actor): MatchQuestionUiState.ActorUiState {
        return MatchQuestionUiState.ActorUiState(
            id = input.actorID,
            imageUrl = input.actorImage,
            name = input.actorName,
            characterName = input.characterName
        )
    }
}