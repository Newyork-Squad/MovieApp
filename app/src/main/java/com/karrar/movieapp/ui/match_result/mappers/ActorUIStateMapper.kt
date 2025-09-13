package com.karrar.movieapp.ui.match_result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Actor
import com.karrar.movieapp.ui.match_result.MatchResultUiState
import javax.inject.Inject

class ActorUIStateMapper @Inject constructor() : Mapper<Actor, MatchResultUiState.ActorUiState> {
    override fun map(input: Actor): MatchResultUiState.ActorUiState {
        return MatchResultUiState.ActorUiState(
            id = input.actorID,
            imageUrl = input.actorImage,
            name = input.actorName,
            characterName = input.characterName
        )
    }
}