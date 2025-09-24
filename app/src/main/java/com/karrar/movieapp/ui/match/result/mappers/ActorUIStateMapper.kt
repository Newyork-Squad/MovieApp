package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Actor
import com.karrar.movieapp.ui.match.MatchUiState
import javax.inject.Inject

class ActorUIStateMapper @Inject constructor() : Mapper<Actor, MatchUiState.ActorUiState> {
    override fun map(input: Actor): MatchUiState.ActorUiState {
        return MatchUiState.ActorUiState(
            id = input.actorID,
            imageUrl = input.actorImage,
            name = input.actorName,
            characterName = input.characterName
        )
    }
}