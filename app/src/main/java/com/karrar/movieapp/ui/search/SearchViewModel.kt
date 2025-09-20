package com.karrar.movieapp.ui.search

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import com.karrar.movieapp.domain.mappers.search.SearchHistoryItemMapper
import com.karrar.movieapp.domain.usecases.GetWatchHistoryUseCase
import com.karrar.movieapp.domain.usecases.home.getData.ClearAllRecentViewedUseCase
import com.karrar.movieapp.domain.usecases.search.ClearAllSearchHistoryUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForActorUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForKeywordUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForMovieUseCase
import com.karrar.movieapp.domain.usecases.search.GetSearchForSeriesUserCase
import com.karrar.movieapp.domain.usecases.search.GetSearchHistoryUseCase
import com.karrar.movieapp.domain.usecases.search.PostSaveSearchResultUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.ClearSearchHistoryUseCase
import com.karrar.movieapp.domain.usecases.searchUseCase.DeleteSearchHistoryItemUseCase
import com.karrar.movieapp.ui.allMedia.Error
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.search.adapters.ActorSearchInteractionListener
import com.karrar.movieapp.ui.search.adapters.MediaSearchInteractionListener
import com.karrar.movieapp.ui.search.adapters.RecentViewedInteractionListener
import com.karrar.movieapp.ui.search.adapters.SearchHistoryInteractionListener
import com.karrar.movieapp.ui.search.adapters.SearchItemInteractionListener
import com.karrar.movieapp.ui.search.adapters.SuggestionsInteractionListener
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaSearchUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.MediaUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.RecentMovieViewedUiState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchHistoryUIState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchItemUiState
import com.karrar.movieapp.ui.search.mediaSearchUIState.SearchKeywordUIState
import com.karrar.movieapp.ui.search.uiStatMapper.RecentMovieViewedUiStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchHistoryUIStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchKeywordUIStateMapper
import com.karrar.movieapp.ui.search.uiStatMapper.SearchMediaUIStateMapper
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val clearAllSearchHistoryUseCase: ClearAllSearchHistoryUseCase,
    private val postSaveSearchResultUseCase: PostSaveSearchResultUseCase,
    private val getRecentViewedUseCase: GetWatchHistoryUseCase,
    private val recentMovieViewedUiStateMapper: RecentMovieViewedUiStateMapper,
    private val searchKeywordMapper: SearchKeywordUIStateMapper,
    private val clearAllRecentViewedUseCase: ClearAllRecentViewedUseCase,
    private val getSearchForKeywordUseCase: GetSearchForKeywordUseCase,
    private val deleteSearchHistoryItemUseCase: DeleteSearchHistoryItemUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
) : BaseViewModel(), MediaSearchInteractionListener, ActorSearchInteractionListener,
    SearchHistoryInteractionListener, RecentViewedInteractionListener,
    SearchItemInteractionListener, SuggestionsInteractionListener {

    private val _uiState = MutableStateFlow(MediaSearchUIState())
    val uiState = _uiState.asStateFlow()

    private val _searchUIEvent = MutableStateFlow<Event<SearchUIEvent?>>(Event(null))
    val searchUIEvent = _searchUIEvent.asStateFlow()

    private val _searchSections = MutableStateFlow<List<SearchItemUiState>>(emptyList())
    val searchSections = _searchSections.asStateFlow()


    init {
        getAllSearchHistory()
        getRecentViewed()
        updateSearchSections()
    }

    override fun getData() {
        _searchUIEvent.update { Event(SearchUIEvent.ClickRetryEvent) }
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
                    Log.d("SearchVM", "History Count = ${list.size}") // ✅ Debug log
                    list.forEach { Log.d("SearchVM", "History Item = $it") }
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
        val ui = uiState.value

        if (ui.searchInput.isNotBlank()) {
            val combinedSuggestions = ui.searchKeywordResult.map { pagingData ->
                pagingData.map { suggestion ->
                    val isHistory = ui.matchedHistory.any { it.keyword.equals(suggestion.keyword, ignoreCase = true) }
                    suggestion.copy(isFromHistory = isHistory)
                }
            }
            sections.add(SearchItemUiState.SuggestionsItems(combinedSuggestions))
        } else if (ui.searchHistory.isNotEmpty()) {
            sections.add(SearchItemUiState.SearchItemHistory(ui.searchHistory))
        }

        if (ui.recentMovieViewed.isNotEmpty()) {
            sections.add(SearchItemUiState.RecentViewed(ui.recentMovieViewed))
        }

        _searchSections.value = sections.sortedBy { it.priority }
    }




    fun onSearchInputChange(searchTerm: CharSequence) {
        _uiState.update { it.copy(searchInput = searchTerm.toString(), isLoading = true) }
        updateSearchSections()
        fetchKeywordSuggestions()
    }

    private fun fetchKeywordSuggestions() {
        viewModelScope.launch {
            val keyword = _uiState.value.searchInput.lowercase()

            val pagingFlow = getSearchForKeywordUseCase(keyword)
                .map { pagingData ->
                    pagingData.map { searchKeywordMapper.map(it) }
                }

            val matchedHistory = _uiState.value.searchHistory
                .filter { it.name.contains(keyword, ignoreCase = true) }
                .map { SearchKeywordUIState(it.name, isFromHistory = true) }
                .distinctBy { it.keyword }

            _uiState.update {
                it.copy(
                    searchKeywordResult = pagingFlow,
                    matchedHistory = matchedHistory,
                    isLoading = false
                )
            }

            updateSearchSections()
        }
    }




    override fun onClickMediaResult(media: MediaUIState) {
        _searchUIEvent.update { Event(SearchUIEvent.ClickMediaEvent(media)) }
    }

    override fun onClickActorResult(personID: Int, name: String) {
        _searchUIEvent.update { Event(SearchUIEvent.ClickActorEvent(personID)) }
    }

    private fun saveSearchResult(id: Int?, name: String) {
        viewModelScope.launch { postSaveSearchResultUseCase(id, name) }
    }

    override fun onClickSearchHistory(name: String) {
        _searchUIEvent.update { Event(SearchUIEvent.ClickSearchEvent(name)) }
        _uiState.update { it.copy(searchInput = name) }
        onSearchInputChange(name)
    }


    override fun onClearAllHistoryClicked() {
        viewModelScope.launch {
            clearAllSearchHistoryUseCase()
            getAllSearchHistory()
            getRecentViewed()
        }
    }

    override fun deleteHistoryItem(item: SearchHistoryUIState) {
        viewModelScope.launch {
            try {
                deleteSearchHistoryItemUseCase(item.entity)

                _uiState.update { currentState ->
                    val updatedHistory = currentState.searchHistory.filter { it.name != item.name }
                    currentState.copy(searchHistory = updatedHistory)
                }
                getAllSearchHistory()
                updateSearchSections()
            } catch (e: Exception) {
            }
        }
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


    override fun onClickRecentViewed(item: RecentMovieViewedUiState) {
        _searchUIEvent.update { Event(SearchUIEvent.ClickRecentViewedEvent(item)) }
    }

    override fun onClearAllClicked() {
        viewModelScope.launch { clearAllRecentViewedUseCase() }
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


    override fun onSuggestionsClicked(name: SearchKeywordUIState) {
        val existing = _uiState.value.searchHistory.any { it.name.equals(name.keyword, ignoreCase = true) }
        _searchUIEvent.update { Event(SearchUIEvent.ClickSearchEvent(name.keyword)) }
        if (!existing) saveSearchResult(null, name.keyword)
        _uiState.update { it.copy(searchInput = name.keyword) }
        onSearchInputChange(name.keyword)
    }



    override fun onSuggestionFill(name: SearchKeywordUIState) {
        _uiState.update { it.copy(searchInput = name.keyword) }
        _searchUIEvent.update { Event(SearchUIEvent.ClickSearchEvent(name.keyword)) }

        onSearchInputChange(name.keyword)
    }


}