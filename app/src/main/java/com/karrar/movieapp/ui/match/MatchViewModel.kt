package com.karrar.movieapp.ui.match

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.karrar.movieapp.R
import com.karrar.movieapp.domain.enums.Era
import com.karrar.movieapp.domain.enums.MatchingGenre
import com.karrar.movieapp.domain.enums.Mood
import com.karrar.movieapp.domain.enums.Runtime
import com.karrar.movieapp.domain.usecases.GetSessionIDUseCase
import com.karrar.movieapp.domain.usecases.match.GetMatchingMoviesUseCase
import com.karrar.movieapp.domain.usecases.movieDetails.GetMovieDetailsUseCase
import com.karrar.movieapp.ui.base.BaseViewModel
import com.karrar.movieapp.ui.match.questions.Choice
import com.karrar.movieapp.ui.match.questions.MatchQuestion
import com.karrar.movieapp.ui.match.questions.MatchQuestionInteractionListener
import com.karrar.movieapp.ui.match.questions.MatchQuestionType
import com.karrar.movieapp.ui.match.questions.mapChoicesToEra
import com.karrar.movieapp.ui.match.questions.mapChoicesToGenres
import com.karrar.movieapp.ui.match.questions.mapChoicesToMoods
import com.karrar.movieapp.ui.match.questions.mapChoicesToRuntime
import com.karrar.movieapp.ui.match.result.MatchResultInteractionListener
import com.karrar.movieapp.ui.match.result.MatchUiEvent
import com.karrar.movieapp.ui.match.result.mappers.ActorUIStateMapper
import com.karrar.movieapp.ui.match.result.mappers.CrewUIStateMapper
import com.karrar.movieapp.ui.match.result.mappers.MediaUIStateMapper
import com.karrar.movieapp.ui.match.result.mappers.MovieDetailsUIStateMapper
import com.karrar.movieapp.ui.match.result.mappers.ReviewUIStateMapper
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel
    @Inject
    constructor(
        private val getMatchingMoviesUseCase: GetMatchingMoviesUseCase,
        private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
        private val movieDetailsUIStateMapper: MovieDetailsUIStateMapper,
        private val actorUIStateMapper: ActorUIStateMapper,
        private val mediaUIStateMapper: MediaUIStateMapper,
        private val reviewUIStateMapper: ReviewUIStateMapper,
        private val crewUIStateMapper: CrewUIStateMapper,
        private val sessionIDUseCase: GetSessionIDUseCase,
        @ApplicationContext private val context: Context
    ) : BaseViewModel(),
        MatchQuestionInteractionListener,
        MatchResultInteractionListener {
        private val _uiState = MutableStateFlow(MatchUiState())
        val uiState = _uiState.asStateFlow()

        private val _questions = MutableStateFlow<List<MatchQuestion>>(emptyList())
        val questions = _questions.asStateFlow()

        private val _matchResultUiEvent = MutableStateFlow<Event<MatchUiEvent?>>(Event(null))
        val matchResultUiEvent = _matchResultUiEvent.asStateFlow()

        private val items = mutableListOf<MatchQuestion>()

        override fun onNextClicked() {
            if (getSelectedChoices(_uiState.value.currentQuestionType).isEmpty() || _uiState.value.isLoading) return
            if (_uiState.value.currentQuestionType == MatchQuestionType.TIME_PERIOD) {
                _uiState.update {
                    it.copy(isLoading = true, isLastQuestion = true)
                }
                _questions.update { currentList ->
                    currentList.map { q ->
                        if (q.type == _uiState.value.currentQuestionType) {
                            val selectedChoices = getSelectedChoices(q.type)
                            q.copy(
                                choices = selectedChoices.map { it.copy(isSelected = true) },
                                isAnswered = true,
                            )
                        } else {
                            q
                        }
                    }
                }
                onStartMatchingClicked()
                return
            }
            val nextType =
                when (_uiState.value.currentQuestionType) {
                    MatchQuestionType.MOOD -> MatchQuestionType.GENRE
                    MatchQuestionType.GENRE -> MatchQuestionType.MEDIA_RUNTIME
                    MatchQuestionType.MEDIA_RUNTIME -> MatchQuestionType.TIME_PERIOD
                    MatchQuestionType.TIME_PERIOD -> return
                }

            _questions.update { currentList ->
                val updatedList =
                    currentList
                        .map { q ->
                            if (q.type == _uiState.value.currentQuestionType) {
                                val selectedChoices = getSelectedChoices(q.type)
                                q.copy(
                                    choices = selectedChoices.map { it.copy(isSelected = true) },
                                    isAnswered = true,
                                )
                            } else {
                                q
                            }
                        }.toMutableList()

                val nextQuestion = items.firstOrNull { it.type == nextType }
                if (nextQuestion != null && updatedList.none { it.type == nextType }) {
                    updatedList.add(nextQuestion)
                }

                updatedList
            }

            _uiState.update { state ->
                state.copy(
                    currentQuestionType = nextType,
                    progress = state.progress + 25,
                    isLastQuestion = nextType == MatchQuestionType.TIME_PERIOD,
                )
            }
        }

        private fun getSelectedChoices(type: MatchQuestionType): List<Choice> =
            when (type) {
                MatchQuestionType.MOOD -> _uiState.value.moodSelected
                MatchQuestionType.GENRE -> _uiState.value.genreSelected
                MatchQuestionType.MEDIA_RUNTIME -> _uiState.value.mediaRuntimeSelected
                MatchQuestionType.TIME_PERIOD -> _uiState.value.timePeriodSelected
            }

    override fun getData() {
        val mood = listOf(
            Choice(name = context.getString(R.string.mood_chill), icon = R.drawable.due_tone_headphone),
            Choice(name = context.getString(R.string.mood_excited), icon = R.drawable.due_tone_flame),
            Choice(name = context.getString(R.string.mood_emotional), icon = R.drawable.due_tone_heart),
            Choice(name = context.getString(R.string.mood_curious), icon = R.drawable.due_tone_search),
        )
        val genre = listOf(
            Choice(name = context.getString(R.string.genre_action)),
            Choice(name = context.getString(R.string.genre_comedy)),
            Choice(name = context.getString(R.string.genre_drama)),
            Choice(name = context.getString(R.string.genre_romance)),
            Choice(name = context.getString(R.string.genre_scifi)),
            Choice(name = context.getString(R.string.genre_thriller)),
            Choice(name = context.getString(R.string.genre_animation)),
            Choice(name = context.getString(R.string.genre_mystery)),
        )
        val time = listOf(
            Choice(
                name = context.getString(R.string.time_short),
                description = context.getString(R.string.time_short_desc),
                icon = R.drawable.due_tone_time_short,
            ),
            Choice(
                name = context.getString(R.string.time_medium),
                description = context.getString(R.string.time_medium_desc),
                icon = R.drawable.due_tone_time_medium,
            ),
            Choice(
                name = context.getString(R.string.time_long),
                description = context.getString(R.string.time_long_desc),
                icon = R.drawable.due_tone_time_long,
            ),
        )
        val release = listOf(
            Choice(name = context.getString(R.string.release_recent)),
            Choice(name = context.getString(R.string.release_classic)),
            Choice(name = context.getString(R.string.release_both)),
        )

        items.addAll(
            listOf(
                MatchQuestion(
                    question = context.getString(R.string.question_mood),
                    type = MatchQuestionType.MOOD,
                    choices = mood,
                    isAnswered = false,
                ),
                MatchQuestion(
                    question = context.getString(R.string.question_genre),
                    type = MatchQuestionType.GENRE,
                    choices = genre,
                    isAnswered = false,
                ),
                MatchQuestion(
                    question = context.getString(R.string.question_time),
                    type = MatchQuestionType.MEDIA_RUNTIME,
                    choices = time,
                    isAnswered = false,
                ),
                MatchQuestion(
                    question = context.getString(R.string.question_release),
                    type = MatchQuestionType.TIME_PERIOD,
                    choices = release,
                    isAnswered = false,
                ),
            )
        )

        _questions.update {
            listOf(items.first())
        }
    }
        fun onChoiceSelected(
            type: MatchQuestionType,
            choices: List<Choice>,
        ) {
            _uiState.update { state ->
                when (type) {
                    MatchQuestionType.MOOD -> {
                        state.copy(
                            moodSelected = choices.map { it.copy(isSelected = true) },
                        )
                    }

                    MatchQuestionType.GENRE -> {
                        state.copy(
                            genreSelected = choices.map { it.copy(isSelected = true) },
                        )
                    }

                    MatchQuestionType.MEDIA_RUNTIME -> {
                        state.copy(
                            mediaRuntimeSelected =
                                choices.mapIndexed { index, choice ->
                                    choice.copy(isSelected = index == 0)
                                },
                        )
                    }

                    MatchQuestionType.TIME_PERIOD -> {
                        state.copy(
                            timePeriodSelected =
                                choices.mapIndexed { index, choice ->
                                    choice.copy(isSelected = index == 0)
                                },
                        )
                    }
                }
            }
        }

        override fun getCurrentQuestionType(): MatchQuestionType = _uiState.value.currentQuestionType

        override fun onStartMatchingClicked() {
            val moods = mapChoicesToMoods(_uiState.value.moodSelected)
            val genres = mapChoicesToGenres(_uiState.value.genreSelected)
            val runtime = mapChoicesToRuntime(_uiState.value.mediaRuntimeSelected) ?: Runtime.MEDIUM
            val era = mapChoicesToEra(_uiState.value.timePeriodSelected) ?: Era.BOTH
            getMatchingMovies(moods, genres, runtime, era)
        }

        fun getMatchingMovies(
            moods: List<Mood>,
            genres: List<MatchingGenre>,
            runtime: Runtime,
            era: Era,
        ) {
            _uiState.update { it.copy(isLoading = true, error = emptyList()) }
            viewModelScope.launch {
                try {
                    val matchingMovies = getMatchingMoviesUseCase(moods, genres, runtime, era)
                    val detailedMovies =
                        matchingMovies.map { movie ->
                            getMovieDetails(movie.mediaID)
                        }
                    if (detailedMovies.isNotEmpty()) {
                        _matchResultUiEvent.update { Event(MatchUiEvent.NavigateToResults) }
                    } else {
                        _matchResultUiEvent.update { Event(MatchUiEvent.ShowNoMoviesToast) }
                    }

                    _uiState.update {
                        MatchUiState(
                            movies = detailedMovies,
                            isLoading = false,
                            selectedMovieIndex = 0,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error =
                                listOf(
                                    MatchUiState.ErrorUIState(
                                        code = Constants.INTERNET_STATUS,
                                        message = e.message.toString(),
                                    ),
                                ),
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun updateSelectedMovie(index: Int) {
            _uiState.update { it.copy(selectedMovieIndex = index) }
        }

        private suspend fun getMovieDetails(movieId: Int): MatchUiState.MovieUIState {
            val details = getMovieDetailsUseCase.getMovieDetails(movieId)
            val cast = getMovieDetailsUseCase.getMovieCast(movieId)
            val crew = getMovieDetailsUseCase.getMovieCrew(movieId)
            val similar = getMovieDetailsUseCase.getSimilarMovie(movieId)
            val reviews = getMovieDetailsUseCase.getMovieReviews(movieId)

            return MatchUiState.MovieUIState(
                id = movieId,
                movieDetailsResult = movieDetailsUIStateMapper.map(details),
                movieCastResult = cast.map { actorUIStateMapper.map(it) },
                movieCrewResult = crew.map { crewUIStateMapper.map(it) }.take(8),
                similarMoviesResult = similar.map { mediaUIStateMapper.map(it) },
                movieReview = reviews.reviews.map { reviewUIStateMapper.map(it) },
            )
        }

        override fun onClickBack() {
            _matchResultUiEvent.update { Event(MatchUiEvent.NavigateBack) }
        }

        override fun onClickYoutubeTrailer() {
            val currentMovieIndex = _uiState.value.selectedMovieIndex
            _matchResultUiEvent.update { Event(MatchUiEvent.PlayYoutubeTrailer(_uiState.value.movies[currentMovieIndex].id)) }
        }

        override fun onClickSaveMovie() {
            if (sessionIDUseCase().isNullOrEmpty()) {
                showLoginDialog()
            } else {
                val currentMovieIndex = _uiState.value.selectedMovieIndex
                _matchResultUiEvent.update { Event(MatchUiEvent.SaveMovie(_uiState.value.movies[currentMovieIndex].id)) }
            }
        }

        private fun showLoginDialog() {
            _matchResultUiEvent.update { Event(MatchUiEvent.ShowLoginDialogEvent) }
        }

        override fun onClickViewDetails() {
            val currentMovieIndex = _uiState.value.selectedMovieIndex
            _matchResultUiEvent.update { Event(MatchUiEvent.ViewMovieDetails(_uiState.value.movies[currentMovieIndex].id)) }
        }
    }
