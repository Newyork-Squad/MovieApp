package com.karrar.movieapp.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import com.karrar.movieapp.domain.mappers.search.SearchHistoryItemMapper
import com.karrar.movieapp.domain.usecases.GetWatchHistoryUseCase
import com.karrar.movieapp.domain.usecases.home.getData.ClearAllRecentViewedUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.ClearSearchHistoryUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.DeleteSearchHistoryItemUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.GetSearchForActorUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.GetSearchForMovieUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.GetSearchForSeriesUserCase
import com.karrar.movieapp.domain.usecases.searchUseCase.GetSearchHistoryUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.PostSaveSearchResultUseCase
import com.karrar.movieapp.ui.allMedia.Error
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.search.adapters.ActorSearchInteractionListener
import com.karrar.movieapp.ui.search.adapters.MediaSearchInteractionListener
import com.karrar.movieapp.ui.search.adapters.RecentViewedInteractionListener
import com.karrar.movieapp.ui.search.adapters.SearchHistoryInteractionListener
import com.karrar.movieapp.ui.search.adapters.SearchItemInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaSearchUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaTypes
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchItemUiState
import com.karrar.movieapp.ui.search.uiStatMapper.RecentMovieViewedUiStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchHistoryUIStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchMediaUIStateMapper
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
class SearchViewModel @Inject constructor(
    private val searchHistoryUIStateMapper: SearchHistoryUIStateMapper,
    private val searchMediaUIStateMapper: SearchMediaUIStateMapper,
    private val searchHistoryItemMapper: SearchHistoryItemMapper,
    private val getSearchForMovieUseCase: GetSearchForMovieUseCase,
    private val getSearchForSeriesUserCase: GetSearchForSeriesUserCase,
    private val getSearchForActorUseCase: GetSearchForActorUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val postSaveSearchResultUseCase: PostSaveSearchResultUseCase,
    private val getRecentViewedUseCase: GetWatchHistoryUseCase,
    private val recentMovieViewedUiStateMapper: RecentMovieViewedUiStateMapper,
    private val clearAllRecentViewedUseCase: ClearAllRecentViewedUseCase,
    private val deleteSearchHistoryItemUseCase: DeleteSearchHistoryItemUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
) : BaseViewModel(), MediaSearchInteractionListener, ActorSearchInteractionListener,
    SearchHistoryInteractionListener, RecentViewedInteractionListener,
    SearchItemInteractionListener {

    private val _uiState = MutableStateFlow(MediaSearchUIState())
    val uiState = _uiState.asStateFlow()

    private val _isGrid = MutableStateFlow(true)
    val isGrid: StateFlow<Boolean> = _isGrid.asStateFlow()

    private val _showToggle = MutableStateFlow(false)
    val showToggle = _showToggle.asStateFlow()

    private val _searchUIEvent = MutableStateFlow<Event<SearchUIEvent?>>(Event(null))
    val searchUIEvent = _searchUIEvent.asStateFlow()

    private val _searchSections = MutableStateFlow<List<SearchItemUiState>>(emptyList())
    val searchSections = _searchSections.asStateFlow()

    private val _isSearchFocused = MutableStateFlow(false)
    val isSearchFocused: StateFlow<Boolean> = _isSearchFocused.asStateFlow()


    init {
        getAllSearchHistory()
        getRecentViewed()
    }

    override fun getData() {
        _searchUIEvent.update { Event(SearchUIEvent.ClickRetryEvent) }
    }

    fun setSearchFocus(focused: Boolean) {
        _isSearchFocused.value = focused
    }

    private fun getRecentViewed() {
        viewModelScope.launch {
            try {
                getRecentViewedUseCase().collect { list ->
                    val items = list.map(recentMovieViewedUiStateMapper::map)
                    _uiState.update {
                        it.copy(
                            recentMovieViewed = items,
                            isLoading = false
                        )
                    }
                    updateSearchSections()
                }
            } catch (_: Throwable) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun getAllSearchHistory() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                getSearchHistoryUseCase().collect { list ->
                    _uiState.update {
                        it.copy(
                            searchHistory = list.map { item -> searchHistoryUIStateMapper.map(item) },
                            isLoading = false,
                            isEmpty = false
                        )
                    }
                    updateSearchSections()
                }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(error = listOf(Error(0, e.message.toString())))
                }
            }
        }
    }

    private fun updateSearchSections() {
        val sections = mutableListOf<SearchItemUiState>()
        val ui = _uiState.value

        if (ui.searchHistory.isNotEmpty()) {
            sections.add(SearchItemUiState.SearchItemHistory(ui.searchHistory))
        }
        if (ui.recentMovieViewed.isNotEmpty()) {
            sections.add(SearchItemUiState.RecentViewed(ui.recentMovieViewed))
        }

        _searchSections.value = sections.sortedBy { it.priority }
    }

    fun onSearchInputChange(searchTerm: CharSequence) {
        _uiState.update { it.copy(searchInput = searchTerm.toString(), isLoading = true) }
        viewModelScope.launch {
            when (_uiState.value.searchTypes) {
                MediaTypes.MOVIE -> onSearchForMovie()
                MediaTypes.TVS_SHOW -> onSearchForSeries()
                MediaTypes.ACTOR -> onSearchForActor()
            }
        }
    }


    fun onSearchForMovie() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchTypes = MediaTypes.MOVIE,
                    isLoading = false,
                    searchResult = getSearchForMovieUseCase(it.searchInput).map { pagingData ->
                        pagingData.map { item -> searchMediaUIStateMapper.map(item) }
                    }
                )
            }
        }
    }

    fun onSearchForSeries() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchTypes = MediaTypes.TVS_SHOW,
                    isLoading = false,
                    searchResult = getSearchForSeriesUserCase(it.searchInput).map { pagingData ->
                        pagingData.map { item -> searchMediaUIStateMapper.map(item) }
                    }
                )
            }
        }
    }

    fun onSearchForActor() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchTypes = MediaTypes.ACTOR,
                    isLoading = false,
                    searchResult = getSearchForActorUseCase(it.searchInput).map { pagingData ->
                        pagingData.map { item -> searchMediaUIStateMapper.map(item) }
                    }
                )
            }
        }
    }

    override fun onClickMediaResult(media: MediaUIState) {
        saveSearchResult(media.mediaID, media.mediaName)
        setSearchFocus(false)
        _searchUIEvent.update { Event(SearchUIEvent.ClickMediaEvent(media)) }
    }

    override fun onClickActorResult(personID: Int, name: String) {
        saveSearchResult(personID, name)
        setSearchFocus(false)
        _searchUIEvent.update { Event(SearchUIEvent.ClickActorEvent(personID)) }
    }

    private fun saveSearchResult(id: Int, name: String) {
        viewModelScope.launch { postSaveSearchResultUseCase(id, name) }
    }

    override fun onClickSearchHistory(name: String) {
        onSearchInputChange(name)
        setSearchFocus(false)
        _searchUIEvent.update { Event(SearchUIEvent.ClickSearchHistoryEvent) }
    }

    override fun onClickDeleteSearchHistoryItem(item: SearchHistoryUIState) {
        onDeleteSearchHistoryItem(item)
    }


    fun onClickBack() {
        _searchUIEvent.update { Event(SearchUIEvent.ClickBackEvent) }
    }

    fun onClickVoice(){
        _searchUIEvent.update { Event(SearchUIEvent.ClickVoiceEvent) }
    }

    fun setErrorUiState(combinedLoadStates: CombinedLoadStates, itemCount: Int) {
        when (combinedLoadStates.refresh) {
            is LoadState.Loading -> {
                _uiState.update {
                    it.copy(isLoading = true, error = emptyList(), isEmpty = false)
                }
            }

            is LoadState.Error -> {
                _uiState.update {
                    it.copy(isLoading = false, error = listOf(Error(404, "")), isEmpty = false)
                }
            }

            is LoadState.NotLoading -> {
                if (itemCount < 1) {
                    _uiState.update {
                        it.copy(
                            isEmpty = true,
                            isLoading = false,
                            error = emptyList()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isEmpty = false,
                            isLoading = false,
                            error = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun setGridMode(grid: Boolean) {
        _isGrid.value = grid
    }

    fun toggleGridMode() = setGridMode(!_isGrid.value)

    fun setToggleVisibility(visible: Boolean) {
        _showToggle.value = visible
    }

    override fun onClickRecentViewed(item: RecentMovieViewedUiState) {
        _searchUIEvent.update { Event(SearchUIEvent.ClickRecentViewedEvent(item)) }
    }

    override fun onClearAllRecentHistoryClicked() {
        viewModelScope.launch { clearAllRecentViewedUseCase() }
        getAllSearchHistory()
        getRecentViewed()
    }

    override fun onClearAllQueryHistoryClicked() {
        viewModelScope.launch { clearSearchHistoryUseCase() }
        getAllSearchHistory()
        getRecentViewed()
    }

    fun onDeleteSearchHistoryItem(item: SearchHistoryUIState) {
        viewModelScope.launch {
            try {
                deleteSearchHistoryItemUseCase(searchHistoryItemMapper.map(item))
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(error = listOf(Error(0, e.message.toString())))
                }
            }
        }
    }

    fun onClearSearchHistory() {
        viewModelScope.launch {
            try {
                clearSearchHistoryUseCase()
                _uiState.update { it.copy(searchHistory = emptyList()) }
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(error = listOf(Error(0, e.message.toString())))
                }
            }
        }
    }


}