package com.karrar.movieapp.ui.profile.myratings

import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.usecases.GetGenreListUseCase
import com.karrar.movieapp.domain.usecases.GetListOfRatedUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.DeleteMovieRatingUseCase
import com.karrar.movieapp.domain.usecases.tvShowDetails.DeleteSeriesRatingUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRatingsViewModel @Inject constructor(
    private val getRatedUseCase: GetListOfRatedUseCase,
    private val ratedUIStateMapper: RatedUIStateMapper,
    private val getGenreListUseCase: GetGenreListUseCase,
    private val deleteMovieRatingUseCase: DeleteMovieRatingUseCase,
    private val deleteSeriesRatingUseCase: DeleteSeriesRatingUseCase,
) : BaseViewModel(), RatedMoviesInteractionListener {

    private val _ratedUiState = MutableStateFlow(MyRateUIState())
    val ratedUiState: StateFlow<MyRateUIState> = _ratedUiState

    private val _myRatingUIEvent: MutableStateFlow<Event<MyRatingUIEvent?>> =
        MutableStateFlow(Event(null))
    val myRatingUIEvent = _myRatingUIEvent.asStateFlow()

    private val _currentTab = MutableStateFlow(Constants.MOVIE)
    val currentTab: StateFlow<String> = _currentTab

    private var allRatedList: List<RatedUIState> = emptyList()

    init {
        getData()
    }

    override fun getData() {
        viewModelScope.launch {
            _ratedUiState.update { it.copy(isLoading = true) }
            try {
                // جلب rated list
                val ratedList = getRatedUseCase()

                val movieGenresMap = getGenreListUseCase(Constants.MOVIE_CATEGORIES_ID)
                    .associateBy { it.genreID } // Map<Int, Genre> للبحث السريع
                val seriesGenresMap = getGenreListUseCase(Constants.TV_CATEGORIES_ID)
                    .associateBy { it.genreID }

                allRatedList = ratedList.map { rated ->
                    val baseUi = ratedUIStateMapper.map(rated)
                    val genresNames = when (rated.mediaType) {
                        Constants.MOVIE -> rated.genres.mapNotNull { movieGenresMap[it]?.genreName }
                        Constants.TV_SHOWS -> rated.genres.mapNotNull { seriesGenresMap[it]?.genreName }
                        else -> emptyList()
                    }
                    baseUi.copy(genres = genresNames)
                }

                filterRatedList()
                _ratedUiState.update { it.copy(isLoading = false) }
            } catch (e: Throwable) {
                _ratedUiState.update { it.copy(error = listOf(Error("")), isLoading = false) }
            }
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
        filterRatedList()
    }

    private fun filterRatedList() {
        val filtered = allRatedList.filter { it.mediaType == _currentTab.value }
        _ratedUiState.update { it.copy(ratedList = filtered) }
    }

    override fun onClickMovie(movieId: Int) {
        ratedUiState.value.ratedList.find { it.id == movieId }?.let { item ->
            if (item.mediaType == Constants.MOVIE) {
                _myRatingUIEvent.update { Event(MyRatingUIEvent.MovieEvent(movieId)) }
            } else {
                _myRatingUIEvent.update { Event(MyRatingUIEvent.TVShowEvent(movieId)) }
            }
        }
    }

    fun retryConnect() {
        _ratedUiState.update { it.copy(error = emptyList()) }
        getData()
    }

    fun deleteMovie(movieId: Int) {
        viewModelScope.launch {
            try {
                deleteMovieRatingUseCase(movieId)
                allRatedList = allRatedList.filterNot { it.id == movieId && it.mediaType == Constants.MOVIE }
                filterRatedList()
            } catch (e: Throwable) {
                _ratedUiState.update { it.copy(error = listOf(Error("Failed to delete movie"))) }
            }
        }
    }

    fun deleteSeries(seriesId: Int) {
        viewModelScope.launch {
            try {
                deleteSeriesRatingUseCase(seriesId)
                allRatedList = allRatedList.filterNot { it.id == seriesId && it.mediaType == Constants.TV_SHOWS }
                filterRatedList()
            } catch (e: Throwable) {
                _ratedUiState.update { it.copy(error = listOf(Error("Failed to delete series"))) }
            }
        }
    }
}
