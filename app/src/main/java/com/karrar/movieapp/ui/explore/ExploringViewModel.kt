package com.karrar.movieapp.ui.explore

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import com.karrar.movieapp.domain.usecases.GetGenreListUseCase
import com.karrar.movieapp.domain.usecases.GetMediaByGenreIDUseCase
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.explore.GenresInteractionListener
import com.karrar.movieapp.ui.category.GenreUIStateMapper
import com.karrar.movieapp.ui.category.MediaUIStateMapper
import com.karrar.movieapp.ui.explore.exploreUIState.ErrorUIState
import com.karrar.movieapp.ui.explore.exploreUIState.ExploreUIState
import com.karrar.movieapp.ui.explore.exploreUIState.ExploringUIEvent
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExploringViewModel @Inject constructor(
    private val getMediaByGenreUseCase: GetMediaByGenreIDUseCase,
    private val getGenresUseCase: GetGenreListUseCase,
    private val mediaUIStateMapper: MediaUIStateMapper,
    private val genreUIStateMapper: GenreUIStateMapper,
) : BaseViewModel(), MediaInteractionListener, GenresInteractionListener {

    private val _uiState = MutableStateFlow(ExploreUIState())
    val uiState: StateFlow<ExploreUIState> = _uiState

    private val _exploringUIEvent: MutableStateFlow<Event<ExploringUIEvent>?> = MutableStateFlow(null)
    val exploringUIEvent= _exploringUIEvent.asStateFlow()

    init {
        setMediaType(Constants.MOVIE_CATEGORIES_ID)
    }

    override fun getData() {
        val mediaId = _uiState.value.selectedMediaId
        val catId = _uiState.value.selectedCategoryID
        loadGenres(mediaId)
        loadMedia(mediaId, catId)
    }

    fun setMediaType(mediaId: Int) {
        _uiState.update {
            it.copy(
                isLoading = true,
                error = emptyList(),
                selectedMediaId = mediaId,
                selectedCategoryID = Constants.FIRST_CATEGORY_ID
            )
        }
        loadGenres(mediaId)
        loadMedia(mediaId, Constants.FIRST_CATEGORY_ID)
    }

    private fun loadGenres(mediaId: Int) {
        viewModelScope.launch {
            try {
                val list = getGenresUseCase(mediaId).map { genreUIStateMapper.map(it) }
                _uiState.update { it.copy(genre = list, isLoading = false) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(error = listOf(ErrorUIState(-1, t.message.orEmpty()))) }
            }
        }
    }

    private fun loadMedia(mediaId: Int, categoryId: Int) {
        viewModelScope.launch {
            try {
                val paging = getMediaByGenreUseCase(mediaId, categoryId)
                    .map { pagingData -> pagingData.map { mediaUIStateMapper.map(it) } }
                _uiState.update { it.copy(isLoading = false, media = paging, error = emptyList()) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(isLoading = false, error = listOf(ErrorUIState(-1, t.message.orEmpty()))) }
            }
        }
    }

    fun onClickSearch() {
        _exploringUIEvent.update { Event(ExploringUIEvent.SearchEvent) }
    }

    override fun onGenreSelected(genreId: Int) {
        _uiState.update { it.copy(selectedCategoryID = genreId, isLoading = true) }
        loadMedia(_uiState.value.selectedMediaId, genreId)
    }

    override fun onClickMedia(mediaId: Int) {
        val isTv = _uiState.value.selectedMediaId == Constants.TV_CATEGORIES_ID
        _exploringUIEvent.update { Event(ExploringUIEvent.OpenDetails(mediaId, isTv)) }
    }

    fun setErrorUiState(combinedLoadStates: CombinedLoadStates) {
        when (combinedLoadStates.refresh) {
            is LoadState.NotLoading -> {
                _uiState.update {
                    it.copy(isLoading = false, error = emptyList())
                }
            }
            LoadState.Loading -> {
                _uiState.update {
                    it.copy(isLoading = false, error = emptyList())
                }
            }
            is LoadState.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, error = listOf(ErrorUIState(404, "Error")))
                }
            }
        }
    }

}