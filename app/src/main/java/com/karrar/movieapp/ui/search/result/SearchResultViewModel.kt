package com.karrar.movieapp.ui.search.result

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.cachedIn
import androidx.paging.map
import com.karrar.movieapp.domain.usecases.search.GetSearchForActorUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForKeywordUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForMovieUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForSeriesUserCase
import com.karrar.movieapp.domain.usecases.search.PostSaveSearchResultUseCase
import com.karrar.movieapp.ui.allMedia.Error
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.search.adapters.ActorSearchInteractionListener
import com.karrar.movieapp.ui.search.adapters.MediaSearchInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaTypes
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState
import com.karrar.movieapp.ui.search.uiStatMapper.SearchKeywordUIStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchMediaUIStateMapper
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchMediaUIStateMapper: SearchMediaUIStateMapper,
    private val searchKeywordMapper: SearchKeywordUIStateMapper,
    private val getSearchForMovieUseCase: GetSearchForMovieUseCase,
    private val getSearchForSeriesUserCase: GetSearchForSeriesUserCase,
    private val getSearchForActorUseCase: GetSearchForActorUseCase,
    private val getSearchForKeywordUseCase: GetSearchForKeywordUseCase,
    private val postSaveSearchResultUseCase: PostSaveSearchResultUseCase
) : BaseViewModel(), MediaSearchInteractionListener, ActorSearchInteractionListener {

    private val _uiState = MutableStateFlow(SearchResultUIState(searchTypes = MediaTypes.MOVIE))
    val uiState = _uiState.asStateFlow()

    private val _searchResultEvent = MutableStateFlow(Event<SearchResultEvent?>(null))
    val searchResultEvent = _searchResultEvent.asStateFlow()

    private val _isGrid = MutableStateFlow(true)
    val isGrid: StateFlow<Boolean> = _isGrid.asStateFlow()

    private val _showToggle = MutableStateFlow(false)
    val showToggle = _showToggle.asStateFlow()

    private var searchJob: Job? = null

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    override fun getData() {
        if (_uiState.value.searchInput.isNotBlank()) {
            fetchResults()
        }
    }

    fun setSearchInput(query: String) {
        _uiState.update { it.copy(searchInput = query) }
        fetchResults()
    }

    fun setMediaType(type: MediaTypes) {
        if (_uiState.value.searchTypes != type) {
            _uiState.update { it.copy(searchTypes = type) }
            fetchResults()
        }
    }

    private fun fetchResults() {
        searchJob?.cancel()
        val input = _uiState.value.searchInput
        if (input.isBlank()) return

        _uiState.update { it.copy(loading = true, error = emptyList(), isEmpty = false) }

        searchJob = viewModelScope.launch {
            when (_uiState.value.searchTypes) {
                MediaTypes.MOVIE -> {
                    getSearchForMovieUseCase(input)
                        .map { paging -> paging.map(searchMediaUIStateMapper::map) }
                        .cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _uiState.update { it.copy(searchResult = pagingData, loading = false) }
                        }
                }
                MediaTypes.TVS_SHOW -> {
                    getSearchForSeriesUserCase(input)
                        .map { paging -> paging.map(searchMediaUIStateMapper::map) }
                        .cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _uiState.update { it.copy(searchResult = pagingData, loading = false) }
                        }
                }
                MediaTypes.ACTOR -> {
                    getSearchForActorUseCase(input)
                        .map { paging -> paging.map(searchMediaUIStateMapper::map) }
                        .cachedIn(viewModelScope)
                        .collect { pagingData ->
                            _uiState.update { it.copy(searchResult = pagingData, loading = false) }
                        }
                }
                MediaTypes.KEYWORD -> Unit
            }
        }
    }

    override fun onClickMediaResult(media: MediaUIState) {
        _searchResultEvent.update { Event(SearchResultEvent.ClickMedia(media)) }
    }

    override fun onClickActorResult(personID: Int, name: String) {
        _searchResultEvent.update { Event(SearchResultEvent.ClickActor(personID)) }
    }

    fun setErrorState(loadState: CombinedLoadStates, itemCount: Int) {
        when (loadState.refresh) {
            is LoadState.Loading -> _uiState.update { it.copy(loading = true, error = emptyList()) }
            is LoadState.Error -> _uiState.update { it.copy(loading = false, error = listOf(Error(404, ""))) }
            is LoadState.NotLoading -> _uiState.update { it.copy(isEmpty = itemCount < 1, loading = false, error = emptyList()) }
        }
    }
    fun setGridMode(grid: Boolean) {
        _isGrid.value = grid
    }

    fun toggleGridMode() = setGridMode(!_isGrid.value)

    fun setToggleVisibility(visible: Boolean) {
        _showToggle.value = visible
    }

    fun onClickBack() {
        _searchResultEvent.update { Event(SearchResultEvent.ClickBack) }
    }
    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }
}
