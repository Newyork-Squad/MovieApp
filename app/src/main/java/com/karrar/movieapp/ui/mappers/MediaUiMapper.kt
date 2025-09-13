package com.karrar.movieapp.ui.mappers


import com.karrar.movieapp.domain.mappers.Mapper
import com.karrar.movieapp.domain.models.Media
import com.karrar.movieapp.ui.models.MediaUiState
import com.karrar.movieapp.utilities.convertToMonthDayYearFormat
import javax.inject.Inject

class MediaUiMapper @Inject constructor() : Mapper<Media, MediaUiState> {
    override fun map(input: Media): MediaUiState {
        val formattedDate = if (input.mediaDate.isBlank()) {
            ""
        } else {
            try {
                input.mediaDate.convertToMonthDayYearFormat()
            } catch (e: Exception) {
                ""
            }
        }

        return MediaUiState(
            id = input.mediaID,
            imageUrl = input.mediaImage,
            mediaTitle = input.mediaName,
            mediaRate = input.mediaRate,
            mediaImage = input.mediaImage,
            mediaDate = formattedDate,
            genresIds = input.genresIds,
        )
    }
}
