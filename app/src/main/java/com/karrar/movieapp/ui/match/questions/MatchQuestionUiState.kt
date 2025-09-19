package com.karrar.movieapp.ui.match.questions

data class MatchQuestionUiState(
    val currentQuestionType: MatchQuestionType = MatchQuestionType.MOOD,
    val moodSelected: List<Choice> = emptyList(),
    val genreSelected: List<Choice> = emptyList(),
    val mediaRuntimeSelected: List<Choice> = emptyList(),
    val timePeriodSelected: List<Choice> = emptyList(),
    val isLoading: Boolean = false,
    val progress: Int = 25,
)
