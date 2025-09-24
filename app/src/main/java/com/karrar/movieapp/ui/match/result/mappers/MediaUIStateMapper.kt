package com.karrar.movieapp.ui.match.result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.ui.match.questions.MatchQuestionUiState
import javax.inject.Inject

class MediaUIStateMapper @Inject constructor() : Mapper<Media, MatchQuestionUiState.MediaUiState> {
    override fun map(input: Media): MatchQuestionUiState.MediaUiState {
        return MatchQuestionUiState.MediaUiState(
            id = input.mediaID,
            imageUrl = input.mediaImage,
            mediaTitle = input.mediaName,
            mediaRate = input.mediaRate,
            mediaImage = input.mediaImage
        )
    }
}