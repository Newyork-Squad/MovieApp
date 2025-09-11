package com.karrar.movieapp.ui.match.questions

data class MatchQuestion(
    val question: String,
    val choices: List<Choice>,
    val type: MatchQuestionType,
    val isAnswered: Boolean,
)

enum class MatchQuestionType {
    MOOD,
    GENRE,
    TIME,
    RELEASE,
}
