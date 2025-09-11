package com.karrar.movieapp.ui.match.questions

import com.karrar.movieapp.ui.base.BaseInteractionListener

interface MatchQuestionInteractionListener : BaseInteractionListener {
    fun onChoiceSelected(choice: Choice)

    fun onNextClicked()

    fun getSelectedChoices(type: MatchQuestionType): List<Choice>

    fun getCurrentQuestionType(): MatchQuestionType
}
