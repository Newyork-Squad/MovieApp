package com.karrar.movieapp.ui.match_result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Review
import com.karrar.movieapp.ui.match_result.MatchResultUiState
import javax.inject.Inject

class ReviewUIStateMapper @Inject constructor() : Mapper<Review, MatchResultUiState.ReviewUIState> {
    override fun map(input: Review): MatchResultUiState.ReviewUIState {
        return MatchResultUiState.ReviewUIState(
            content = input.content,
            createDate = input.createDate,
            userImage = input.userImage,
            name = input.name,
            userName = input.userName,
            rating = input.rating
        )
    }
}