package com.karrar.movieapp.ui.match.questions

import com.karrar.movieapp.R
import com.karrar.movieapp.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MatchViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        MatchQuestionInteractionListener {
        private val _uiState = MutableStateFlow(MatchQuestionUiState())
        val uiState = _uiState.asStateFlow()

        private val _questions =
            MutableStateFlow<List<MatchQuestion>>(emptyList())
        val questions = _questions.asStateFlow()

        private val items = mutableListOf<MatchQuestion>()

        override fun onNextClicked() {
            if (_uiState.value.currentQuestionType == MatchQuestionType.RELEASE) {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                return
            }
            val nextType =
                when (_uiState.value.currentQuestionType) {
                    MatchQuestionType.MOOD -> MatchQuestionType.GENRE
                    MatchQuestionType.GENRE -> MatchQuestionType.TIME
                    MatchQuestionType.TIME -> MatchQuestionType.RELEASE
                    MatchQuestionType.RELEASE -> return
                }

            _questions.update { currentList ->
                val updatedList =
                    currentList
                        .map { q ->
                            if (q.type == _uiState.value.currentQuestionType) {
                                val selectedChoices = getSelectedChoices(q.type)
                                q.copy(
                                    choices = selectedChoices,
                                    isAnswered = true,
                                )
                            } else {
                                q
                            }
                        }.toMutableList()

                // append next question if it's not already there
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
                )
            }
        }

        override fun getSelectedChoices(type: MatchQuestionType): List<Choice> =
            when (type) {
                MatchQuestionType.MOOD -> _uiState.value.moodSelected
                MatchQuestionType.GENRE -> _uiState.value.genreSelected
                MatchQuestionType.TIME -> _uiState.value.mediaTimeDurationSelected
                MatchQuestionType.RELEASE -> _uiState.value.releaseSelected
            }

        override fun getData() {
            val mood =
                listOf(
                    Choice(name = "Chill", icon = R.drawable.due_tone_headphone),
                    Choice(name = "Excited", icon = R.drawable.due_tone_flame),
                    Choice(name = "Emotional", icon = R.drawable.due_tone_heart),
                    Choice(name = "Curious", icon = R.drawable.due_tone_search),
                )
            val genre =
                listOf(
                    Choice(name = "Action"),
                    Choice(name = "Comedy"),
                    Choice(name = "Drama"),
                    Choice(name = "Romance"),
                    Choice(name = "Sci-Fi"),
                    Choice(name = "Thriller"),
                    Choice(name = "Animation"),
                    Choice(name = "Mystery"),
                )
            val time =
                listOf(
                    Choice(
                        name = "Short",
                        description = "(Under 90 min)",
                        icon = R.drawable.due_tone_time_short,
                    ),
                    Choice(
                        name = "Medium",
                        description = "(between 90 & 120 min)",
                        icon = R.drawable.due_tone_time_medium,
                    ),
                    Choice(
                        name = "Long",
                        description = "(Over 120 min)",
                        icon = R.drawable.due_tone_time_long,
                    ),
                )
            val release =
                listOf(
                    Choice(name = "Recent"),
                    Choice(name = "Classic"),
                    Choice(name = "Both"),
                )
            items.addAll(
                listOf(
                    MatchQuestion(
                        question = "What mood are you in?",
                        type = MatchQuestionType.MOOD,
                        choices = mood,
                        isAnswered = false,
                    ),
                    MatchQuestion(
                        question = "What genre are you in?",
                        type = MatchQuestionType.GENRE,
                        choices = genre,
                        isAnswered = false,
                    ),
                    MatchQuestion(
                        question = "What time of day are you in?",
                        type = MatchQuestionType.TIME,
                        choices = time,
                        isAnswered = false,
                    ),
                    MatchQuestion(
                        question = "What year are you in?",
                        type = MatchQuestionType.RELEASE,
                        choices = release,
                        isAnswered = false,
                    ),
                ),
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
                        state.copy(moodSelected = choices)
                    }

                    MatchQuestionType.GENRE -> {
                        state.copy(genreSelected = choices)
                    }

                    MatchQuestionType.TIME -> {
                        state.copy(mediaTimeDurationSelected = choices)
                    }

                    MatchQuestionType.RELEASE -> {
                        state.copy(releaseSelected = choices)
                    }
                }
            }
        }

        override fun getCurrentQuestionType(): MatchQuestionType = _uiState.value.currentQuestionType
    }
