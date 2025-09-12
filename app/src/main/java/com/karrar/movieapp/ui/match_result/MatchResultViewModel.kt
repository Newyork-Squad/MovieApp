package com.karrar.movieapp.ui.match_result

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood
import com.karrar.movieapp.domain.enums.Runtime
import com.karrar.movieapp.domain.usecases.match.GetMatchingMoviesUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.GetMovieDetailsUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.match_result.mappers.ActorUIStateMapper
import com.karrar.movieapp.ui.match_result.mappers.CrewUIStateMapper
import com.karrar.movieapp.ui.match_result.mappers.MediaUIStateMapper
import com.karrar.movieapp.ui.match_result.mappers.MovieDetailsUIStateMapper
import com.karrar.movieapp.ui.match_result.mappers.ReviewUIStateMapper
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
class MatchResultViewModel @Inject constructor(
    private val getMatchingMoviesUseCase: GetMatchingMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val movieDetailsUIStateMapper: MovieDetailsUIStateMapper,
    private val actorUIStateMapper: ActorUIStateMapper,
    private val mediaUIStateMapper: MediaUIStateMapper,
    private val reviewUIStateMapper: ReviewUIStateMapper,
    private val crewUIStateMapper: CrewUIStateMapper,
    state: SavedStateHandle
) : BaseViewModel(), MatchResultInteractionListener {

    private val _uiState = MutableStateFlow(MatchResultUiState())
    val uiState: StateFlow<MatchResultUiState> = _uiState.asStateFlow()

    private val _matchResultUiEvent = MutableStateFlow<Event<MatchResultUiEvent?>>(Event(null))
    val matchResultUiEvent = _matchResultUiEvent.asStateFlow()


    fun getMatchingMovies(
        moods: List<Mood>,
        genres: List<MatchingGenre>,
        runtime: Runtime,
        era: Era
    ) {
        _uiState.update { it.copy(isLoading = true, error = emptyList()) }
        viewModelScope.launch {
            try {
                val matchingMovies = getMatchingMoviesUseCase(moods, genres, runtime, era)
                val detailedMovies = matchingMovies.map { movie ->
                    getMovieDetails(movie.mediaID)
                }
                _uiState.update {
                    it.copy(
                        movies = detailedMovies,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = listOf(
                            MatchResultUiState.ErrorUIState(
                                code = Constants.INTERNET_STATUS,
                                message = e.message.toString()
                            )
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }


    private suspend fun getMovieDetails(movieId: Int): MatchResultUiState.MovieUIState {
        val details = getMovieDetailsUseCase.getMovieDetails(movieId)
        val cast = getMovieDetailsUseCase.getMovieCast(movieId)
        val crew = getMovieDetailsUseCase.getMovieCrew(movieId)
        val similar = getMovieDetailsUseCase.getSimilarMovie(movieId)
        val reviews = getMovieDetailsUseCase.getMovieReviews(movieId)

        return MatchResultUiState.MovieUIState(
            movieDetailsResult = movieDetailsUIStateMapper.map(details),
            movieCastResult = cast.map { actorUIStateMapper.map(it) },
            movieCrewResult = crew.map { crewUIStateMapper.map(it) }.take(8),
            similarMoviesResult = similar.map { mediaUIStateMapper.map(it) },
            movieReview = reviews.reviews.map { reviewUIStateMapper.map(it) }
        )
    }


    override fun getData() {
    }

    override fun onClickViewDetails() {
        _matchResultUiEvent.update { Event(MatchResultUiEvent.ViewMovieDetails) }
    }

    override fun onClickBack() {
        _matchResultUiEvent.update { Event(MatchResultUiEvent.NavigateBack) }
    }

    override fun onClickYoutubeTrailer(movieId: Int) {
        _matchResultUiEvent.update { Event(MatchResultUiEvent.PlayYoutubeTrailer(movieId)) }
    }

    override fun onClickSaveMovie(movieId: Int) {
        _matchResultUiEvent.update { Event(MatchResultUiEvent.SaveMovie(movieId)) }
    }
}