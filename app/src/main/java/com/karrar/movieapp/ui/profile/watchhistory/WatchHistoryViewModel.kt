package com.karrar.movieapp.ui.profile.watchhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.mappers.WatchHistoryMapper
import com.karrar.movieapp.domain.usecases.DeleteMovieFromHistoryUseCase
import com.karrar.movieapp.domain.usecases.GetWatchHistoryUseCase
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchHistoryViewModel @Inject constructor(
    private val getWatchHistoryUseCase: GetWatchHistoryUseCase,
    private val deleteMovieFromHistoryUseCase: DeleteMovieFromHistoryUseCase,
    private val watchHistoryMapper: WatchHistoryMapper
) : ViewModel(), WatchHistoryInteractionListener,WatchHistoryListener {

    private val _uiState = MutableStateFlow(WatchHistoryUiState())
    val uiState = _uiState.asStateFlow()

    private val _deleteButtonVisibility = MutableStateFlow(false)
    val deleteButtonVisibility = _deleteButtonVisibility.asStateFlow()

    private val _cardVisibility = MutableStateFlow(true) // true means visible
    val cardVisibility = _cardVisibility.asStateFlow()


    private val _watchHistoryUIEvent: MutableStateFlow<Event<WatchHistoryUIEvent?>> =
        MutableStateFlow(Event(null))
    val watchHistoryUIEvent = _watchHistoryUIEvent.asStateFlow()

    init {
        getWatchHistoryData()
    }

    private fun getWatchHistoryData() {
        viewModelScope.launch {
            try {
                getWatchHistoryUseCase().collect { list ->
                    _uiState.update { watchHistoryUiState ->
                        watchHistoryUiState.copy(allMedia = list.map { watchHistoryMapper.map(it) })
                    }
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(error = listOf(Error(400, t.message.toString()))) }
            }

        }
    }

    override fun onClickMovie(item: MediaHistoryUiState) {
        if (item.mediaType.equals(Constants.MOVIE, true)) {
            _watchHistoryUIEvent.update { Event(WatchHistoryUIEvent.MovieEvent(item.id)) }
        } else {
            _watchHistoryUIEvent.update { Event(WatchHistoryUIEvent.TVShowEvent(item.id)) }
        }
    }

    override fun onClickToExploreScreen() {
        _watchHistoryUIEvent.update { Event(WatchHistoryUIEvent.ToExploreScreen) }
    }

    override fun onDeleteClick(item: MediaHistoryUiState) {
        viewModelScope.launch {
            deleteMovieFromHistoryUseCase(watchHistoryMapper.map(item)) // Convert to WatchHistoryEntity and call use case

            _uiState.update { currentState ->
                val updatedList = currentState.allMedia.filter { it.id != item.id }
                currentState.copy(allMedia = updatedList)
            }

        }
    }


    fun showDeleteButton(position: Int) {
        _deleteButtonVisibility.value = true
    }

    fun closeInfoCard() {
        _cardVisibility.value = false
        _uiState.update { currentState ->
            currentState.copy(isVisible = false)
        }
    }
}