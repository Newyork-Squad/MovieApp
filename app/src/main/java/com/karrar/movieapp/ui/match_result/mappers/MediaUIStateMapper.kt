package com.karrar.movieapp.ui.match_result.mappers

import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.ui.match_result.MatchResultUiState
import com.karrar.movieapp.ui.models.MediaUiState
import javax.inject.Inject

class MediaUIStateMapper @Inject constructor() : Mapper<Media, MatchResultUiState.MediaUiState> {
    override fun map(input: Media): MatchResultUiState.MediaUiState {
        return MatchResultUiState.MediaUiState(
            id = input.mediaID,
            imageUrl = input.mediaImage,
            mediaTitle = input.mediaName,
            mediaRate = input.mediaRate,
            mediaImage = input.mediaImage
        )
    }
}