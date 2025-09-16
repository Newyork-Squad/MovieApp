package com.karrar.movieapp.ui.match.questions

import com.karrar.movieapp.ui.base.BaseInteractionListener

interface MatchQuestionInteractionListener : BaseInteractionListener {

    fun onNextClicked()

    fun getCurrentQuestionType(): MatchQuestionType
}
