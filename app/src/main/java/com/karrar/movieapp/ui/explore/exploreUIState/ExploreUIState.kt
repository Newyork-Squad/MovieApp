package com.karrar.movieapp.ui.explore.exploreUIState

import androidx.paging.PagingData
import com.karrar.movieapp.ui.category.uiState.GenreUIState
import com.karrar.movieapp.ui.category.uiState.MediaUIState
import com.karrar.movieapp.utilities.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow


data class ExploreUIState(
    val genre: List<GenreUIState> = emptyList(),
    val selectedCategoryID: Int = Constants.FIRST_CATEGORY_ID,
    val media: Flow<PagingData<MediaUIState>> = emptyFlow(),
    val selectedMediaId: Int = Constants.MOVIE_CATEGORIES_ID,
    val selectedGenreId: Int? = null,
    val isGrid: Boolean = true,
    val isLoading: Boolean = false,
    val error: List<ErrorUIState> = emptyList()
)
