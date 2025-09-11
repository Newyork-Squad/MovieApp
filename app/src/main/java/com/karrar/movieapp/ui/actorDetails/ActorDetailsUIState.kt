package com.karrar.movieapp.ui.actorDetails

import com.karrar.movieapp.ui.actorDetails.socialmedia.SocialMediaLinksUIState

data class ActorDetailsUIState(
    val name: String = "",
    val imageUrl: String = "",
    val gender: String = "",
    val birthday: String = "",
    val placeOfBirth: String = "",
    val knownFor: String = "",
    val biography: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: List<Error> = emptyList(),
    val actorMovies: List<ActorMoviesUIState> = emptyList(),
    val actorSocialMediaLinks: SocialMediaLinksUIState = SocialMediaLinksUIState(),
    val actorImages: List<String> = emptyList()
)