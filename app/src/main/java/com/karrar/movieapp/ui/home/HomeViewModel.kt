package com.karrar.movieapp.ui.home

import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.enums.AllMediaType
import com.karrar.movieapp.domain.enums.HomeItemsType
import com.karrar.movieapp.domain.mappers.WatchHistoryMapper
import com.karrar.movieapp.domain.usecases.CheckIfLoggedInUseCase
import com.karrar.movieapp.domain.usecases.GetAccountDetailsUseCase
import com.karrar.movieapp.domain.usecases.home.HomeUseCasesContainer
import com.karrar.movieapp.domain.usecases.mylist.GetMyListUseCase
import com.karrar.movieapp.ui.adapters.ActorsInteractionListener
import com.karrar.movieapp.ui.adapters.MediaInteractionListener
import com.karrar.movieapp.ui.adapters.MovieInteractionListener
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.home.adapter.TVShowInteractionListener
import com.karrar.movieapp.ui.home.homeUiState.HomeUIEvent
import com.karrar.movieapp.ui.home.homeUiState.HomeUiState
import com.karrar.movieapp.ui.mappers.MediaUiMapper
import com.karrar.movieapp.ui.myList.CreatedListInteractionListener
import com.karrar.movieapp.ui.myList.CreatedListUIMapper
import com.karrar.movieapp.ui.myList.myListUIState.CreatedListUIState
import com.karrar.movieapp.ui.profile.ProfileUIState
import com.karrar.movieapp.ui.profile.watchhistory.MediaHistoryUiState
import com.karrar.movieapp.ui.profile.watchhistory.WatchHistoryInteractionListener
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCasesContainer: HomeUseCasesContainer,
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase,
    private val mediaUiMapper: MediaUiMapper,
    private val popularUiMapper: PopularUiMapper,
    private val watchHistoryMapper: WatchHistoryMapper,
    private val getMyListUseCase: GetMyListUseCase,
    private val createdListUIMapper: CreatedListUIMapper,
    private val checkIfLoggedInUseCase: CheckIfLoggedInUseCase,
) : BaseViewModel(), HomeInteractionListener, ActorsInteractionListener, MovieInteractionListener,
    MediaInteractionListener, TVShowInteractionListener, WatchHistoryInteractionListener,
    CreatedListInteractionListener {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _homeUIEvent = MutableStateFlow<Event<HomeUIEvent?>>(Event(null))
    val homeUIEvent = _homeUIEvent.asStateFlow()

    private val _profileDetailsUIState = MutableStateFlow(ProfileUIState())
    val profileDetailsUIState = _profileDetailsUIState.asStateFlow()

    private var lastRefreshTime = 0L

    init {
        getHomeData()
    }

    private fun getHomeData() {
        _homeUiState.update { it.copy(isLoading = true) }
        getProfileDetails()
        getUpcoming()
        getTopRatedTvShow()
        getRecentlyReleasedTvShow()
        getPopularMovies()
        getRecentlyViewed()
        getCollections()
        getMatchingMovies()
    }

    override fun getData() {
        getHomeData()
        _homeUiState.update { it.copy(error = emptyList()) }
    }

    fun refreshHomeData() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime < 1000) {
            return
        }
        lastRefreshTime = currentTime
        _homeUiState.update {
            HomeUiState().copy(isLoading = true)
        }
        getHomeData()
    }

    private fun getProfileDetails() {
        if (checkIfLoggedInUseCase()) {
            _profileDetailsUIState.update {
                it.copy(isLoggedIn = true, error = false)
            }

            viewModelScope.launch {
                try {
                    val accountDetails = getAccountDetailsUseCase()
                    _profileDetailsUIState.update {
                        it.copy(
                            name = accountDetails.name,
                            username = accountDetails.username,
                            isLoading = false
                        )
                    }
                } catch (t: Throwable) {
                    _profileDetailsUIState.update {
                        it.copy(isLoading = false, error = true)
                    }
                }
            }
        } else {
            _profileDetailsUIState.update {
                it.copy(isLoggedIn = false)
            }
        }
    }

    val displayName: StateFlow<String> = profileDetailsUIState.map { state ->
        if (state.isLoggedIn) {
            state.name.ifBlank { state.username }
        } else {
            "Home"
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        "Home"
    )

    private fun getPopularMovies() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getPopularMoviesUseCase().collect { list ->
                    if (list.isNotEmpty()) {
                        val items = list.map(popularUiMapper::map)
                        _homeUiState.update {
                            it.copy(
                                popularMovies = HomeItem.Slider(items),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun onError(message: String) {
        val errors = _homeUiState.value.error.toMutableList()
        errors.add(message)
        _homeUiState.update { it.copy(error = errors, isLoading = false) }
    }

    private fun getUpcoming() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getUpcomingMoviesUseCase().collect { list ->
                    if (list.isNotEmpty()) {
                        val items = list.map(mediaUiMapper::map)
                        _homeUiState.update {
                            it.copy(
                                upcomingMovies = HomeItem.Upcoming(items),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun getMatchingMovies() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getUpcomingMoviesUseCase().collect { list ->
                    if (list.isNotEmpty()) {
                        val items = list.reversed().map(mediaUiMapper::map)
                        _homeUiState.update {
                            it.copy(
                                matchedItems = HomeItem.MatchedItems(items),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun getTopRatedTvShow() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getTopRatedTvShowUseCase().collect { list ->
                    if (list.isNotEmpty()) {
                        val items = list.map(mediaUiMapper::map)
                        _homeUiState.update {
                            it.copy(
                                topRatedSeries = HomeItem.TopRatedTvShows(items),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun getRecentlyReleasedTvShow() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getAiringTodayUseCase().collect { list ->
                    if (list.isNotEmpty()) {
                        val items = list.map(mediaUiMapper::map)
                        _homeUiState.update {
                            it.copy(
                                recentlyReleasedSeries = HomeItem.RecentlyReleased(items),
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun getRecentlyViewed() {
        viewModelScope.launch {
            try {
                homeUseCasesContainer.getWatchHistoryUseCase().collect { list ->
                    val items = list.map(watchHistoryMapper::map)
                    _homeUiState.update {
                        it.copy(
                            recentlyViewed = HomeItem.RecentlyViewed(items),
                            isLoading = false
                        )
                    }
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    private fun getCollections() {
        if (!checkIfLoggedInUseCase()) {
            _homeUiState.update {
                it.copy(isLoading = false)
            }
            return
        }

        viewModelScope.launch {
            try {
                val items = getMyListUseCase().map { createdListUIMapper.map(it) }
                _homeUiState.update {
                    it.copy(
                        collections = HomeItem.Collections(items),
                        isLoading = false
                    )
                }
            } catch (th: Throwable) {
                onError(th.message.toString())
            }
        }
    }

    override fun onClickMovie(movieId: Int) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickMovieEvent(movieId)) }
    }

    override fun onClickActor(actorID: Int) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickActorEvent(actorID)) }
    }

    override fun onClickSeeAllMovie(homeItemsType: HomeItemsType) {
        val type = when (homeItemsType) {
            HomeItemsType.UPCOMING -> AllMediaType.UPCOMING
            HomeItemsType.TOP_RATED_TV_SHOWS -> AllMediaType.TOP_RATED
            HomeItemsType.RECENTLY_RELEASED -> AllMediaType.RECENTLY_RELEASED
            HomeItemsType.RECENTLY_VIEWED -> {
                onClickSeeAllRecentlyViewed()
                return
            }
            HomeItemsType.COLLECTIONS -> {
                onClickSeeAllCollections()
                return
            }
            HomeItemsType.NON -> AllMediaType.ACTOR_MOVIES
            HomeItemsType.MATCHES_YOUR_VIBE -> TODO()
        }
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeeAllMovieEvent(type)) }
    }

    override fun onClickSeeAllActors() {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeeAllActorEvent) }
    }

    override fun onClickSeeAllRecentlyViewed() {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeeAllRecentlyViewed) }
    }

    override fun onClickSeeAllCollections() {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeeAllCollections) }
    }

    override fun onClickMedia(mediaId: Int) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeriesEvent(mediaId)) }
    }

    override fun onClickTVShow(tVShowID: Int) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeriesEvent(tVShowID)) }
    }

    override fun onClickSeeTVShow(type: AllMediaType) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickSeeAllTVShowsEvent(type)) }
    }

    override fun onClickMovie(item: MediaHistoryUiState) {
        if (item.mediaType.equals(Constants.MOVIE, true)) {
            _homeUIEvent.update { Event(HomeUIEvent.ClickMovieEvent(item.id)) }
        } else {
            _homeUIEvent.update { Event(HomeUIEvent.ClickSeriesEvent(item.id)) }
        }
    }

    override fun onListClick(item: CreatedListUIState) {
        _homeUIEvent.update { Event(HomeUIEvent.ClickListEvent(item)) }
    }

    override fun onClickWhatShouldWatch() {
        _homeUIEvent.update { Event(HomeUIEvent.clickToMatchScreen) }
    }

    override fun onClickNeedMoreToWatch() {
        _homeUIEvent.update { Event(HomeUIEvent.clickToExploreScreen) }
    }
}