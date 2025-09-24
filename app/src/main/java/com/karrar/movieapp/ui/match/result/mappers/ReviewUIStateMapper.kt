package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Review
import com.karrar.movieapp.ui.match.MatchUiState
import javax.inject.Inject

class ReviewUIStateMapper @Inject constructor() : Mapper<Review, MatchUiState.ReviewUIState> {
    override fun map(input: Review): MatchUiState.ReviewUIState {
        return MatchUiState.ReviewUIState(
            content = input.content,
            createDate = input.createDate,
            userImage = input.userImage,
            name = input.name,
            userName = input.userName,
            rating = input.rating
        )
    }
}