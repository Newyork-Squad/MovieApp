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

        override fun onChoiceSelected(choice: Choice) {
            val currentType = _uiState.value.currentQuestionType
            _uiState.update { state ->
                val updatedQuestions =
                    state.questions.map { q ->
                        if (q.type == currentType) {
                            q.copy(
                                choices =
                                    q.choices.map {
                                        if (it.name == choice.name) {
                                            it.copy(isSelected = !it.isSelected)
                                        } else {
                                            it
                                        }
                                    },
                            )
                        } else {
                            q
                        }
                    }
                state.copy(questions = updatedQuestions)
            }
        }

        override fun onNextClicked() {
            _uiState.update { state ->
                val nextType =
                    when (state.currentQuestionType) {
                        MatchQuestionType.MOOD -> MatchQuestionType.GENRE
                        MatchQuestionType.GENRE -> MatchQuestionType.TIME
                        MatchQuestionType.TIME -> MatchQuestionType.RELEASE
                        MatchQuestionType.RELEASE -> return
                    }

                val resetQuestions =
                    state.questions.map { q ->
                        if (q.type == nextType) {
                            q.copy(
                                choices = q.choices.map { it.copy(isSelected = false) },
                                isAnswered = true,
                            )
                        } else {
                            q
                        }
                    }

                state.copy(
                    currentQuestionType = nextType,
                    questions = resetQuestions,
                    progress = state.progress + 25,
                )
            }
        }

    override fun getSelectedChoices(type: MatchQuestionType): List<Choice> {
        TODO("Not yet implemented")
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
            val items =
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
                )
            _uiState.update {
                it.copy(questions = items)
            }
        }

        fun onChoiceSelected(
            type: MatchQuestionType,
            choice: List<Choice>,
        ) {
            _uiState.update { state ->
                val updatedQuestion =
                    state.questions.map {
                        if (it.type == type) {
                            it.copy(choices = choice)
                        } else {
                            it
                        }
                    }
                when (type) {
                    MatchQuestionType.MOOD -> {
                        state.copy(
                            questions = updatedQuestion,
                            moodSelected = choice,
                        )
                    }

                    MatchQuestionType.GENRE -> {
                        state.copy(
                            questions = updatedQuestion,
                            genreSelected = choice,
                        )
                    }

                    MatchQuestionType.TIME -> {
                        state.copy(
                            questions = updatedQuestion,
                            mediaTimeDurationSelected = choice,
                        )
                    }

                    MatchQuestionType.RELEASE -> {
                        state.copy(
                            questions = updatedQuestion,
                            releaseSelected = choice,
                        )
                    }
                }
            }
        }

        override fun getCurrentQuestionType(): MatchQuestionType = _uiState.value.currentQuestionType
    }
