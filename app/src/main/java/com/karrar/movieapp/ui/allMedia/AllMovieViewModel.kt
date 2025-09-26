package com.karrar.movieapp.ui.allMedia

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import com.karrar.movieapp.domain.usecases.GetGenreListUseCase
import com.karrar.movieapp.domain.usecases.GetMediaByGenreIDUseCase
import com.karrar.movieapp.domain.usecases.allMedia.CheckIfMediaIsSeriesUseCase
import com.karrar.movieapp.domain.usecases.allMedia.GetMediaByTypeUseCase
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.mappers.MediaUiMapper
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMovieViewModel @Inject constructor(
    state: SavedStateHandle,
    private val checkIfMediaIsSeriesUseCase: CheckIfMediaIsSeriesUseCase,
    private val getMediaByType: GetMediaByTypeUseCase,
    private val mediaUiMapper: MediaUiMapper,
    private val getGenreListUseCase: GetGenreListUseCase,
    private val getMediaByGenreIDUseCase: GetMediaByGenreIDUseCase,
) : BaseViewModel(), MediaInteractionListener {

    val args = AllMovieFragmentArgs.fromSavedStateHandle(state)

    private val _uiState = MutableStateFlow(AllMediaUiState())
    val uiState = _uiState.asStateFlow()

    private val _mediaUIEvent = MutableStateFlow<Event<MediaUIEvent>?>(null)
    val mediaUIEvent = _mediaUIEvent.asStateFlow()

    private val _isGrid = MutableStateFlow(true)
    val isGrid = _isGrid.asStateFlow()

    init {
        getData()
    }

    override fun getData() {
        viewModelScope.launch {
            try {
                val mediaTypeStr = if (checkIfMediaIsSeriesUseCase(args.type)) "tv" else "movie"

                val genreListId =
                    if (mediaTypeStr == "movie") Constants.MOVIE_CATEGORIES_ID else Constants.TV_CATEGORIES_ID
                val allGenres = getGenreListUseCase(genreListId)

                val allMediaItems = when (args.idType) {
                    com.karrar.movieapp.ui.home.homeUiState.IdType.GENRE -> {
                        getMediaByGenreIDUseCase(Constants.MOVIE_CATEGORIES_ID, args.id)
                    }
                    com.karrar.movieapp.ui.home.homeUiState.IdType.ACTOR -> {
                        getMediaByType(args.type, args.id)
                    }
                }.map { pager ->
                    pager.map { media ->
                        val baseUi = mediaUiMapper.map(media)
                        val genreNames = media.genresIds.mapNotNull { id ->
                            allGenres.find { it.genreID == id }?.genreName
                        }
                        baseUi.copy(genres = genreNames)
                    }
                }

                _uiState.update { it.copy(allMedia = allMediaItems, error = emptyList()) }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(error = listOf(Error(404, t.message ?: "Unknown error")))
                }
            }
        }
    }

    override fun onClickMedia(mediaId: Int) {
        val event = if (checkIfMediaIsSeriesUseCase(args.type)) {
            MediaUIEvent.ClickSeriesEvent(mediaId)
        } else {
            MediaUIEvent.ClickMovieEvent(mediaId)
        }
        _mediaUIEvent.update { Event(event) }
    }

    fun setErrorUiState(loadStates: CombinedLoadStates) {
        val errorList = when (loadStates.refresh) {
            is LoadState.Error -> listOf(Error(404, "Error loading data"))
            else -> emptyList()
        }
        _uiState.update { it.copy(error = errorList) }
    }

    fun toggleGridMode() {
        _isGrid.value = !_isGrid.value
    }

    fun setGridMode(grid: Boolean) {
        _isGrid.value = grid
    }
}


