package com.karrar.movieapp.ui.myList.listDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.GetGenreListUseCase
import com.karrar.movieapp.domain.usecases.GetSessionIDUseCase
import com.karrar.movieapp.domain.usecases.mylist.GetMyMediaListDetailsUseCase
import com.karrar.movieapp.domain.usecases.mylist.RemoveMovieFromCollectionUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.category.uiState.ErrorUIState
import com.karrar.movieapp.ui.myList.listDetails.listDetailsUIState.ListDetailsUIEvent
import com.karrar.movieapp.ui.myList.listDetails.listDetailsUIState.ListDetailsUIState
import com.karrar.movieapp.ui.myList.listDetails.listDetailsUIState.SavedMediaUIState
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    private val getMyMediaListDetailsUseCase: GetMyMediaListDetailsUseCase,
    private val getGenreListUseCase: GetGenreListUseCase,
    private val removeMovieFromCollectionUseCase: RemoveMovieFromCollectionUseCase,
    private val mediaUIStateMapper: MediaUIStateMapper,
    private val getSessionIDUseCase: GetSessionIDUseCase,
    saveStateHandle: SavedStateHandle
) : BaseViewModel(), ListDetailsInteractionListener {

    val args = ListDetailsFragmentArgs.fromSavedStateHandle(saveStateHandle)

    private val _listDetailsUIState = MutableStateFlow(ListDetailsUIState())
    val listDetailsUIState = _listDetailsUIState.asStateFlow()

    private val _listDetailsUIEvent = MutableStateFlow<Event<ListDetailsUIEvent?>>(Event(null))
    val listDetailsUIEvent = _listDetailsUIEvent.asStateFlow()

    init {
        getData()
    }

    override fun getData() {
        _listDetailsUIState.update {
            it.copy(isLoading = true, isEmpty = false, error = emptyList())
        }

        viewModelScope.launch {
            try {
                val mediaList = getMyMediaListDetailsUseCase(args.id)

                val mediaType = mediaList.firstOrNull()?.mediaType ?: Constants.MOVIE
                val mediaIdForGenres = if (mediaType == Constants.MOVIE) Constants.MOVIE_CATEGORIES_ID else Constants.TV_CATEGORIES_ID

                val allGenres = getGenreListUseCase(mediaIdForGenres)

                val result = mediaList.map { media ->
                    val baseUi = mediaUIStateMapper.map(media)

                    val genreNames = media.genres.mapNotNull { genreId ->
                        allGenres.find { it.genreID == genreId }?.genreName
                    }

                    baseUi.copy(genres = genreNames)
                }


                _listDetailsUIState.update {
                    it.copy(
                        isLoading = false,
                        isEmpty = result.isEmpty(),
                        savedMedia = result
                    )
                }
            } catch (t: Throwable) {
                _listDetailsUIState.update {
                    it.copy(
                        isLoading = false,
                        error = listOf(ErrorUIState(0, t.message.toString()))
                    )
                }
            }
        }
    }
    fun removeMedia(item: SavedMediaUIState) {
        val sessionId = getSessionIDUseCase() ?: run {
            _listDetailsUIEvent.update { Event(ListDetailsUIEvent.ShowMessage("Session not found")) }
            return
        }

        viewModelScope.launch {
            try {
                val response = removeMovieFromCollectionUseCase(
                    sessionId = sessionId,
                    collectionId = args.id.toString(),
                    movieId = item.mediaID
                )

                if (response?.success == true) {
                    _listDetailsUIState.update { state ->
                        state.copy(
                            savedMedia = state.savedMedia.filter { it.mediaID != item.mediaID }
                        )
                    }
                } else {
                    _listDetailsUIEvent.update { Event(ListDetailsUIEvent.ShowMessage(response?.statusMessage ?: "Failed to remove")) }
                }
            } catch (t: Throwable) {
                _listDetailsUIEvent.update { Event(ListDetailsUIEvent.ShowMessage(t.message ?: "Error")) }
            }
        }
    }

    override fun onItemClick(item: SavedMediaUIState) {
        _listDetailsUIEvent.update { Event(ListDetailsUIEvent.OnItemSelected(item)) }
    }
}
